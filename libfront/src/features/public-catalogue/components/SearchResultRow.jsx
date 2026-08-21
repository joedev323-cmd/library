import { Link } from 'react-router-dom'
import Badge from '../../../components/ui/Badge'

function SearchResultRow({ book }) {
  const available = book.status === 'Available'

  return (
    <Link
      to={`/books/${book.id}`}
      className="
        block
        border-b border-slate-200
        bg-white
        px-5 py-4
        transition-colors
        last:border-b-0
        hover:bg-slate-50
      "
    >
      <div className="flex items-start justify-between gap-4">

        <div className="min-w-0">

          <h3 className="font-semibold text-slate-900">
            {book.title}
          </h3>

          <p className="mt-1 text-sm text-slate-500">
            by {book.author}
          </p>

          <div className="mt-3">
            <Badge variant="neutral">
              {book.category}
            </Badge>
          </div>

        </div>

        <Badge variant={available ? 'success' : 'warning'}>
          {available ? 'Available' : 'On Loan'}
        </Badge>

      </div>
    </Link>
  )
}

export default SearchResultRow
