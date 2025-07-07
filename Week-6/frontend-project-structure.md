# Frontend Project Structure - React.js/Next.js

## Project Structure Overview

```
dashboard-platform/
├── app/                                 # Next.js App Router
├── components/                          # Reusable Components
├── lib/                                # Utilities and Configurations
├── types/                              # TypeScript Definitions
├── public/                             # Static Assets
├── tests/                              # Test Files
├── docs/                               # Documentation
├── scripts/                            # Build and Deployment Scripts
└── .github/                            # GitHub Actions
```

## Detailed Folder Structure

### 1. App Router Structure (Next.js 14)

```
app/
├── (auth)/                             # Authentication Routes Group
│   ├── login/
│   │   ├── page.tsx                   # Login Page
│   │   └── loading.tsx                # Loading Component
│   ├── register/
│   │   ├── page.tsx                   # Register Page
│   │   └── loading.tsx                # Loading Component
│   └── layout.tsx                     # Auth Layout
├── (dashboard)/                        # Dashboard Routes Group
│   ├── dashboard/
│   │   ├── [id]/
│   │   │   ├── page.tsx              # Dashboard Detail Page
│   │   │   ├── loading.tsx           # Loading Component
│   │   │   ├── error.tsx             # Error Boundary
│   │   │   └── not-found.tsx         # 404 Page
│   │   ├── create/
│   │   │   └── page.tsx              # Create Dashboard Page
│   │   └── page.tsx                   # Dashboard List Page
│   ├── analytics/
│   │   └── page.tsx                   # Analytics Page
│   └── layout.tsx                     # Dashboard Layout
├── api/                                # API Routes
│   ├── auth/
│   │   ├── login/
│   │   │   └── route.ts              # Login API
│   │   └── logout/
│   │       └── route.ts              # Logout API
│   ├── dashboards/
│   │   ├── route.ts                   # Dashboard CRUD API
│   │   └── [id]/
│   │       └── route.ts              # Dashboard Detail API
│   ├── widgets/
│   │   ├── route.ts                   # Widget CRUD API
│   │   └── data/
│   │       └── route.ts              # Widget Data API
│   └── socket/
│       └── route.ts                   # Socket.io API
├── globals.css                         # Global Styles
├── layout.tsx                          # Root Layout
├── page.tsx                            # Home Page
├── loading.tsx                         # Global Loading
├── error.tsx                           # Global Error Boundary
└── not-found.tsx                       # Global 404
```

### 2. Components Structure

```
components/
├── ui/                                 # Base UI Components
│   ├── button/
│   │   ├── Button.tsx
│   │   ├── Button.test.tsx
│   │   └── index.ts
│   ├── input/
│   │   ├── Input.tsx
│   │   ├── Input.test.tsx
│   │   └── index.ts
│   ├── modal/
│   │   ├── Modal.tsx
│   │   ├── Modal.test.tsx
│   │   └── index.ts
│   ├── dropdown/
│   │   ├── Dropdown.tsx
│   │   ├── Dropdown.test.tsx
│   │   └── index.ts
│   ├── tooltip/
│   │   ├── Tooltip.tsx
│   │   ├── Tooltip.test.tsx
│   │   └── index.ts
│   └── index.ts                        # UI Components Export
├── charts/                             # Chart Components
│   ├── ChartWidget.tsx
│   ├── ChartComponent.tsx
│   ├── LineChart.tsx
│   ├── BarChart.tsx
│   ├── PieChart.tsx
│   ├── AreaChart.tsx
│   ├── ChartSkeleton.tsx
│   ├── ChartError.tsx
│   └── index.ts
├── dashboard/                          # Dashboard Components
│   ├── DashboardBuilder.tsx
│   ├── DashboardCanvas.tsx
│   ├── DashboardToolbar.tsx
│   ├── DashboardHeader.tsx
│   ├── DashboardSidebar.tsx
│   ├── DashboardSkeleton.tsx
│   └── index.ts
├── widgets/                            # Widget Components
│   ├── WidgetFactory.tsx
│   ├── WidgetLibrary.tsx
│   ├── WidgetHeader.tsx
│   ├── WidgetResizer.tsx
│   ├── DraggableWidget.tsx
│   ├── WidgetConfigPanel.tsx
│   ├── types/
│   │   ├── ChartWidget.tsx
│   │   ├── TableWidget.tsx
│   │   ├── MetricWidget.tsx
│   │   ├── TextWidget.tsx
│   │   ├── ImageWidget.tsx
│   │   └── IframeWidget.tsx
│   └── index.ts
├── collaboration/                      # Collaboration Components
│   ├── CollaborationPanel.tsx
│   ├── CursorTracker.tsx
│   ├── UserAvatar.tsx
│   ├── CommentItem.tsx
│   ├── AddCommentForm.tsx
│   ├── CollaborationSkeleton.tsx
│   └── index.ts
├── forms/                              # Form Components
│   ├── LoginForm.tsx
│   ├── RegisterForm.tsx
│   ├── DashboardForm.tsx
│   ├── WidgetConfigForm.tsx
│   ├── UserProfileForm.tsx
│   └── index.ts
├── layout/                             # Layout Components
│   ├── Header.tsx
│   ├── Sidebar.tsx
│   ├── Footer.tsx
│   ├── Navigation.tsx
│   ├── Breadcrumb.tsx
│   └── index.ts
└── index.ts                            # Main Components Export
```

