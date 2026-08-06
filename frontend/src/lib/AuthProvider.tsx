"use client";

import { ReactNode, useSyncExternalStore } from "react";
import { create } from "zustand";
import { persist } from "zustand/middleware";

interface DecodedToken {
  sub?: string;
  userId?: string;
  exp?: number;
}

interface AuthState {
  token: string | null;
  // Le backend n'expose pas de GET /advertisers/user/{userId} (contrairement à owner/mediabuyer),
  // donc l'advertiserId créé via "devenir annonceur" doit être mémorisé côté client.
  advertiserId: string | null;
  // Idem : BillboardController n'a aucun endpoint "lister les billboards d'un propriétaire",
  // donc on mémorise les ids créés pour reconstituer "mes billboards" via GET /billboards/{id}.
  myBillboardIds: string[];
  setToken: (token: string | null) => void;
  setAdvertiserId: (advertiserId: string | null) => void;
  addBillboardId: (billboardId: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      advertiserId: null,
      myBillboardIds: [],
      setToken: (token) => set({ token }),
      setAdvertiserId: (advertiserId) => set({ advertiserId }),
      addBillboardId: (billboardId) =>
        set((state) => ({ myBillboardIds: [...state.myBillboardIds, billboardId] })),
      logout: () => set({ token: null, advertiserId: null, myBillboardIds: [] }),
    }),
    { name: "billboard-auth" },
  ),
);

function decodeToken(token: string): DecodedToken | null {
  try {
    const payload = token.split(".")[1];
    const json = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
    return JSON.parse(json);
  } catch {
    return null;
  }
}

const noopSubscribe = () => () => {};

/**
 * zustand-persist ne peut lire localStorage qu'après le montage côté client : le rendu
 * serveur doit donc rester "non hydraté" pour éviter un mismatch d'hydratation React.
 * useSyncExternalStore avec des snapshots serveur/client différents est la façon
 * recommandée d'exprimer ça (contrairement à un useEffect + setState).
 */
function useHydrated() {
  return useSyncExternalStore(
    noopSubscribe,
    () => true,
    () => false,
  );
}

export function AuthProvider({ children }: { children: ReactNode }) {
  return <>{children}</>;
}

export function useAuth() {
  const hydrated = useHydrated();
  const token = useAuthStore((state) => state.token);
  const advertiserId = useAuthStore((state) => state.advertiserId);
  const myBillboardIds = useAuthStore((state) => state.myBillboardIds);
  const setToken = useAuthStore((state) => state.setToken);
  const setAdvertiserId = useAuthStore((state) => state.setAdvertiserId);
  const addBillboardId = useAuthStore((state) => state.addBillboardId);
  const logout = useAuthStore((state) => state.logout);

  const decoded = token ? decodeToken(token) : null;

  return {
    token: hydrated ? token : null,
    userId: hydrated ? decoded?.userId ?? null : null,
    email: hydrated ? decoded?.sub ?? null : null,
    advertiserId: hydrated ? advertiserId : null,
    myBillboardIds: hydrated ? myBillboardIds : [],
    isAuthenticated: hydrated && !!token,
    hydrated,
    login: setToken,
    setAdvertiserId,
    addBillboardId,
    logout,
  };
}
