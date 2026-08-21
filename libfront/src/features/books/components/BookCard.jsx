function BookCard({ book, onEdit, onDelete }) {
  const isAvailable = book.status === 'Available'

  return (
    <article className="bg-white rounded-xl border border-slate-200
                        shadow-sm p-6 hover:shadow-md
                        transition-shadow">

      {/* Header */}
      <div className="flex items-start justify-between gap-4">

        <div className="min-w-0">
          <h3 className="text-lg font-semibold text-slate-800 truncate">
            {book.title}
          </h3>

          <p className="text-sm text-slate-500 mt-1">
            {book.author}
          </p>
        </div>

        <span
          className={`shrink-0 px-3 py-1 rounded-full
                      text-xs font-medium ${
            isAvailable
              ? 'bg-green-100 text-green-700'
              : 'bg-orange-100 text-orange-700'
          }`}
        >
          {book.status}
        </span>

      </div>

      {/* Metadata */}
      <div className="mt-5">
        <span className="inline-flex px-3 py-1 rounded-full
                         bg-slate-100 text-slate-600
                         text-xs font-medium">
          {book.category}
        </span>
      </div>

      {/* Actions */}
      <div className="grid grid-cols-2 gap-3 mt-6">

        <button
          type="button"
          onClick={() => onEdit(book)}
          className="border border-blue-600 text-blue-600
                     py-2 rounded-lg font-medium
                     hover:bg-blue-50 transition-colors"
        >
          Edit
        </button>

        <button
          type="button"
          onClick={() => onDelete(book.id)}
          className="border border-red-600 text-red-600
                     py-2 rounded-lg font-medium
                     hover:bg-red-50 transition-colors"
        >
          Delete
        </button>

      </div>

    </article>
  )
}

export default BookCard