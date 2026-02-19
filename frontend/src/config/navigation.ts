export type Role = "HR" | "MANAGER" | "EMPLOYEE";
 
export interface NavItem {
  label: string;
  path: string;
}
 
export const navigation: Record<Role, NavItem[]> = {
  HR: [
    { label: "Dashboard", path: "/dashboard" },
    { label: "Travel Plans", path: "/dashboard/travel" },
    { label: "Expense Review", path: "/dashboard/expenses/review" },
    { label: "Jobs", path: "/dashboard/jobs" },
    {label: "Referrals", path: "/dashboard/referrals/review"},
    {label:"My Referrals", path: "/dashboard/referrals"},
    { label: "Organization", path: "/dashboard/org" },
    { label: "Games", path: "/dashboard/games" },
  ],
 
  MANAGER: [
    { label: "Dashboard", path: "/dashboard" },
    { label: "My Travels", path: "/dashboard/travel" },
    { label: "My Expense", path: "/dashboard/expenses" },
    { label: "Jobs", path: "/dashboard/jobs" },
    {label:"My Referrals", path: "/dashboard/referrals"},
    { label: "Organization", path: "/dashboard/org" },
  ],
 
  EMPLOYEE: [
    { label: "Dashboard", path: "/dashboard" },
    { label: "My Travels", path: "/dashboard/travel" },
    { label: "My Expenses", path: "/dashboard/expenses" },
    { label: "Jobs", path: "/dashboard/jobs" },
    {label:"My Referrals", path: "/dashboard/referrals"},
    { label: "Games", path: "/dashboard/games" },
  ],
};