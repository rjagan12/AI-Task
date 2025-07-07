'use client'

import { useCallback } from 'react'
import { DndProvider } from 'react-dnd'
import { HTML5Backend } from 'react-dnd-html5-backend'
import { Dashboard, Widget } from '@/lib/store/dashboard-store'
import DashboardWidget from './DashboardWidget'
import WidgetLibrary from './WidgetLibrary'
import { useDashboardStore } from '@/lib/store/dashboard-store'

interface DashboardBuilderProps {
  dashboard: Dashboard
}

export default function DashboardBuilder({ dashboard }: DashboardBuilderProps) {
  const { isEditing, addWidget, updateWidget, removeWidget } = useDashboardStore()

  const handleWidgetDrop = useCallback((item: Widget, monitor: any) => {
    const dropResult = monitor.getDropResult()
    if (dropResult) {
      const { x, y } = dropResult
      const newWidget = {
        ...item,
        position: { x, y },
        id: `${item.type}-${Date.now()}`,
      }
      addWidget(newWidget)
    }
  }, [addWidget])

  const handleWidgetUpdate = useCallback((widgetId: string, updates: Partial<Widget>) => {
    updateWidget(widgetId, updates)
  }, [updateWidget])

  const handleWidgetRemove = useCallback((widgetId: string) => {
    removeWidget(widgetId)
  }, [removeWidget])

  return (
    <DndProvider backend={HTML5Backend}>
      <div className="h-full">
        {isEditing && (
          <div className="mb-6">
            <WidgetLibrary />
          </div>
        )}
        
        <div className="relative bg-gray-50 rounded-lg border-2 border-dashed border-gray-300 min-h-[600px]">
          {dashboard.widgets.length === 0 ? (
            <div className="absolute inset-0 flex items-center justify-center">
              <div className="text-center">
                <h3 className="text-lg font-medium text-gray-900 mb-2">
                  No widgets yet
                </h3>
                <p className="text-gray-500 mb-4">
                  {isEditing 
                    ? 'Drag widgets from the library above to get started'
                    : 'This dashboard is empty'
                  }
                </p>
              </div>
            </div>
          ) : (
            <div className="relative w-full h-full">
              {dashboard.widgets.map((widget) => (
                <DashboardWidget
                  key={widget.id}
                  widget={widget}
                  isEditing={isEditing}
                  onUpdate={handleWidgetUpdate}
                  onRemove={handleWidgetRemove}
                />
              ))}
            </div>
          )}
        </div>
      </div>
    </DndProvider>
  )
} 