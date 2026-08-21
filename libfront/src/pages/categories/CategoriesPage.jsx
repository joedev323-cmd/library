import { useState } from 'react'
import PageHeader from '../../components/layout/PageHeader'
import Card from '../../components/ui/Card'
import EmptyState from '../../components/ui/EmptyState'

import CategoryForm from '../../features/categories/components/CategoryForm'
import CategoryRow from '../../features/categories/components/CategoryRow'
import { getCategories } from '../../features/categories/Services/categoryService'

function CategoriesPage() {
  const [categories, setCategories] = useState(() => getCategories())
  const [editingCategory, setEditingCategory] = useState(null)
  const [showForm, setShowForm] = useState(false)

  function handleAdd() {
    setEditingCategory(null)
    setShowForm(true)
  }

  function handleEdit(category) {
    setEditingCategory(category)
    setShowForm(true)
  }

  function handleCancel() {
    setEditingCategory(null)
    setShowForm(false)
  }

  function handleSave(categoryData) {
    if (categoryData.id == null) {
      const newCategory = {
        ...categoryData,
        id: Date.now(),
      }

      setCategories((current) => [
        ...current,
        newCategory,
      ])
    } else {
      setCategories((current) =>
        current.map((category) =>
          category.id === categoryData.id
            ? categoryData
            : category
        )
      )
    }

    handleCancel()
  }

  function handleDelete(categoryId) {
    setCategories((current) =>
      current.filter(
        (category) => category.id !== categoryId
      )
    )
  }

  return (
    <div>
      <PageHeader
        title="Categories"
        description="Manage the categories used to organize your library catalogue."
        action={
          !showForm && (
            <button
              type="button"
              onClick={handleAdd}
              className="inline-flex items-center justify-center rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-blue-700 focus:outline-none focus:ring-4 focus:ring-blue-200"
            >
              Add Category
            </button>
          )
        }
      />

      {showForm && (
        <CategoryForm
          category={editingCategory}
          onSave={handleSave}
          onCancel={handleCancel}
        />
      )}

      <Card className="overflow-hidden p-0">
        {categories.length === 0 ? (
          <div className="p-8">
            <EmptyState
              title="No categories"
              description="Create your first category to organize your books."
              action={
                <button
                  type="button"
                  onClick={handleAdd}
                  className="rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium text-white hover:bg-blue-700"
                >
                  Add Category
                </button>
              }
            />
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-slate-50">
                <tr>
                  <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                    Category
                  </th>

                  <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                    Description
                  </th>

                  <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                    Actions
                  </th>
                </tr>
              </thead>

              <tbody>
                {categories.map((category) => (
                  <CategoryRow
                    key={category.id}
                    category={category}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                  />
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  )
}

export default CategoriesPage
