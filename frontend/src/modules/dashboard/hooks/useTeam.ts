import { useQuery } from "@tanstack/react-query";
import { fetchTeamExpenses, fetchTeamMembers, fetchTeamTravels } from "../api/teamApi";

export function useTeamMembers(){
    return useQuery({
        queryKey:["team-members"], queryFn:fetchTeamMembers, staleTime:60_000,
    });
}

export function useTeamTravels(){
    return useQuery({
        queryKey:["team-travels"], queryFn:fetchTeamTravels, staleTime: 60_000,
    });
}

export function useTeamExpenses(){
    return useQuery({
        queryKey:["team-expenses"], queryFn:fetchTeamExpenses, staleTime:60_000
    });
}