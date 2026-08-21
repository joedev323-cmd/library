import { useState } from 'react'
import MemberRow from '../../features/members/components/MemberRow'
import MemberForm from '../../features/members/components/MemberForm'
import { getMembers } from '../../features/members/services/memberService'

function MembersPage() {
  // -----------------------------
  // State
  // -----------------------------

  const [search, setSearch] = useState('')
  const [members, setMembers] = useState(getMembers())

const [editingMember, setEditingMember] = useState(null)
const [isFormOpen, setIsFormOpen] = useState(false)

  // -----------------------------
  // Derived data
  // -----------------------------

  const filteredMembers = members.filter((member) => {
    const searchTerm = search.toLowerCase()

    return (
      member.name.toLowerCase().includes(searchTerm) ||
      member.email.toLowerCase().includes(searchTerm)
    )
  })

  // -----------------------------
  // Form handlers
  // -----------------------------

 function handleOpenAddForm() {
  setEditingMember(null)
  setIsFormOpen(true)
}

function handleEditMember(member) {
  setEditingMember(member)
  setIsFormOpen(true)
}

function handleCloseForm() {
  setEditingMember(null)
  setIsFormOpen(false)
}

  // -----------------------------
  // Save member
  // -----------------------------

  function handleSaveMember(savedMember) {
    setMembers((currentMembers) => {
      const memberExists = currentMembers.some(
        (member) => member.id === savedMember.id
      )

      if (memberExists) {
        return currentMembers.map((member) =>
          member.id === savedMember.id
            ? savedMember
            : member
        )
      }

      return [
        ...currentMembers,
        savedMember,
      ]
    })

    handleCloseForm()
  }

  // -----------------------------
  // Delete member
  // -----------------------------

  function handleDeleteMember(memberId) {
    setMembers((currentMembers) =>
      currentMembers.filter(
        (member) => member.id !== memberId
      )
    )
  }

  // -----------------------------
  // UI
  // -----------------------------

  return (
    <div>

      {/* Page header */}
      <div className="flex items-center justify-between mb-8">

        <div>
          <h1 className="text-2xl font-bold text-slate-800">
            Members
          </h1>

          <p className="text-slate-500 mt-1">
            Manage library members.
          </p>
        </div>

        <button
          type="button"
          onClick={handleOpenAddForm}
          className="bg-blue-600 text-white px-5 py-3 rounded-lg
                     font-medium hover:bg-blue-700 transition-colors"
        >
          + Add Member
        </button>

      </div>

      {/* Add / Edit form */}
      {isFormOpen && (
   <MemberForm
  member={editingMember}
  onSave={handleSaveMember}
  onCancel={handleCloseForm}
/>
      )}

      {/* Search */}
      <div className="mb-6">

        <input
          type="text"
          placeholder="Search by name or email..."
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          className="w-full bg-white border border-slate-300
                     rounded-lg px-4 py-3
                     focus:outline-none focus:ring-2
                     focus:ring-blue-500"
        />

      </div>

      {/* Members table */}
      <div className="bg-white rounded-xl border border-slate-200
                      shadow-sm overflow-hidden">

        <div className="overflow-x-auto">

          <table className="w-full">

            {/* Table header */}
            <thead className="bg-slate-50">

              <tr>

                <th
                  className="text-left px-6 py-4
                             text-sm font-semibold text-slate-600"
                >
                  Name
                </th>

                <th
                  className="text-left px-6 py-4
                             text-sm font-semibold text-slate-600"
                >
                  Email
                </th>

                <th
                  className="text-left px-6 py-4
                             text-sm font-semibold text-slate-600"
                >
                  Type
                </th>

                <th
                  className="text-left px-6 py-4
                             text-sm font-semibold text-slate-600"
                >
                  Status
                </th>

                <th
                  className="text-left px-6 py-4
                             text-sm font-semibold text-slate-600"
                >
                  Actions
                </th>

              </tr>

            </thead>

            {/* Table body */}
            <tbody>

              {filteredMembers.map((member) => (
                <MemberRow
                  key={member.id}
                  member={member}
                  onEdit={handleEditMember}
                  onDelete={handleDeleteMember}
                />
              ))}

            </tbody>

          </table>

        </div>

      </div>

      {/* Empty state */}
      {filteredMembers.length === 0 && (
        <div className="text-center py-12 text-slate-500">
          No members found.
        </div>
      )}

    </div>
  )
}

export default MembersPage