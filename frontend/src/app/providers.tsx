import { Provider } from "react-redux";
import { store } from "../store/store";
import { type ReactNode } from "react";
import { useAuthInit } from "../modules/auth/hooks/useAuthInit";
 
function Init({ children }: { children: ReactNode }) {
  useAuthInit();
  return <>{children}</>;
}
 
export default function AppProviders({ children }: { children: ReactNode }) {
  return (
    <Provider store={store}>
      <Init>{children}</Init>
    </Provider>
  );
}