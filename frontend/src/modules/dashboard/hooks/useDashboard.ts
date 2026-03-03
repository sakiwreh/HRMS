import { useQuery } from "@tanstack/react-query"
import { fetchActiveJobCount, fetchPendingCount, fetchTodayMatches, fetchTravelCount } from "../api/dashboardApi"

export function useTotalPlans(){
    return useQuery({queryKey:["dashboard","travel-count"], queryFn:fetchTravelCount});
}

export function usePendingExpenseCount(){
    return useQuery({queryKey:["dashboard","penidng-count"],queryFn:fetchPendingCount});
}

export function useActiveJobcount(){
    return useQuery({queryKey:["dashboard","active-job-count"], queryFn: fetchActiveJobCount})
}

export function useTodayMatches(){
    return useQuery({queryKey:["dashbaord","today-matches"], queryFn:fetchTodayMatches})
}