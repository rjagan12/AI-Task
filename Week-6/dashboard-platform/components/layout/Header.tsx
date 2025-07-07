'use client'

import { useState } from 'react'
import { useAuthStore } from '@/lib/store/auth-store'
import { useDashboardStore } from '@/lib/store/dashboard-store'
import { 
  Bell, 
  Search, 
  Settings, 
  User,
  Edit3,
  Eye,
  Share2
} from 'lucide-react'

export default function Header() {
  const { user } = useAuthStore()
  const { currentDashboard, isEditing, setEditingMode } = useDashboardStore()
  const [searchQuery, setSearchQuery] = useState('')

  const handleEditToggle = () => {
    setEditingMode(!isEditing)
  }

  return (
    <header className="bg-white border-b border-gray-200 px-6 py-4">
      <div className="flex items-center justify-between">
        {/* Left Section */}
        <div className="flex items-center space-x-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
        </div>

        {/* Center Section - Dashboard Title */}
        <div className="flex-1 flex justify-center">
          {currentDashboard && (
            <div className="flex items-center space-x-4">
              <h1 className="text-xl font-semibold text-gray-900">
                {currentDashboard.title}
              </h1>
              <div className="flex items-center space-x-2">
                <button
                  onClick={handleEditToggle}
                  className={`p-2 rounded-md transition-colors ${
                    isEditing
                      ? 'bg-blue-100 text-blue-700'
                      : 'hover:bg-gray-100 text-gray-600'
                  }`}
                  title={isEditing ? 'View Mode' : 'Edit Mode'}
                >
                  {isEditing ? <Eye className="h-4 w-4" /> : <Edit3 className="h-4 w-4" />}
                </button>
                <button
                  className="p-2 rounded-md hover:bg-gray-100 text-gray-600"
                  title="Share Dashboard"
                >
                  <Share2 className="h-4 w-4" />
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Right Section */}
        <div className="flex items-center space-x-4">
          <button className="relative p-2 rounded-md hover:bg-gray-100">
            <Bell className="h-5 w-5 text-gray-600" />
            <span className="absolute top-1 right-1 h-2 w-2 bg-red-500 rounded-full"></span>
          </button>
          
          <button className="p-2 rounded-md hover:bg-gray-100">
            <Settings className="h-5 w-5 text-gray-600" />
          </button>
          
          <div className="flex items-center space-x-2">
            <div className="h-8 w-8 rounded-full bg-gray-300 flex items-center justify-center">
              <User className="h-4 w-4 text-gray-600" />
            </div>
            <div className="hidden md:block">
              <p className="text-sm font-medium text-gray-700">{user?.fullName}</p>
              <p className="text-xs text-gray-500">{user?.role}</p>
            </div>
          </div>
        </div>
      </div>
    </header>
  )
} 