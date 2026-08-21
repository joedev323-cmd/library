function CategoryRow({
  category,
  onEdit,
  onDelete,
}) {
  function handleDelete() {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${category.name}"?`
    )

    if (confirmed) {
      onDelete(category.id)
    }
  }

  return (
    <tr
      className="border-b border-slate-200
                 last:border-b-0 hover:bg-slate-50
                 transition-colors"
    >
      {/* Name */}
      <td className="px-6 py-4">
        <div className="font-medium text-slate-800">
          {category.name}
        </div>
      </td>

      {/* Description */}
      <td className="px-6 py-4">
        <p className="text-sm text-slate-600 max-w-xl">
          {category.description || (
            <span className="text-slate-400 italic">
              No description
            </span>
          )}
        </p>
      </td>

      {/* Actions */}
      <td className="px-6 py-4">
        <div className="flex items-center gap-4">
          <button
            type="button"
            onClick={() => onEdit(category)}
            className="text-sm text-blue-600
                       hover:text-blue-800
                       font-medium transition-colors"
          >
            Edit
          </button>

          <button
            type="button"
            onClick={handleDelete}
            className="text-sm text-red-600
                       hover:text-red-800
                       font-medium transition-colors"
          >
            Delete
          </button>
        </div>
      </td>
    </tr>
  )
}

export default CategoryRow