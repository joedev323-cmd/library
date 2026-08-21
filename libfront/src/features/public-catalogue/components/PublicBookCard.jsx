import Card from '../../../components/ui/Card'
import Badge from '../../../components/ui/Badge'

function PublicBookCard({ book }) {
  const available = book.status === 'Available'

  return (
    <Card className="transition-shadow hover:shadow-md">

      <div className="flex items-start justify-between gap-4">

        <div className="min-w-0">

          <h3 className="truncate text-lg font-semibold text-slate-900">
            {book.title}
          </h3>

          <p className="mt-1 text-sm text-slate-500">
            by {book.author}
          </p>

        </div>

        <Badge
          variant={available ? 'success' : 'warning'}
        >
          {available ? 'Available' : 'On Loan'}
        </Badge>

      </div>

      <div className="mt-5">
        <Badge variant="neutral">
          {book.category}
        </Badge>
      </div>

      <div className="mt-6 border-t border-slate-100 pt-4">

        <p className="text-xs font-medium uppercase tracking-wide text-slate-400">
          Availability
        </p>

        <p
          className={`
            mt-1 text-sm font-semibold
            ${
              available
                ? 'text-green-600'
                : 'text-amber-600'
            }
          `}
        >
          {available
            ? 'Available in the library'
            : 'Currently checked out'}
        </p>

      </div>

    </Card>
  )
}

export default PublicBookCard