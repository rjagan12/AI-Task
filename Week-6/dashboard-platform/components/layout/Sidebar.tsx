'use client'

import { useState } from 'react'
import { useAuthStore } from '@/lib/store/auth-store'
import { useDashboardStore } from '@/lib/store/dashboard-store'
import { 
  LayoutDashboard, 
  Plus, 
  Settings, 
  LogOut, 
  User,
  BarChart3,
  Calendar,
  Users,
  BookOpen
} from 'lucide-react'

export default function Sidebar() {
  const [isCollapsed, setIsCollapsed] = useState(false)
  const { user, logout } = useAuthStore()
  const { dashboards, currentDashboard, setCurrentDashboard } = useDashboardStore()

  const navigationItems = [
    { icon: LayoutDashboard, label: 'Dashboard', href: '/dashboard' },
    { icon: BookOpen, label: 'Courses', href: '/courses' },
    { icon: Users, label: 'Students', href: '/students' },
    { icon: BarChart3, label: 'Analytics', href: '/analytics' },
    { icon: Calendar, label: 'Schedule', href: '/schedule' },
  ]

  const handleLogout = () => {
    logout()
  }

  return (
    <div className={`bg-white border-r border-gray-200 transition-all duration-300 ${
      isCollapsed ? 'w-16' : 'w-64'
    }`}>
      <div className="flex flex-col h-full">
        {/* Header */}
        <div className="p-4 border-b border-gray-200">
          <div className="flex items-center justify-between">
            {!isCollapsed && (
              <h1 className="text-xl font-bold text-gray-900">LMS Platform</h1>
            )}
            <button
              onClick={() => setIsCollapsed(!isCollapsed)}
              className="p-2 rounded-md hover:bg-gray-100"
            >
              <LayoutDashboard className="h-5 w-5 text-gray-600" />
            </button>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-4">
          <ul className="space-y-2">
            {navigationItems.map((item) => (
              <li key={item.label}>
                <a
                  href={item.href}
                  className="flex items-center px-3 py-2 text-sm font-medium text-gray-700 rounded-md hover:bg-gray-100 hover:text-gray-900"
                >
                  <item.icon className="h-5 w-5 mr-3" />
                  {!isCollapsed && item.label}
                </a>
              </li>
            ))}
          </ul>

          {/* Dashboards Section */}
          {!isCollapsed && (
            <div className="mt-8">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-sm font-medium text-gray-500 uppercase tracking-wider">
                  Dashboards
                </h3>
                <button className="p-1 rounded-md hover:bg-gray-100">
                  <Plus className="h-4 w-4 text-gray-400" />
                </button>
              </div>
              <ul className="space-y-1">
                {dashboards.map((dashboard) => (
                  <li key={dashboard.id}>
                    <button
                      onClick={() => setCurrentDashboard(dashboard)}
                      className={`w-full text-left px-3 py-2 text-sm rounded-md transition-colors ${
                        currentDashboard?.id === dashboard.id
                          ? 'bg-blue-100 text-blue-700'
                          : 'text-gray-700 hover:bg-gray-100'
                      }`}
                    >
                      {dashboard.title}
                    </button>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </nav>

        {/* User Section */}
        <div className="p-4 border-t border-gray-200">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="h-8 w-8 rounded-full bg-gray-300 flex items-center justify-center">
                <User className="h-4 w-4 text-gray-600" />
              </div>
            </div>
            {!isCollapsed && (
              <div className="ml-3 flex-1">
                <p className="text-sm font-medium text-gray-700">{user?.fullName}</p>
                <p className="text-xs text-gray-500">{user?.role}</p>
              </div>
            )}
            <button
              onClick={handleLogout}
              className="ml-2 p-1 rounded-md hover:bg-gray-100"
              title="Logout"
            >
              <LogOut className="h-4 w-4 text-gray-400" />
            </button>
          </div>
        </div>
      </div>
    </div>
  )
} 