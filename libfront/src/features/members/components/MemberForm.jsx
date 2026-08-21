import { useState } from 'react'

function MemberForm({ member, onSave, onCancel }) {
  const [name, setName] = useState(member?.name ?? '')
  const [email, setEmail] = useState(member?.email ?? '')
  const [type, setType] = useState(member?.type ?? 'Student')

  const isEditing = member !== null

  function handleSubmit(event) {
    event.preventDefault()

    const savedMember = {
      ...(isEditing && { id: member.id }),
      name: name.trim(),
      email: email.trim(),
      type,
      ...(isEditing && { status: member.status }),
    }

    onSave(savedMember)
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white border border-slate-200 rounded-xl shadow-sm p-6 mb-6"
    >
      <h2 className="text-lg font-semibold text-slate-800 mb-6">
        {isEditing ? 'Edit Member' : 'Add Member'}
      </h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">

        {/* Name */}
        <div>
          <label
            htmlFor="member-name"
            className="block text-sm font-medium text-slate-700 mb-2"
          >
            Full Name
          </label>

          <input
            id="member-name"
            type="text"
            value={name}
            onChange={(event) => setName(event.target.value)}
            placeholder="Member name"
            className="w-full border border-slate-300 rounded-lg px-4 py-3
                       focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>

        {/* Email */}
        <div>
          <label
            htmlFor="member-email"
            className="block text-sm font-medium text-slate-700 mb-2"
          >
            Email
          </label>

          <input
            id="member-email"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            placeholder="member@example.com"
            className="w-full border border-slate-300 rounded-lg px-4 py-3
                       focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>

        {/* Member type */}
        <div>
          <label
            htmlFor="member-type"
            className="block text-sm font-medium text-slate-700 mb-2"
          >
            Member Type
          </label>

          <select
            id="member-type"
            value={type}
            onChange={(event) => setType(event.target.value)}
            className="w-full border border-slate-300 rounded-lg px-4 py-3
                       focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="Student">Student</option>
            <option value="Staff">Staff</option>
          </select>
        </div>

      </div>

      <div className="flex justify-end gap-3 mt-6">

        <button
          type="button"
          onClick={onCancel}
          className="px-5 py-3 rounded-lg border border-slate-300
                     text-slate-700 hover:bg-slate-50"
        >
          Cancel
        </button>

        <button
          type="submit"
          className="px-5 py-3 rounded-lg bg-blue-600 text-white
                     font-medium hover:bg-blue-700"
        >
          {isEditing ? 'Save Changes' : 'Add Member'}
        </button>

      </div>
    </form>
  )
}

export default MemberForm