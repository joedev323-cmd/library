import { useMemo, useState } from 'react'
import BookCard from '../../features/books/components/BookCard'
import BookForm from '../../features/books/components/BookForm'
import { getBooks } from '../../features/books/services/bookService'

function CataloguePage() {
  const [books, setBooks] = useState(() => [...getBooks()])
  const [search, setSearch] = useState('')
  const [category, setCategory] = useState('All')
  const [editingBook, setEditingBook] = useState(null)

  // -----------------------------
  // Categories
  // -----------------------------

  const categories = useMemo(() => {
    const uniqueCategories = new Set(
      books.map((book) => book.category)
    )

    return ['All', ...uniqueCategories]
  }, [books])

  // -----------------------------
  // Filtered books
  // -----------------------------

  const filteredBooks = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase()

    return books.filter((book) => {
      const matchesSearch =
        book.title.toLowerCase().includes(normalizedSearch) ||
        book.author.toLowerCase().includes(normalizedSearch)

      const matchesCategory =
        category === 'All' ||
        book.category === category

      return matchesSearch && matchesCategory
    })
  }, [books, search, category])

  // -----------------------------
  // Book actions
  // -----------------------------

  function handleOpenAddForm() {
    setEditingBook({
      id: null,
      title: '',
      author: '',
      category: '',
      status: 'Available',
    })
  }

  function handleEditBook(book) {
    setEditingBook({ ...book })
  }

  function handleCloseForm() {
    setEditingBook(null)
  }

  function handleSaveBook(savedBook) {
    setBooks((currentBooks) => {
      const isEditing = savedBook.id !== null

      if (isEditing) {
        return currentBooks.map((book) =>
          book.id === savedBook.id
            ? savedBook
            : book
        )
      }

      const newBook = {
        ...savedBook,
        id: Date.now(),
      }

      return [...currentBooks, newBook]
    })

    setEditingBook(null)
  }

  function handleDeleteBook(bookId) {
    const book = books.find(
      (item) => item.id === bookId
    )

    if (!book) {
      return
    }

    const confirmed = window.confirm(
      `Are you sure you want to delete "${book.title}"?`
    )

    if (!confirmed) {
      return
    }

    setBooks((currentBooks) =>
      currentBooks.filter(
        (item) => item.id !== bookId
      )
    )
  }

  return (
    <div className="space-y-6">

      {/* Page header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">

        <div>
          <h1 className="text-2xl font-bold text-slate-800">
            Catalogue
          </h1>

          <p className="text-slate-500 mt-1">
            Browse and manage the library collection.
          </p>
        </div>

        <button
          type="button"
          onClick={handleOpenAddForm}
          className="bg-blue-600 text-white px-5 py-3 rounded-lg
                     font-medium hover:bg-blue-700
                     transition-colors"
        >
          + Add Book
        </button>

      </div>

      {/* Book form */}
      {editingBook !== null && (
        <BookForm
          book={editingBook}
          onSave={handleSaveBook}
          onCancel={handleCloseForm}
        />
      )}

      {/* Search / filter */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-4">
        <div className="flex flex-col md:flex-row gap-4">

          <div className="flex-1">
            <label
              htmlFor="book-search"
              className="sr-only"
            >
              Search books
            </label>

            <input
              id="book-search"
              type="search"
              placeholder="Search by title or author..."
              value={search}
              onChange={(event) =>
                setSearch(event.target.value)
              }
              className="w-full border border-slate-300 rounded-lg
                         px-4 py-3
                         focus:outline-none focus:ring-2
                         focus:ring-blue-500"
            />
          </div>

          <div className="md:w-56">
            <label
              htmlFor="category-filter"
              className="sr-only"
            >
              Filter by category
            </label>

            <select
              id="category-filter"
              value={category}
              onChange={(event) =>
                setCategory(event.target.value)
              }
              className="w-full bg-white border border-slate-300
                         rounded-lg px-4 py-3
                         focus:outline-none focus:ring-2
                         focus:ring-blue-500"
            >
              {categories.map((categoryName) => (
                <option
                  key={categoryName}
                  value={categoryName}
                >
                  {categoryName}
                </option>
              ))}
            </select>
          </div>

        </div>

        <div className="mt-3 text-sm text-slate-500">
          Showing {filteredBooks.length} of {books.length} books
        </div>
      </div>

      {/* Books */}
      {filteredBooks.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">

          {filteredBooks.map((book) => (
            <BookCard
              key={book.id}
              book={book}
              onEdit={handleEditBook}
              onDelete={handleDeleteBook}
            />
          ))}

        </div>
      ) : (
        <div className="bg-white rounded-xl border border-slate-200
                        text-center py-16">

          <div className="text-4xl mb-4">
            📚
          </div>

          <h3 className="text-lg font-semibold text-slate-800">
            No books found
          </h3>

          <p className="text-slate-500 mt-1">
            Try changing your search or category filter.
          </p>

        </div>
      )}

    </div>
  )
}

export default CataloguePage