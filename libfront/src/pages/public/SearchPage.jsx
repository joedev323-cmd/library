import { useMemo, useState } from 'react'
import { useSearchParams } from 'react-router-dom'

import PageHeader from '../../components/layout/PageHeader'
import Card from '../../components/ui/Card'
import Badge from '../../components/ui/Badge'

import { getBooks } from '../../features/books/services/bookService'

function SearchPage() {
  const [searchParams] = useSearchParams()

  const initialQuery = searchParams.get('q') || ''

  const [query, setQuery] = useState(initialQuery)

  const books = getBooks()

  const filteredBooks = useMemo(() => {
    const search = query.trim().toLowerCase()

    if (!search) {
      return books
    }

    return books.filter((book) => {
      return (
        book.title.toLowerCase().includes(search) ||
        book.author.toLowerCase().includes(search) ||
        book.category.toLowerCase().includes(search)
      )
    })
  }, [books, query])

  return (
    <div>
      <PageHeader
        title="Catalogue"
        description="Search and explore books available in the library."
      />

      {/* Search */}
      <Card className="mb-6">
        <form
          onSubmit={(event) => event.preventDefault()}
          className="flex flex-col gap-3 sm:flex-row"
        >
          <div className="relative flex-1">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Search by title, author, or category..."
              className="
                w-full rounded-lg border border-slate-200
                bg-white px-4 py-3 text-sm text-slate-900
                placeholder:text-slate-400
                focus:border-blue-500
                focus:outline-none
                focus:ring-4
                focus:ring-blue-100
              "
            />
          </div>

          <button
            type="submit"
            className="
              rounded-lg bg-blue-600 px-5 py-3
              text-sm font-medium text-white
              transition hover:bg-blue-700
              focus:outline-none focus:ring-4
              focus:ring-blue-200
            "
          >
            Search
          </button>
        </form>
      </Card>

      {/* Results */}
      <div className="mb-4 flex items-center justify-between">
        <div>
          <h2 className="text-lg font-semibold text-slate-900">
            {query ? 'Search results' : 'All books'}
          </h2>

          <p className="mt-1 text-sm text-slate-500">
            {filteredBooks.length}{' '}
            {filteredBooks.length === 1 ? 'book' : 'books'} found
          </p>
        </div>
      </div>

      {filteredBooks.length === 0 ? (
        <Card>
          <div className="py-10 text-center">
            <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-xl bg-slate-100 text-slate-500">
              <SearchIcon />
            </div>

            <h3 className="mt-4 text-sm font-semibold text-slate-900">
              No books found
            </h3>

            <p className="mt-1 text-sm text-slate-500">
              Try searching for a different title, author, or category.
            </p>
          </div>
        </Card>
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {filteredBooks.map((book) => (
            <SearchResultCard
              key={book.id}
              book={book}
            />
          ))}
        </div>
      )}
    </div>
  )
}

function SearchResultCard({ book }) {
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

        <Badge variant={available ? 'success' : 'warning'}>
          {available ? 'Available' : 'On Loan'}
        </Badge>
      </div>

      <div className="mt-5 flex items-center justify-between">
        <Badge variant="neutral">
          {book.category}
        </Badge>

        <span className="text-xs text-slate-400">
          ISBN not available
        </span>
      </div>

      <div className="mt-5 border-t border-slate-100 pt-4">
        <p className="text-xs font-medium uppercase tracking-wide text-slate-400">
          Availability
        </p>

        <p
          className={`mt-1 text-sm font-semibold ${
            available
              ? 'text-green-600'
              : 'text-amber-600'
          }`}
        >
          {available
            ? 'Available in the library'
            : 'Currently checked out'}
        </p>
      </div>
    </Card>
  )
}

function SearchIcon() {
  return (
    <svg
      width="20"
      height="20"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <circle cx="11" cy="11" r="7" />
      <path d="m20 20-4-4" />
    </svg>
  )
}

export default SearchPage
