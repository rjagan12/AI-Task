import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import DashboardBuilder from '@/components/dashboard/DashboardBuilder'
import { useDashboardStore } from '@/lib/store/dashboard-store'

// Mock the dashboard store
jest.mock('@/lib/store/dashboard-store', () => ({
  useDashboardStore: jest.fn(),
}))

// Mock react-dnd
jest.mock('react-dnd', () => ({
  DndProvider: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}))

jest.mock('react-dnd-html5-backend', () => ({
  HTML5Backend: jest.fn(),
}))

// Mock DashboardWidget component
jest.mock('@/components/dashboard/DashboardWidget', () => {
  return function MockDashboardWidget({ widget, isEditing, onUpdate, onRemove }: any) {
    return (
      <div data-testid={`widget-${widget.id}`}>
        <span>{widget.title}</span>
        {isEditing && (
          <>
            <button onClick={() => onUpdate(widget.id, { title: 'Updated Widget' })}>
              Update
            </button>
            <button onClick={() => onRemove(widget.id)}>Remove</button>
          </>
        )}
      </div>
    )
  }
})

// Mock WidgetLibrary component
jest.mock('@/components/dashboard/WidgetLibrary', () => {
  return function MockWidgetLibrary() {
    return <div data-testid="widget-library">Widget Library</div>
  }
})

const mockDashboard = {
  id: 'dashboard-1',
  title: 'Test Dashboard',
  description: 'Test Description',
  widgets: [
    {
      id: 'widget-1',
      type: 'chart',
      title: 'Test Widget 1',
      position: { x: 0, y: 0 },
      size: { width: 300, height: 200 },
      config: {},
    },
    {
      id: 'widget-2',
      type: 'stats',
      title: 'Test Widget 2',
      position: { x: 320, y: 0 },
      size: { width: 300, height: 200 },
      config: {},
    },
  ],
}

describe('DashboardBuilder', () => {
  const mockUseDashboardStore = useDashboardStore as jest.MockedFunction<typeof useDashboardStore>

  beforeEach(() => {
    mockUseDashboardStore.mockReturnValue({
      isEditing: false,
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: jest.fn(),
      toggleEditMode: jest.fn(),
      dashboard: mockDashboard,
    })
  })

  afterEach(() => {
    jest.clearAllMocks()
  })

  it('renders dashboard with widgets', () => {
    render(<DashboardBuilder dashboard={mockDashboard} />)

    expect(screen.getByText('Test Widget 1')).toBeInTheDocument()
    expect(screen.getByText('Test Widget 2')).toBeInTheDocument()
    expect(screen.queryByTestId('widget-library')).not.toBeInTheDocument()
  })

  it('shows widget library when in editing mode', () => {
    mockUseDashboardStore.mockReturnValue({
      isEditing: true,
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: jest.fn(),
      toggleEditMode: jest.fn(),
      dashboard: mockDashboard,
    })

    render(<DashboardBuilder dashboard={mockDashboard} />)

    expect(screen.getByTestId('widget-library')).toBeInTheDocument()
  })

  it('shows empty state when no widgets', () => {
    const emptyDashboard = {
      ...mockDashboard,
      widgets: [],
    }

    render(<DashboardBuilder dashboard={emptyDashboard} />)

    expect(screen.getByText('No widgets yet')).toBeInTheDocument()
    expect(screen.getByText('This dashboard is empty')).toBeInTheDocument()
  })

  it('shows editing instructions when in editing mode with no widgets', () => {
    mockUseDashboardStore.mockReturnValue({
      isEditing: true,
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: jest.fn(),
      toggleEditMode: jest.fn(),
      dashboard: { ...mockDashboard, widgets: [] },
    })

    render(<DashboardBuilder dashboard={{ ...mockDashboard, widgets: [] }} />)

    expect(screen.getByText('Drag widgets from the library above to get started')).toBeInTheDocument()
  })

  it('calls updateWidget when widget is updated', async () => {
    const mockUpdateWidget = jest.fn()
    mockUseDashboardStore.mockReturnValue({
      isEditing: true,
      addWidget: jest.fn(),
      updateWidget: mockUpdateWidget,
      removeWidget: jest.fn(),
      toggleEditMode: jest.fn(),
      dashboard: mockDashboard,
    })

    render(<DashboardBuilder dashboard={mockDashboard} />)

    const updateButtons = screen.getAllByText('Update')
    fireEvent.click(updateButtons[0])

    await waitFor(() => {
      expect(mockUpdateWidget).toHaveBeenCalledWith('widget-1', { title: 'Updated Widget' })
    })
  })

  it('calls removeWidget when widget is removed', async () => {
    const mockRemoveWidget = jest.fn()
    mockUseDashboardStore.mockReturnValue({
      isEditing: true,
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: mockRemoveWidget,
      toggleEditMode: jest.fn(),
      dashboard: mockDashboard,
    })

    render(<DashboardBuilder dashboard={mockDashboard} />)

    const removeButtons = screen.getAllByText('Remove')
    fireEvent.click(removeButtons[0])

    await waitFor(() => {
      expect(mockRemoveWidget).toHaveBeenCalledWith('widget-1')
    })
  })

  it('renders correct number of widgets', () => {
    render(<DashboardBuilder dashboard={mockDashboard} />)

    const widgets = screen.getAllByTestId(/^widget-/)
    expect(widgets).toHaveLength(2)
  })

  it('applies correct CSS classes for dashboard container', () => {
    render(<DashboardBuilder dashboard={mockDashboard} />)

    const dashboardContainer = screen.getByText('Test Widget 1').closest('.relative')
    expect(dashboardContainer).toHaveClass('relative', 'bg-gray-50', 'rounded-lg', 'border-2', 'border-dashed', 'border-gray-300', 'min-h-[600px]')
  })
}) 