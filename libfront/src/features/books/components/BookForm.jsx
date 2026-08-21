import { useEffect, useState } from 'react'

const defaultBook = {
  id: null,
  title: '',
  author: '',
  category: 'Programming',
  status: 'Available',
}

function BookForm({ book, onSave, onCancel }) {
  const [formData, setFormData] = useState({
    ...defaultBook,
    ...book,
  })

  const [error, setError] = useState('')

  const isEditing = book?.id != null

  // Keep form synchronized when switching
  // between Add and Edit.
  useEffect(() => {
    setFormData({
      ...defaultBook,
      ...book,
    })

    setError('')
  }, [book])

  function handleChange(event) {
    const { name, value } = event.target

    setFormData((current) => ({
      ...current,
      [name]: value,
    }))

    if (error) {
      setError('')
    }
  }

  function handleSubmit(event) {
    event.preventDefault()

    const title = formData.title.trim()
    const author = formData.author.trim()

    if (!title || !author) {
      setError('Title and author are required.')
      return
    }

    const savedBook = {
      ...formData,
      title,
      author,
    }

    onSave(savedBook)
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white border border-slate-200 rounded-xl
                 shadow-sm p-6 mb-6"
    >

      {/* Form header */}
      <div className="mb-6">

        <h2 className="text-lg font-semibold text-slate-800">
          {isEditing ? 'Edit Book' : 'Add Book'}
        </h2>

        <p className="text-sm text-slate-500 mt-1">
          {isEditing
            ? 'Update the details of this catalogue item.'
            : 'Add a new book to the catalogue.'
          }
        </p>

      </div>

      {/* Error */}
      {error && (
        <div
          className="mb-5 rounded-lg border border-red-200
                     bg-red-50 px-4 py-3 text-sm text-red-700"
        >
          {error}
        </div>
      )}

      {/* Fields */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

        {/* Title */}
        <div>
          <label
            htmlFor="book-title"
            className="block text-sm font-medium
                       text-slate-700 mb-2"
          >
            Title
          </label>

          <input
            id="book-title"
            name="title"
            type="text"
            value={formData.title}
            onChange={handleChange}
            placeholder="Book title"
            required
            className="w-full border border-slate-300 rounded-lg
                       px-4 py-3
                       focus:outline-none focus:ring-2
                       focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        {/* Author */}
        <div>
          <label
            htmlFor="book-author"
            className="block text-sm font-medium
                       text-slate-700 mb-2"
          >
            Author
          </label>

          <input
            id="book-author"
            name="author"
            type="text"
            value={formData.author}
            onChange={handleChange}
            placeholder="Author name"
            required
            className="w-full border border-slate-300 rounded-lg
                       px-4 py-3
                       focus:outline-none focus:ring-2
                       focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        {/* Category */}
        <div>
          <label
            htmlFor="book-category"
            className="block text-sm font-medium
                       text-slate-700 mb-2"
          >
            Category
          </label>

          <select
            id="book-category"
            name="category"
            value={formData.category}
            onChange={handleChange}
            className="w-full border border-slate-300 rounded-lg
                       px-4 py-3 bg-white
                       focus:outline-none focus:ring-2
                       focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="Programming">
              Programming
            </option>

            <option value="Software Engineering">
              Software Engineering
            </option>

            <option value="Self Development">
              Self Development
            </option>
          </select>
        </div>

        {/* Status */}
        <div>
          <label
            htmlFor="book-status"
            className="block text-sm font-medium
                       text-slate-700 mb-2"
          >
            Status
          </label>

          <select
            id="book-status"
            name="status"
            value={formData.status}
            onChange={handleChange}
            className="w-full border border-slate-300 rounded-lg
                       px-4 py-3 bg-white
                       focus:outline-none focus:ring-2
                       focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="Available">
              Available
            </option>

            <option value="Borrowed">
              Borrowed
            </option>
          </select>
        </div>

      </div>

      {/* Actions */}
      <div className="flex justify-end gap-3 mt-6 pt-5
                      border-t border-slate-200">

        <button
          type="button"
          onClick={onCancel}
          className="px-5 py-3 rounded-lg
                     border border-slate-300
                     text-slate-700 font-medium
                     hover:bg-slate-50 transition-colors"
        >
          Cancel
        </button>

        <button
          type="submit"
          className="px-5 py-3 rounded-lg
                     bg-blue-600 text-white font-medium
                     hover:bg-blue-700 transition-colors"
        >
          {isEditing ? 'Save Changes' : 'Add Book'}
        </button>

      </div>

    </form>
  )
}

export default BookForm