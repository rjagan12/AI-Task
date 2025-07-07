import React from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import Header from '@/components/layout/Header'
import { useAuthStore } from '@/lib/store/auth-store'
import { useDashboardStore } from '@/lib/store/dashboard-store'

// Mock the stores
jest.mock('@/lib/store/auth-store', () => ({
  useAuthStore: jest.fn(),
}))

jest.mock('@/lib/store/dashboard-store', () => ({
  useDashboardStore: jest.fn(),
}))

// Mock lucide-react icons
jest.mock('lucide-react', () => ({
  Bell: () => <div data-testid="bell-icon">Bell</div>,
  Search: () => <div data-testid="search-icon">Search</div>,
  Settings: () => <div data-testid="settings-icon">Settings</div>,
  User: () => <div data-testid="user-icon">User</div>,
  Edit3: () => <div data-testid="edit-icon">Edit</div>,
  Eye: () => <div data-testid="eye-icon">Eye</div>,
  Share2: () => <div data-testid="share-icon">Share</div>,
}))

const mockUser = {
  id: 'user-1',
  email: 'test@example.com',
  fullName: 'John Doe',
  role: 'STUDENT',
}

const mockDashboard = {
  id: 'dashboard-1',
  title: 'Test Dashboard',
  description: 'Test Description',
  widgets: [],
}

describe('Header', () => {
  const mockUseAuthStore = useAuthStore as jest.MockedFunction<typeof useAuthStore>
  const mockUseDashboardStore = useDashboardStore as jest.MockedFunction<typeof useDashboardStore>

  beforeEach(() => {
    mockUseAuthStore.mockReturnValue({
      user: mockUser,
      isAuthenticated: true,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    mockUseDashboardStore.mockReturnValue({
      currentDashboard: mockDashboard,
      isEditing: false,
      setEditingMode: jest.fn(),
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: jest.fn(),
      dashboard: mockDashboard,
    })
  })

  afterEach(() => {
    jest.clearAllMocks()
  })

  it('renders header with user information', () => {
    render(<Header />)

    expect(screen.getByText('John Doe')).toBeInTheDocument()
    expect(screen.getByText('STUDENT')).toBeInTheDocument()
  })

  it('renders dashboard title', () => {
    render(<Header />)

    expect(screen.getByText('Test Dashboard')).toBeInTheDocument()
  })

  it('renders search input', () => {
    render(<Header />)

    const searchInput = screen.getByPlaceholderText('Search...')
    expect(searchInput).toBeInTheDocument()
  })

  it('renders all action buttons', () => {
    render(<Header />)

    expect(screen.getByTestId('bell-icon')).toBeInTheDocument()
    expect(screen.getByTestId('settings-icon')).toBeInTheDocument()
    expect(screen.getByTestId('edit-icon')).toBeInTheDocument()
    expect(screen.getByTestId('share-icon')).toBeInTheDocument()
  })

  it('shows edit icon when not in editing mode', () => {
    render(<Header />)

    expect(screen.getByTestId('edit-icon')).toBeInTheDocument()
    expect(screen.queryByTestId('eye-icon')).not.toBeInTheDocument()
  })

  it('shows eye icon when in editing mode', () => {
    mockUseDashboardStore.mockReturnValue({
      currentDashboard: mockDashboard,
      isEditing: true,
      setEditingMode: jest.fn(),
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: jest.fn(),
      dashboard: mockDashboard,
    })

    render(<Header />)

    expect(screen.getByTestId('eye-icon')).toBeInTheDocument()
    expect(screen.queryByTestId('edit-icon')).not.toBeInTheDocument()
  })

  it('calls setEditingMode when edit toggle button is clicked', () => {
    const mockSetEditingMode = jest.fn()
    mockUseDashboardStore.mockReturnValue({
      currentDashboard: mockDashboard,
      isEditing: false,
      setEditingMode: mockSetEditingMode,
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: jest.fn(),
      dashboard: mockDashboard,
    })

    render(<Header />)

    const editButton = screen.getByTitle('Edit Mode')
    fireEvent.click(editButton)

    expect(mockSetEditingMode).toHaveBeenCalledWith(true)
  })

  it('calls setEditingMode with false when view toggle button is clicked', () => {
    const mockSetEditingMode = jest.fn()
    mockUseDashboardStore.mockReturnValue({
      currentDashboard: mockDashboard,
      isEditing: true,
      setEditingMode: mockSetEditingMode,
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: jest.fn(),
      dashboard: mockDashboard,
    })

    render(<Header />)

    const viewButton = screen.getByTitle('View Mode')
    fireEvent.click(viewButton)

    expect(mockSetEditingMode).toHaveBeenCalledWith(false)
  })

  it('updates search query when input changes', () => {
    render(<Header />)

    const searchInput = screen.getByPlaceholderText('Search...')
    fireEvent.change(searchInput, { target: { value: 'test query' } })

    expect(searchInput).toHaveValue('test query')
  })

  it('renders notification indicator', () => {
    render(<Header />)

    const notificationIndicator = screen.getByTestId('bell-icon').parentElement
    expect(notificationIndicator).toHaveClass('relative')
  })

  it('applies correct CSS classes for edit button when editing', () => {
    mockUseDashboardStore.mockReturnValue({
      currentDashboard: mockDashboard,
      isEditing: true,
      setEditingMode: jest.fn(),
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: jest.fn(),
      dashboard: mockDashboard,
    })

    render(<Header />)

    const editButton = screen.getByTitle('View Mode')
    expect(editButton).toHaveClass('bg-blue-100', 'text-blue-700')
  })

  it('applies correct CSS classes for edit button when not editing', () => {
    render(<Header />)

    const editButton = screen.getByTitle('Edit Mode')
    expect(editButton).toHaveClass('hover:bg-gray-100', 'text-gray-600')
  })

  it('handles missing user gracefully', () => {
    mockUseAuthStore.mockReturnValue({
      user: null,
      isAuthenticated: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(<Header />)

    expect(screen.queryByText('John Doe')).not.toBeInTheDocument()
    expect(screen.queryByText('STUDENT')).not.toBeInTheDocument()
  })

  it('handles missing dashboard gracefully', () => {
    mockUseDashboardStore.mockReturnValue({
      currentDashboard: null,
      isEditing: false,
      setEditingMode: jest.fn(),
      addWidget: jest.fn(),
      updateWidget: jest.fn(),
      removeWidget: jest.fn(),
      dashboard: null,
    })

    render(<Header />)

    expect(screen.queryByText('Test Dashboard')).not.toBeInTheDocument()
  })
}) 