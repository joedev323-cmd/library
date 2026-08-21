 function MemberRow({ member, onEdit, onDelete }) {
  return (
    <tr className="border-b border-slate-200">

      <td className="px-6 py-4">
        <div className="font-medium text-slate-800">
          {member.name}
        </div>
      </td>

      <td className="px-6 py-4 text-slate-600">
        {member.email}
      </td>

      <td className="px-6 py-4">
        <span className="bg-blue-100 text-blue-700 px-3 py-1 rounded-full text-xs font-medium">
          {member.type}
        </span>
      </td>

      <td className="px-6 py-4">
        <span
          className={`px-3 py-1 rounded-full text-xs font-medium ${
            member.status === 'Active'
              ? 'bg-green-100 text-green-700'
              : 'bg-slate-100 text-slate-600'
          }`}
        >
          {member.status}
        </span>
      </td>

      <td className="px-6 py-4">

        <div className="flex gap-3">

          <button
            type="button"
            onClick={() => onEdit(member)}
            className="text-blue-600 hover:text-blue-800"
          >
            Edit
          </button>

          <button
            type="button"
            onClick={() => onDelete(member.id)}
            className="text-red-600 hover:text-red-800"
          >
            Delete
          </button>

        </div>

      </td>

    </tr>
  )
}

export default MemberRow