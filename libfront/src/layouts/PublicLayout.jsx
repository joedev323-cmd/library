import { Link, Outlet } from 'react-router-dom'

function PublicLayout() {
  return (
    <div className="flex min-h-screen flex-col bg-slate-50">

      {/* Header */}
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex h-16 w-full max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">

          <Link to="/" className="flex items-center gap-2">
            <span className="h-3 w-3 rounded-full bg-blue-600" />

            <span className="text-xl font-light text-slate-800">
              Biblio
              <span className="font-semibold text-blue-600">
                Hub
              </span>
            </span>
          </Link>

          <Link
            to="/staff/login"
            className="
              inline-flex items-center justify-center
              rounded-lg
              bg-blue-600
              px-4 py-2
              text-sm font-medium
              text-white
              transition-colors
              hover:bg-blue-700
              focus:outline-none
              focus:ring-4
              focus:ring-blue-100
            "
          >
            Staff Portal
          </Link>

        </div>
      </header>

      {/* Page content */}
      <main className="flex-1">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-200 bg-white">
        <div className="mx-auto max-w-7xl px-4 py-5 text-center sm:px-6 lg:px-8">
          <p className="text-xs text-slate-400">
            BiblioHub Library Management System © 2026
          </p>
        </div>
      </footer>

    </div>
  )
}

export default PublicLayout