### 3. Library Structure

```
lib/
├── store/                              # Zustand Stores
│   ├── dashboard-store.ts
│   ├── user-store.ts
│   ├── real-time-store.ts
│   ├── widget-store.ts
│   └── index.ts
├── hooks/                              # Custom Hooks
│   ├── useAuth.ts
│   ├── useDashboard.ts
│   ├── useWidgetData.ts
│   ├── useSocket.ts
│   ├── useLocalStorage.ts
│   ├── useDebounce.ts
│   ├── useIntersectionObserver.ts
│   └── index.ts
├── api/                                # API Functions
│   ├── auth.ts
│   ├── dashboard.ts
│   ├── widget.ts
│   ├── user.ts
│   ├── client.ts                       # API Client Configuration
│   └── index.ts
├── utils/                              # Utility Functions
│   ├── validation.ts
│   ├── formatting.ts
│   ├── constants.ts
│   ├── helpers.ts
│   ├── chart-helpers.ts
│   └── index.ts
├── validations/                        # Zod Schemas
│   ├── auth.ts
│   ├── dashboard.ts
│   ├── widget.ts
│   ├── user.ts
│   └── index.ts
├── socket/                             # Socket.io Client
│   ├── socket-client.ts
│   ├── socket-events.ts
│   ├── socket-types.ts
│   └── index.ts
├── config/                             # Configuration
│   ├── constants.ts
│   ├── environment.ts
│   ├── theme.ts
│   └── index.ts
└── index.ts                            # Main Library Export
```

### 4. Types Structure

```
types/
├── auth.ts                             # Authentication Types
├── dashboard.ts                        # Dashboard Types
├── widget.ts                           # Widget Types
├── user.ts                             # User Types
├── api.ts                              # API Response Types
├── socket.ts                           # Socket Event Types
├── chart.ts                            # Chart Configuration Types
├── common.ts                           # Common Types
└── index.ts                            # Types Export
```

### 5. Public Assets Structure

```
public/
├── icons/                              # Icon Assets
│   ├── dashboard.svg
│   ├── chart.svg
│   ├── user.svg
│   └── settings.svg
├── images/                             # Image Assets
│   ├── logo.svg
│   ├── logo-dark.svg
│   ├── placeholder.png
│   └── backgrounds/
├── fonts/                              # Custom Fonts
│   └── inter/
├── manifest.json                        # PWA Manifest
├── robots.txt                          # SEO
├── sitemap.xml                         # SEO
└── favicon.ico                         # Favicon
```

### 6. Test Structure

```
tests/
├── unit/                               # Unit Tests
│   ├── components/
│   │   ├── ui/
│   │   ├── charts/
│   │   ├── dashboard/
│   │   └── widgets/
│   ├── hooks/
│   ├── utils/
│   └── store/
├── integration/                        # Integration Tests
│   ├── api/
│   ├── socket/
│   └── dashboard/
├── e2e/                                # End-to-End Tests
│   ├── auth/
│   ├── dashboard/
│   └── collaboration/
├── fixtures/                           # Test Data
│   ├── dashboards.json
│   ├── widgets.json
│   └── users.json
├── mocks/                              # Mock Data
│   ├── api.ts
│   ├── socket.ts
│   └── store.ts
└── utils/                              # Test Utilities
    ├── render.tsx
    ├── test-utils.ts
    └── mock-providers.tsx
```

