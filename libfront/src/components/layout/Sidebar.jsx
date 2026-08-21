import { NavLink } from 'react-router-dom'
import { navigation } from '../../constants/navigation'

function Sidebar() {
  return (
    <aside className="fixed inset-y-0 left-0 z-40 hidden w-64 border-r border-slate-200 bg-white lg:block">

      {/* Brand */}
      <div className="flex h-16 items-center border-b border-slate-200 px-6">

        <a href="/" className="flex items-center gap-2">

          <span className="h-3 w-3 rounded-full bg-blue-600" />

          <span className="text-xl font-light text-slate-800">
            Biblio
            <span className="font-semibold text-blue-600">
              Hub
            </span>
          </span>

        </a>

      </div>

      {/* Navigation */}
      <nav className="p-4 space-y-1">

        {navigation.map((item) => {
          const Icon = item.icon

          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) => `
                flex items-center gap-3
                rounded-lg
                px-3 py-2.5
                text-sm font-medium
                transition-colors
                ${
                  isActive
                    ? 'bg-blue-50 text-blue-700'
                    : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                }
              `}
            >
              {Icon && (
                <Icon className="h-5 w-5" />
              )}

              <span>
                {item.label}
              </span>
            </NavLink>
          )
        })}

      </nav>

    </aside>
  )
}

export default Sidebar