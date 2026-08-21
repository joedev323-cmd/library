import { Link } from 'react-router-dom'
import Button from '../../../components/ui/Button'

function PublicHeader() {
  return (
    <header className="border-b border-slate-200 bg-white">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">

        <Link to="/" className="flex items-center gap-2">
          <span className="h-3 w-3 rounded-full bg-blue-600" />

          <span className="text-xl font-light text-slate-800">
            Biblio
            <span className="font-semibold text-blue-600">
              Hub
            </span>
          </span>
        </Link>

        <Link to="/staff/login">
          <Button variant="secondary" size="sm">
            Staff Portal
          </Button>
        </Link>

      </div>
    </header>
  )
}

export default PublicHeader
