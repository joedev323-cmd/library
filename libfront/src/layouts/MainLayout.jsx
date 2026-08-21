import { Outlet } from 'react-router-dom'
import Sidebar from '../components/layout/Sidebar'
import Topbar from '../components/layout/Topbar'

function MainLayout() {
  return (
    <div className="min-h-screen bg-slate-50">

      <Sidebar />

      <div className="lg:pl-64">

        <Topbar />

        <main className="px-4 py-6 sm:px-6 lg:px-8">
          <div className="mx-auto max-w-7xl">
            <Outlet />
          </div>
        </main>

      </div>

    </div>
  )
}

export default MainLayout