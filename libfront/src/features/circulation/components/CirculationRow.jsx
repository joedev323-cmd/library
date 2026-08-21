function CirculationRow({ record, onReturn }) {
  const isActive =
    record.status === 'Borrowed' ||
    record.status === 'Overdue'

  const statusClass =
    record.status === 'Borrowed'
      ? 'bg-blue-100 text-blue-700'
      : record.status === 'Overdue'
        ? 'bg-red-100 text-red-700'
        : 'bg-green-100 text-green-700'

  return (
    <tr className="border-b border-slate-200">

      <td className="px-6 py-4">
        <div className="font-medium text-slate-800">
          {record.member}
        </div>
      </td>

      <td className="px-6 py-4 text-slate-600">
        {record.book}
      </td>

      <td className="px-6 py-4 text-slate-600">
        {record.borrowedDate}
      </td>

      <td className="px-6 py-4 text-slate-600">
        {record.dueDate}
      </td>

      <td className="px-6 py-4">
        <span
          className={`px-3 py-1 rounded-full text-xs font-medium ${statusClass}`}
        >
          {record.status}
        </span>
      </td>

      <td className="px-6 py-4">
        {isActive ? (
          <button
            type="button"
            onClick={() => onReturn(record.id)}
            className="text-green-600 hover:text-green-800 font-medium"
          >
            Return
          </button>
        ) : (
          <span className="text-slate-400">
            Completed
          </span>
        )}
      </td>

    </tr>
  )
}

export default CirculationRow