import { Link } from 'react-router-dom'
import Badge from '../../../components/ui/Badge'

function PopularSearches({ searches = [] }) {
  if (!searches.length) return null

  return (
    <div className="flex flex-wrap items-center gap-2">
      <span className="text-xs font-medium text-slate-500">
        Popular searches:
      </span>

      {searches.map((search) => (
        <Link
          key={search}
          to={`/search?q=${encodeURIComponent(search)}`}
        >
          <Badge variant="neutral">
            {search}
          </Badge>
        </Link>
      ))}
    </div>
  )
}

export default PopularSearches
