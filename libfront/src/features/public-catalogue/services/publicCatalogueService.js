import { getBooks } from '../../books/services/bookService'

export function searchBooks(query = '') {
  const books = getBooks()
  const normalizedQuery = query.trim().toLowerCase()

  if (!normalizedQuery) {
    return books
  }

  return books.filter((book) => {
    return (
      book.title.toLowerCase().includes(normalizedQuery) ||
      book.author.toLowerCase().includes(normalizedQuery) ||
      book.category.toLowerCase().includes(normalizedQuery)
    )
  })
}

export function getPopularBooks(limit = 3) {
  return getBooks().slice(0, limit)
}

export function getNewArrivals(limit = 3) {
  return [...getBooks()].reverse().slice(0, limit)
}

export function getCategories() {
  return [...new Set(getBooks().map((book) => book.category))]
}

export function getAvailableBookCount() {
  return getBooks().filter(
    (book) => book.status === 'Available'
  ).length
}
