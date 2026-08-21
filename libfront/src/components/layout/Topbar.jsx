import { useAuth } from '../../features/auth/context/AuthContext'

function Topbar() {
  const { logout } = useAuth()

  return (
    <header className="sticky top-0 z-30 border-b border-slate-200 bg-white/95 backdrop-blur">
      <div className="flex h-16 items-center justify-between px-4 sm:px-6 lg:px-8">

        {/* Mobile brand */}
        <div className="flex items-center gap-2 lg:hidden">
          <span className="h-2.5 w-2.5 rounded-full bg-blue-600" />

          <span className="text-lg font-light text-slate-800">
            Biblio
            <span className="font-semibold text-blue-600">
              Hub
            </span>
          </span>
        </div>

        {/* Desktop spacer */}
        <div className="hidden lg:block" />

        {/* Account actions */}
        <div className="flex items-center gap-3">

          <div className="flex h-9 w-9 items-center justify-center rounded-full bg-blue-50 text-sm font-semibold text-blue-700">
            L
          </div>

          <button
            type="button"
            onClick={logout}
            className="
              rounded-lg
              px-3 py-2
              text-sm font-medium
              text-slate-600
              transition-colors
              hover:bg-slate-50
              hover:text-slate-900
              focus:outline-none
              focus:ring-4
              focus:ring-blue-100
            "
          >
            Sign out
          </button>

        </div>

      </div>
    </header>
  )
}

export default Topbar
