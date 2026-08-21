import LoginForm from '../../features/auth/components/LoginForm'
import Card from '../../components/ui/Card'

function LoginPage() {
  return (
    <div>
      {/* Intro */}
      <div className="mb-8 text-center">
        <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-xl bg-blue-50 text-blue-600">
          <span className="text-lg font-bold">
            B
          </span>
        </div>

        <p className="text-xs font-semibold uppercase tracking-widest text-blue-600">
          Staff Portal
        </p>

        <h1 className="mt-2 text-3xl font-bold tracking-tight text-slate-900">
          Welcome back
        </h1>

        <p className="mt-2 text-sm text-slate-500">
          Sign in to access the BiblioHub staff console.
        </p>
      </div>

      {/* Login card */}
      <Card className="p-6 sm:p-8">
        <LoginForm />
      </Card>

      {/* Footer note */}
      <p className="mt-6 text-center text-xs text-slate-400">
        Authorized library staff only
      </p>

      <p className="mt-2 text-center text-xs text-slate-400">
        BiblioHub Library Management System © 2026
      </p>
    </div>
  )
}

export default LoginPage
