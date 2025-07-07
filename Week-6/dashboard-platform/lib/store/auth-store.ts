import { create } from 'zustand'
import { persist } from 'zustand/middleware'

export interface User {
  id: string
  email: string
  fullName: string
  role: string
  profilePicture?: string
  preferences?: any
}

export interface AuthState {
  user: User | null
  isAuthenticated: boolean
  token: string | null
  refreshToken: string | null
  
  // Actions
  login: (user: User, token: string, refreshToken: string) => void
  logout: () => void
  updateUser: (user: Partial<User>) => void
  setToken: (token: string) => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      isAuthenticated: false,
      token: null,
      refreshToken: null,

      login: (user: User, token: string, refreshToken: string) => {
        set({
          user,
          isAuthenticated: true,
          token,
          refreshToken,
        })
      },

      logout: () => {
        set({
          user: null,
          isAuthenticated: false,
          token: null,
          refreshToken: null,
        })
      },

      updateUser: (userData: Partial<User>) => {
        const currentUser = get().user
        if (currentUser) {
          set({
            user: { ...currentUser, ...userData },
          })
        }
      },

      setToken: (token: string) => {
        set({ token })
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        user: state.user,
        isAuthenticated: state.isAuthenticated,
        token: state.token,
        refreshToken: state.refreshToken,
      }),
    }
  )
) 