## Package.json Configuration

```json
{
  "name": "dashboard-platform",
  "version": "1.0.0",
  "private": true,
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint",
    "lint:fix": "next lint --fix",
    "type-check": "tsc --noEmit",
    "test": "jest",
    "test:watch": "jest --watch",
    "test:coverage": "jest --coverage",
    "test:e2e": "playwright test",
    "test:e2e:ui": "playwright test --ui",
    "format": "prettier --write .",
    "format:check": "prettier --check .",
    "prepare": "husky install"
  },
  "dependencies": {
    "next": "14.0.0",
    "react": "18.2.0",
    "react-dom": "18.2.0",
    "typescript": "5.2.0",
    "tailwindcss": "3.3.0",
    "zustand": "4.4.0",
    "@tanstack/react-query": "5.8.0",
    "react-hook-form": "7.47.0",
    "zod": "3.22.0",
    "chart.js": "4.4.0",
    "react-chartjs-2": "5.2.0",
    "socket.io-client": "4.7.0",
    "react-dnd": "16.0.0",
    "react-dnd-html5-backend": "16.0.0",
    "lucide-react": "0.292.0",
    "clsx": "2.0.0",
    "class-variance-authority": "0.7.0",
    "framer-motion": "10.16.0",
    "react-intersection-observer": "9.5.0",
    "react-window": "1.8.8",
    "react-virtualized-auto-sizer": "1.0.7"
  },
  "devDependencies": {
    "@types/node": "20.8.0",
    "@types/react": "18.2.0",
    "@types/react-dom": "18.2.0",
    "@typescript-eslint/eslint-plugin": "6.9.0",
    "@typescript-eslint/parser": "6.9.0",
    "eslint": "8.52.0",
    "eslint-config-next": "14.0.0",
    "eslint-config-prettier": "9.0.0",
    "eslint-plugin-prettier": "5.0.0",
    "prettier": "3.0.0",
    "jest": "29.7.0",
    "@testing-library/react": "13.4.0",
    "@testing-library/jest-dom": "6.1.0",
    "@testing-library/user-event": "14.5.0",
    "jest-environment-jsdom": "29.7.0",
    "playwright": "1.39.0",
    "husky": "8.0.0",
    "lint-staged": "14.0.0",
    "autoprefixer": "10.4.16",
    "postcss": "8.4.31"
  },
  "lint-staged": {
    "*.{js,jsx,ts,tsx}": [
      "eslint --fix",
      "prettier --write"
    ],
    "*.{json,md}": [
      "prettier --write"
    ]
  }
}
```

## Coding Conventions

### 1. File Naming

```typescript
// Components: PascalCase
DashboardBuilder.tsx
ChartWidget.tsx
UserAvatar.tsx

// Hooks: camelCase with 'use' prefix
useAuth.ts
useDashboard.ts
useWidgetData.ts

// Utilities: camelCase
validation.ts
formatting.ts
helpers.ts

// Types: PascalCase
Dashboard.ts
Widget.ts
User.ts

// Constants: UPPER_SNAKE_CASE
API_ENDPOINTS.ts
CHART_TYPES.ts
WIDGET_TYPES.ts
```

### 2. Component Structure

```typescript
// components/dashboard/DashboardBuilder.tsx
import React from 'react'
import { useDashboardStore } from '@/lib/store/dashboard-store'
import { DashboardToolbar } from './DashboardToolbar'
import { DashboardCanvas } from './DashboardCanvas'
import { CollaborationPanel } from '@/components/collaboration/CollaborationPanel'

interface DashboardBuilderProps {
  dashboard: Dashboard
}

export const DashboardBuilder: React.FC<DashboardBuilderProps> = ({ dashboard }) => {
  const { widgets, addWidget, updateWidget, removeWidget } = useDashboardStore()
  const { isEditing, setEditingMode } = useDashboardStore()

  return (
    <div className="dashboard-builder">
      <DashboardToolbar 
        isEditing={isEditing}
        onToggleEdit={() => setEditingMode(!isEditing)}
        onAddWidget={addWidget}
      />
      
      <DashboardCanvas>
        {widgets.map(widget => (
          <DraggableWidget
            key={widget.id}
            widget={widget}
            isEditing={isEditing}
            onUpdate={updateWidget}
            onRemove={removeWidget}
          />
        ))}
      </DashboardCanvas>
      
      <CollaborationPanel />
    </div>
  )
}

export default DashboardBuilder
```

