'use client'

import { useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { useAuthStore } from '@/lib/store/auth-store'
import { useDashboardStore } from '@/lib/store/dashboard-store'
import DashboardLayout from '@/components/layout/DashboardLayout'
import DashboardBuilder from '@/components/dashboard/DashboardBuilder'
import { useQuery } from '@tanstack/react-query'
import { fetchDashboards } from '@/lib/api/dashboard-api'

export default function DashboardPage() {
  const router = useRouter()
  const { isAuthenticated, user } = useAuthStore()
  const { setDashboards, currentDashboard } = useDashboardStore()

  const { data: dashboards, isLoading } = useQuery({
    queryKey: ['dashboards'],
    queryFn: fetchDashboards,
    enabled: isAuthenticated,
  })

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/auth/login')
    }
  }, [isAuthenticated, router])

  useEffect(() => {
    if (dashboards) {
      setDashboards(dashboards)
    }
  }, [dashboards, setDashboards])

  if (!isAuthenticated) {
    return null
  }

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  return (
    <DashboardLayout>
      <div className="p-6">
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600 mt-2">
            Welcome back, {user?.fullName}! Create and manage your dashboards.
          </p>
        </div>
        
        {currentDashboard ? (
          <DashboardBuilder dashboard={currentDashboard} />
        ) : (
          <div className="text-center py-12">
            <h2 className="text-xl font-semibold text-gray-700 mb-4">
              No dashboard selected
            </h2>
            <p className="text-gray-500 mb-6">
              Select a dashboard from the sidebar or create a new one.
            </p>
          </div>
        )}
      </div>
    </DashboardLayout>
  )
} 