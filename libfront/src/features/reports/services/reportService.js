const API_URL =
  import.meta.env.VITE_API_URL || 'http://localhost:9090/api'

const BACKEND_URL =
  API_URL.replace(/\/api$/, '')

export async function getReports() {
  const response = await fetch(`${API_URL}/reports`)

  if (!response.ok) {
    throw new Error('Failed to load reports')
  }

  return response.json()
}

export function exportInventoryManifest() {
  window.location.href =
    `${BACKEND_URL}/reports/export-inventory`
}