### 3. Hook Structure

```typescript
// lib/hooks/useAuth.ts
import { useState, useEffect } from 'react'
import { useUserStore } from '@/lib/store/user-store'
import { loginUser, logoutUser } from '@/lib/api/auth'
import type { LoginCredentials, User } from '@/types/auth'

export const useAuth = () => {
  const { user, setUser, clearUser } = useUserStore()
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const login = async (credentials: LoginCredentials) => {
    try {
      setIsLoading(true)
      setError(null)
      
      const user = await loginUser(credentials)
      setUser(user)
      
      return user
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Login failed'
      setError(errorMessage)
      throw err
    } finally {
      setIsLoading(false)
    }
  }

  const logout = async () => {
    try {
      await logoutUser()
      clearUser()
    } catch (err) {
      console.error('Logout error:', err)
    }
  }

  return {
    user,
    isLoading,
    error,
    login,
    logout,
    isAuthenticated: !!user
  }
}
```

### 4. Store Structure

```typescript
// lib/store/dashboard-store.ts
import { create } from 'zustand'
import { devtools, persist } from 'zustand/middleware'
import type { Dashboard, Widget, CursorPosition, Comment } from '@/types/dashboard'

interface DashboardState {
  // State
  dashboards: Dashboard[]
  currentDashboard: Dashboard | null
  selectedWidgets: string[]
  isEditing: boolean
  isCollaborating: boolean
  cursorPositions: Record<string, CursorPosition>
  comments: Comment[]

  // Actions
  setDashboards: (dashboards: Dashboard[]) => void
  setCurrentDashboard: (dashboard: Dashboard | null) => void
  addDashboard: (dashboard: Dashboard) => void
  updateDashboard: (id: string, updates: Partial<Dashboard>) => void
  deleteDashboard: (id: string) => void
  selectWidget: (widgetId: string) => void
  addWidget: (widget: Widget) => void
  updateWidget: (widgetId: string, updates: Partial<Widget>) => void
  removeWidget: (widgetId: string) => void
  setEditingMode: (editing: boolean) => void
  updateCursorPosition: (userId: string, position: CursorPosition) => void
  addComment: (comment: Comment) => void
  clearComments: () => void
}

export const useDashboardStore = create<DashboardState>()(
  devtools(
    persist(
      (set, get) => ({
        // Initial state
        dashboards: [],
        currentDashboard: null,
        selectedWidgets: [],
        isEditing: false,
        isCollaborating: false,
        cursorPositions: {},
        comments: [],

        // Actions
        setDashboards: (dashboards) => set({ dashboards }),
        
        setCurrentDashboard: (currentDashboard) => set({ currentDashboard }),
        
        addDashboard: (dashboard) => 
          set((state) => ({ 
            dashboards: [...state.dashboards, dashboard] 
          })),
        
        updateDashboard: (id, updates) =>
          set((state) => ({
            dashboards: state.dashboards.map(dashboard =>
              dashboard.id === id ? { ...dashboard, ...updates } : dashboard
            ),
            currentDashboard: state.currentDashboard?.id === id
              ? { ...state.currentDashboard, ...updates }
              : state.currentDashboard
          })),
        
        deleteDashboard: (id) =>
          set((state) => ({
            dashboards: state.dashboards.filter(dashboard => dashboard.id !== id),
            currentDashboard: state.currentDashboard?.id === id ? null : state.currentDashboard
          })),
        
        selectWidget: (widgetId) =>
          set((state) => ({
            selectedWidgets: state.selectedWidgets.includes(widgetId)
              ? state.selectedWidgets.filter(id => id !== widgetId)
              : [...state.selectedWidgets, widgetId]
          })),
        
        addWidget: (widget) =>
          set((state) => ({
            currentDashboard: state.currentDashboard
              ? {
                  ...state.currentDashboard,
                  widgets: [...state.currentDashboard.widgets, widget]
                }
              : null
          })),
        
        updateWidget: (widgetId, updates) =>
          set((state) => ({
            currentDashboard: state.currentDashboard
              ? {
                  ...state.currentDashboard,
                  widgets: state.currentDashboard.widgets.map(widget =>
                    widget.id === widgetId ? { ...widget, ...updates } : widget
                  )
                }
              : null
          })),
        
        removeWidget: (widgetId) =>
          set((state) => ({
            currentDashboard: state.currentDashboard
              ? {
                  ...state.currentDashboard,
                  widgets: state.currentDashboard.widgets.filter(
                    widget => widget.id !== widgetId
                  )
                }
              : null,
            selectedWidgets: state.selectedWidgets.filter(id => id !== widgetId)
          })),
        
        setEditingMode: (isEditing) => set({ isEditing }),
        
        updateCursorPosition: (userId, position) =>
          set((state) => ({
            cursorPositions: { ...state.cursorPositions, [userId]: position }
          })),
        
        addComment: (comment) =>
          set((state) => ({
            comments: [...state.comments, comment]
          })),
        
        clearComments: () => set({ comments: [] })
      }),
      {
        name: 'dashboard-storage',
        partialize: (state) => ({
          dashboards: state.dashboards,
          currentDashboard: state.currentDashboard
        })
      }
    )
  )
)
```

