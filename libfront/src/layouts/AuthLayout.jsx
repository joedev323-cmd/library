import { Outlet } from 'react-router-dom'

function AuthLayout() {
  return (
    <div className="min-h-screen bg-slate-50">

      <div className="flex min-h-screen">

        {/* Brand panel */}
        <div className="hidden lg:flex lg:w-1/2 bg-slate-900 p-12 text-white">

          <div className="flex w-full flex-col justify-between">

            <div>

              <div className="flex items-center gap-2">

                <span className="h-3 w-3 rounded-full bg-blue-500" />

                <span className="text-xl font-light">
                  Biblio
                  <span className="font-semibold text-blue-400">
                    Hub
                  </span>
                </span>

              </div>

              <div className="mt-24 max-w-lg">

                <p className="text-sm font-medium uppercase tracking-widest text-blue-400">
                  Library Management
                </p>

                <h1 className="mt-4 text-5xl font-bold leading-tight">
                  Everything your library needs,
                  in one place.
                </h1>

                <p className="mt-6 text-lg leading-8 text-slate-400">
                  Manage books, members, circulation,
                  categories and reports from one
                  simple staff console.
                </p>

              </div>

            </div>

            <p className="text-sm text-slate-500">
              BiblioHub Library Management System © 2026
            </p>

          </div>

        </div>

        {/* Login area */}
        <div className="flex w-full items-center justify-center px-6 py-12 lg:w-1/2">

          <div className="w-full max-w-md">

            <div className="mb-8 lg:hidden">

              <div className="flex items-center gap-2">

                <span className="h-3 w-3 rounded-full bg-blue-600" />

                <span className="text-xl font-light text-slate-800">
                  Biblio
                  <span className="font-semibold text-blue-600">
                    Hub
                  </span>
                </span>

              </div>

            </div>

            <Outlet />

          </div>

        </div>

      </div>

    </div>
  )
}

export default AuthLayout