import { create } from 'zustand'

export interface Widget {
  id: string
  type: 'chart' | 'table' | 'metric' | 'text' | 'image' | 'iframe'
  title: string
  position: { x: number; y: number }
  size: { width: number; height: number }
  config: any
  dataSource: string
  refreshInterval?: number
}

export interface Dashboard {
  id: string
  title: string
  description?: string
  widgets: Widget[]
  layout: 'grid' | 'freeform'
  isPublic: boolean
  createdAt: Date
  updatedAt: Date
}

export interface DashboardState {
  dashboards: Dashboard[]
  currentDashboard: Dashboard | null
  selectedWidgets: string[]
  isEditing: boolean
  isCollaborating: boolean
  
  // Actions
  setDashboards: (dashboards: Dashboard[]) => void
  setCurrentDashboard: (dashboard: Dashboard | null) => void
  addDashboard: (dashboard: Dashboard) => void
  updateDashboard: (id: string, updates: Partial<Dashboard>) => void
  deleteDashboard: (id: string) => void
  selectWidget: (widgetId: string) => void
  deselectWidget: (widgetId: string) => void
  addWidget: (widget: Widget) => void
  updateWidget: (widgetId: string, updates: Partial<Widget>) => void
  removeWidget: (widgetId: string) => void
  setEditingMode: (editing: boolean) => void
  setCollaboratingMode: (collaborating: boolean) => void
}

export const useDashboardStore = create<DashboardState>((set, get) => ({
  dashboards: [],
  currentDashboard: null,
  selectedWidgets: [],
  isEditing: false,
  isCollaborating: false,

  setDashboards: (dashboards) => set({ dashboards }),

  setCurrentDashboard: (dashboard) => set({ currentDashboard: dashboard }),

  addDashboard: (dashboard) => 
    set((state) => ({ 
      dashboards: [...state.dashboards, dashboard] 
    })),

  updateDashboard: (id, updates) =>
    set((state) => ({
      dashboards: state.dashboards.map(dashboard =>
        dashboard.id === id ? { ...dashboard, ...updates, updatedAt: new Date() } : dashboard
      ),
      currentDashboard: state.currentDashboard?.id === id 
        ? { ...state.currentDashboard, ...updates, updatedAt: new Date() }
        : state.currentDashboard,
    })),

  deleteDashboard: (id) =>
    set((state) => ({
      dashboards: state.dashboards.filter(dashboard => dashboard.id !== id),
      currentDashboard: state.currentDashboard?.id === id ? null : state.currentDashboard,
    })),

  selectWidget: (widgetId) =>
    set((state) => ({
      selectedWidgets: [...state.selectedWidgets, widgetId],
    })),

  deselectWidget: (widgetId) =>
    set((state) => ({
      selectedWidgets: state.selectedWidgets.filter(id => id !== widgetId),
    })),

  addWidget: (widget) =>
    set((state) => ({
      currentDashboard: state.currentDashboard
        ? {
            ...state.currentDashboard,
            widgets: [...state.currentDashboard.widgets, widget],
            updatedAt: new Date(),
          }
        : null,
    })),

  updateWidget: (widgetId, updates) =>
    set((state) => ({
      currentDashboard: state.currentDashboard
        ? {
            ...state.currentDashboard,
            widgets: state.currentDashboard.widgets.map(widget =>
              widget.id === widgetId ? { ...widget, ...updates } : widget
            ),
            updatedAt: new Date(),
          }
        : null,
    })),

  removeWidget: (widgetId) =>
    set((state) => ({
      currentDashboard: state.currentDashboard
        ? {
            ...state.currentDashboard,
            widgets: state.currentDashboard.widgets.filter(widget => widget.id !== widgetId),
            updatedAt: new Date(),
          }
        : null,
      selectedWidgets: state.selectedWidgets.filter(id => id !== widgetId),
    })),

  setEditingMode: (editing) => set({ isEditing: editing }),

  setCollaboratingMode: (collaborating) => set({ isCollaborating: collaborating }),
})) 