### 5. API Structure

```typescript
// lib/api/client.ts
import axios from 'axios'
import { getAuthToken, clearAuthToken } from '@/lib/utils/auth'

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    const token = getAuthToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearAuthToken()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default apiClient

// lib/api/dashboard.ts
import apiClient from './client'
import type { Dashboard, CreateDashboardRequest, UpdateDashboardRequest } from '@/types/dashboard'

export const dashboardApi = {
  // Get all dashboards
  getDashboards: async (): Promise<Dashboard[]> => {
    const response = await apiClient.get('/dashboards')
    return response.data
  },

  // Get dashboard by ID
  getDashboard: async (id: string): Promise<Dashboard> => {
    const response = await apiClient.get(`/dashboards/${id}`)
    return response.data
  },

  // Create dashboard
  createDashboard: async (data: CreateDashboardRequest): Promise<Dashboard> => {
    const response = await apiClient.post('/dashboards', data)
    return response.data
  },

  // Update dashboard
  updateDashboard: async (id: string, data: UpdateDashboardRequest): Promise<Dashboard> => {
    const response = await apiClient.put(`/dashboards/${id}`, data)
    return response.data
  },

  // Delete dashboard
  deleteDashboard: async (id: string): Promise<void> => {
    await apiClient.delete(`/dashboards/${id}`)
  }
}
```

### 6. Type Definitions

```typescript
// types/dashboard.ts
export interface Dashboard {
  id: string
  title: string
  description?: string
  userId: string
  widgets: Widget[]
  layout: Layout
  settings: DashboardSettings
  createdAt: string
  updatedAt: string
}

export interface Widget {
  id: string
  type: WidgetType
  title: string
  position: Position
  size: Size
  config: WidgetConfig
  dataSource: DataSource
  refreshInterval?: number
}

export interface Position {
  x: number
  y: number
}

export interface Size {
  width: number
  height: number
}

export interface Layout {
  type: 'grid' | 'free'
  columns?: number
  rows?: number
}

export interface DashboardSettings {
  theme: 'light' | 'dark'
  showGrid: boolean
  allowCollaboration: boolean
  autoSave: boolean
}

export interface CreateDashboardRequest {
  title: string
  description?: string
  layout?: Layout
  settings?: Partial<DashboardSettings>
}

export interface UpdateDashboardRequest {
  title?: string
  description?: string
  layout?: Layout
  settings?: Partial<DashboardSettings>
}

export type WidgetType = 'chart' | 'table' | 'metric' | 'text' | 'image' | 'iframe'

export interface WidgetConfig {
  [key: string]: any
}

export interface DataSource {
  id: string
  type: 'api' | 'database' | 'file'
  url?: string
  query?: string
  headers?: Record<string, string>
}
```

### 7. Testing Structure

