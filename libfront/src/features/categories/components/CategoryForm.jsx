import { useEffect, useState } from 'react'

const defaultCategory = {
  id: null,
  name: '',
  description: '',
}

function CategoryForm({
  category,
  onSave,
  onCancel,
}) {
  const [formData, setFormData] = useState({
    ...defaultCategory,
    ...category,
  })

  const [error, setError] = useState('')

  const isEditing = category?.id != null

  useEffect(() => {
    setFormData({
      ...defaultCategory,
      ...category,
    })

    setError('')
  }, [category])

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

    const name = formData.name.trim()
    const description = formData.description.trim()

    if (!name) {
      setError('Category name is required.')
      return
    }

    onSave({
      ...formData,
      name,
      description,
    })
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white border border-slate-200
                 rounded-xl shadow-sm p-6 mb-6"
    >
      {/* Header */}
      <div className="mb-6">
        <h2 className="text-lg font-semibold text-slate-800">
          {isEditing ? 'Edit Category' : 'Add Category'}
        </h2>

        <p className="text-sm text-slate-500 mt-1">
          {isEditing
            ? 'Update this catalogue category.'
            : 'Create a new category for your books.'}
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

      {/* Name */}
      <div className="mb-5">
        <label
          htmlFor="category-name"
          className="block text-sm font-medium
                     text-slate-700 mb-2"
        >
          Category Name
        </label>

        <input
          id="category-name"
          name="name"
          type="text"
          value={formData.name}
          onChange={handleChange}
          placeholder="e.g. Programming"
          required
          className="w-full border border-slate-300
                     rounded-lg px-4 py-3
                     focus:outline-none focus:ring-2
                     focus:ring-blue-500
                     focus:border-blue-500"
        />
      </div>

      {/* Description */}
      <div>
        <label
          htmlFor="category-description"
          className="block text-sm font-medium
                     text-slate-700 mb-2"
        >
          Description
        </label>

        <textarea
          id="category-description"
          name="description"
          value={formData.description}
          onChange={handleChange}
          placeholder="Describe this category..."
          rows={4}
          className="w-full border border-slate-300
                     rounded-lg px-4 py-3
                     resize-none
                     focus:outline-none focus:ring-2
                     focus:ring-blue-500
                     focus:border-blue-500"
        />
      </div>

      {/* Actions */}
      <div
        className="flex justify-end gap-3 mt-6 pt-5
                   border-t border-slate-200"
      >
        <button
          type="button"
          onClick={onCancel}
          className="px-5 py-3 rounded-lg
                     border border-slate-300
                     text-slate-700 font-medium
                     hover:bg-slate-50
                     transition-colors"
        >
          Cancel
        </button>

        <button
          type="submit"
          className="px-5 py-3 rounded-lg
                     bg-blue-600 text-white
                     font-medium hover:bg-blue-700
                     transition-colors"
        >
          {isEditing ? 'Save Changes' : 'Add Category'}
        </button>
      </div>
    </form>
  )
}

export default CategoryForm