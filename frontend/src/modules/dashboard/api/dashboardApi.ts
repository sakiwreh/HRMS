import api from "../../../lib/axios"

export const fetchTravelCount = async () => {
    const res = await api.get("/dashboard/travel-count")
    return res.data;
}

export const fetchPendingCount = async () => {
    const res = await api.get("/dashboard/expense-count")
    return res.data;
}

export const fetchActiveJobCount = async () => {
    const res = await api.get("/dashboard/active-job-count")
    return res.data;
}

export const fetchTodayMatches = async () => {
    const res = await api.get("/dashboard/upcoming-matches")
    return res.data;
}