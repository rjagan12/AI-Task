import { Dashboard } from '@/lib/store/dashboard-store'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

export async function fetchDashboards(): Promise<Dashboard[]> {
  const response = await fetch(`${API_BASE_URL}/api/v1/dashboards`, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
    },
  })

  if (!response.ok) {
    throw new Error('Failed to fetch dashboards')
  }

  return response.json()
}

export async function createDashboard(dashboard: Partial<Dashboard>): Promise<Dashboard> {
  const response = await fetch(`${API_BASE_URL}/api/v1/dashboards`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
    },
    body: JSON.stringify(dashboard),
  })

  if (!response.ok) {
    throw new Error('Failed to create dashboard')
  }

  return response.json()
}

export async function updateDashboard(id: string, updates: Partial<Dashboard>): Promise<Dashboard> {
  const response = await fetch(`${API_BASE_URL}/api/v1/dashboards/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
    },
    body: JSON.stringify(updates),
  })

  if (!response.ok) {
    throw new Error('Failed to update dashboard')
  }

  return response.json()
}

export async function deleteDashboard(id: string): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/api/v1/dashboards/${id}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
    },
  })

  if (!response.ok) {
    throw new Error('Failed to delete dashboard')
  }
} 