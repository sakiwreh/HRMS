import api from "../../../lib/axios";

export type SocialVisibility = "ALL" | "DEPARTMENT" | "MANAGER_ONLY";
export type SocialCelebrationType = "BIRTHDAY" | "WORK_ANNIVERSARY";

export interface SocialActorResponse {
  id: number;
  name: string;
  profilePath?: string | null;
}

export interface SocialCommentResponse {
  id: number;
  postId: number;
  author: SocialActorResponse;
  text: string;
  createdAt: string;
  updatedAt: string;
}

export interface SocialPostImageResponse {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  url: string;
}

export interface SocialPostResponse {
  id: number;
  author: SocialActorResponse;
  title: string;
  description: string;
  tags: string[];
  visibility: SocialVisibility;
  systemGenerated: boolean;
  systemPostType: SocialCelebrationType | null;
  likeCount: number;
  commentCount: number;
  createdAt: string;
  updatedAt: string;
  images: SocialPostImageResponse[];
  recentComments: SocialCommentResponse[];
}

export interface SpringPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface SocialFeedParams {
  authorId?: number;
  tag?: string;
  from?: string;
  to?: string;
  page?: number;
  size?: number;
  sort?: string;
}

export interface SocialPostCreateRequest {
  title: string;
  description: string;
  visibility?: SocialVisibility;
  tags?: string[];
}

export interface SocialPostCreateInput {
  payload: SocialPostCreateRequest;
  images?: File[];
}

export interface SocialPostUpdateRequest {
  title?: string;
  description?: string;
  visibility?: SocialVisibility;
  tags?: string[];
}

export interface SocialCommentCreateRequest {
  text: string;
}

export interface SocialCommentUpdateRequest {
  text: string;
}

const toAbsoluteUrl = (url: string): string => {
  if (!url) return "";
  if (/^https?:\/\//i.test(url)) return url;
  const base = String(api.defaults.baseURL ?? "").replace(/\/$/, "");
  const path = url.replace(/^\//, "");
  return base ? `${base}/${path}` : `/${path}`;
};

const normalizePost = (post: SocialPostResponse): SocialPostResponse => ({
  ...post,
  images: (post.images ?? []).map((image) => ({
    ...image,
    url: toAbsoluteUrl(image.url),
  })),
  recentComments: post.recentComments ?? [],
});

export const fetchSocialFeed = async (
  params: SocialFeedParams
): Promise<SpringPage<SocialPostResponse>> => {
  const response = await api.get("/social/posts", { params });
  return {
    ...response.data,
    content: (response.data.content ?? []).map(normalizePost),
  };
};

export const fetchSocialPost = async (
  postId: number
): Promise<SocialPostResponse> => {
  const response = await api.get(`/social/posts/${postId}`);
  return normalizePost(response.data);
};

export const createSocialPost = async (
  input: SocialPostCreateInput
): Promise<SocialPostResponse> => {
  const images = input.images ?? [];
  if (images.length === 0) {
    const response = await api.post("/social/posts", input.payload);
    return normalizePost(response.data);
  }

  const formData = new FormData();
  formData.append(
    "payload",
    new Blob([JSON.stringify(input.payload)], { type: "application/json" })
  );
  images.forEach((file) => {
    formData.append("images", file);
  });

  const response = await api.post("/social/posts", formData);
  return normalizePost(response.data);
};

export const updateSocialPost = async (
  postId: number,
  payload: SocialPostUpdateRequest
): Promise<SocialPostResponse> => {
  const response = await api.patch(`/social/posts/${postId}`, payload);
  return normalizePost(response.data);
};

export const deleteSocialPost = async (
  postId: number,
  reason?: string
): Promise<void> => {
  await api.delete(`/social/posts/${postId}`, { params: { reason } });
};

export const likeSocialPost = async (postId: number): Promise<void> => {
  await api.post(`/social/posts/${postId}/likes`);
};

export const unlikeSocialPost = async (postId: number): Promise<void> => {
  await api.delete(`/social/posts/${postId}/likes`);
};

export const fetchSocialComments = async (
  postId: number,
  page = 0,
  size = 20
): Promise<SpringPage<SocialCommentResponse>> => {
  const response = await api.get(`/social/posts/${postId}/comments`, {
    params: { page, size, sort: "createdAt,desc" },
  });
  return response.data;
};

export const addSocialComment = async (
  postId: number,
  payload: SocialCommentCreateRequest
): Promise<SocialCommentResponse> => {
  const response = await api.post(`/social/posts/${postId}/comments`, payload);
  return response.data;
};

export const updateSocialComment = async (
  commentId: number,
  payload: SocialCommentUpdateRequest
): Promise<SocialCommentResponse> => {
  const response = await api.patch(`/social/comments/${commentId}`, payload);
  return response.data;
};

export const deleteSocialComment = async (
  commentId: number,
  reason?: string
): Promise<void> => {
  await api.delete(`/social/comments/${commentId}`, { params: { reason } });
};

export const runDailySocialCelebrations = async (): Promise<void> => {
  await api.post("/social/celebrations/run-daily");
};