```typescript
// tests/unit/components/dashboard/DashboardBuilder.test.tsx
import React from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import { DashboardBuilder } from '@/components/dashboard/DashboardBuilder'
import { mockDashboard } from '@/tests/fixtures/dashboards'

// Mock stores
jest.mock('@/lib/store/dashboard-store', () => ({
  useDashboardStore: jest.fn()
}))

describe('DashboardBuilder', () => {
  const mockUseDashboardStore = useDashboardStore as jest.MockedFunction<typeof useDashboardStore>

  beforeEach(() => {
    mockUseDashboardStore.mockReturnValue({
      widgets: [],
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: jest.fn(),
      isEditing: false,
      setEditingMode: jest.fn()
    })
  })

  it('renders dashboard builder with toolbar', () => {
    render(<DashboardBuilder dashboard={mockDashboard} />)
    
    expect(screen.getByTestId('dashboard-toolbar')).toBeInTheDocument()
    expect(screen.getByTestId('dashboard-canvas')).toBeInTheDocument()
  })

  it('enables edit mode when toggle button is clicked', () => {
    const setEditingMode = jest.fn()
    mockUseDashboardStore.mockReturnValue({
      widgets: [],
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: jest.fn(),
      isEditing: false,
      setEditingMode
    })

    render(<DashboardBuilder dashboard={mockDashboard} />)
    
    const editButton = screen.getByText('Edit')
    fireEvent.click(editButton)
    
    expect(setEditingMode).toHaveBeenCalledWith(true)
  })

  it('displays widgets in canvas', () => {
    const mockWidgets = [
      { id: '1', type: 'chart', title: 'Test Chart' },
      { id: '2', type: 'metric', title: 'Test Metric' }
    ]
    
    mockUseDashboardStore.mockReturnValue({
      widgets: mockWidgets,
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: jest.fn(),
      isEditing: false,
      setEditingMode: jest.fn()
    })

    render(<DashboardBuilder dashboard={mockDashboard} />)
    
    expect(screen.getByText('Test Chart')).toBeInTheDocument()
    expect(screen.getByText('Test Metric')).toBeInTheDocument()
  })
})
```

### 8. Environment Configuration

```typescript
// lib/config/environment.ts
export const environment = {
  // API Configuration
  API_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
  SOCKET_URL: process.env.NEXT_PUBLIC_SOCKET_URL || 'ws://localhost:8080',
  
  // Feature Flags
  ENABLE_COLLABORATION: process.env.NEXT_PUBLIC_ENABLE_COLLABORATION === 'true',
  ENABLE_REAL_TIME: process.env.NEXT_PUBLIC_ENABLE_REAL_TIME === 'true',
  
  // Analytics
  GA_ID: process.env.NEXT_PUBLIC_GA_ID,
  SENTRY_DSN: process.env.NEXT_PUBLIC_SENTRY_DSN,
  
  // Development
  IS_DEVELOPMENT: process.env.NODE_ENV === 'development',
  IS_PRODUCTION: process.env.NODE_ENV === 'production',
  
  // Build
  BUILD_TIME: process.env.NEXT_PUBLIC_BUILD_TIME,
  VERSION: process.env.NEXT_PUBLIC_VERSION || '1.0.0'
} as const

// lib/config/constants.ts
export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    LOGOUT: '/auth/logout',
    REGISTER: '/auth/register',
    REFRESH: '/auth/refresh'
  },
  DASHBOARD: {
    LIST: '/dashboards',
    CREATE: '/dashboards',
    DETAIL: (id: string) => `/dashboards/${id}`,
    UPDATE: (id: string) => `/dashboards/${id}`,
    DELETE: (id: string) => `/dashboards/${id}`
  },
  WIDGET: {
    LIST: '/widgets',
    CREATE: '/widgets',
    DATA: '/widgets/data'
  }
} as const

export const WIDGET_TYPES = {
  CHART: 'chart',
  TABLE: 'table',
  METRIC: 'metric',
  TEXT: 'text',
  IMAGE: 'image',
  IFRAME: 'iframe'
} as const

export const CHART_TYPES = {
  LINE: 'line',
  BAR: 'bar',
  PIE: 'pie',
  AREA: 'area',
  SCATTER: 'scatter'
} as const
```

This structure provides a comprehensive foundation for building scalable, maintainable React.js/Next.js applications with proper separation of concerns, testing strategies, and modern development practices. 