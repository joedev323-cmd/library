const members = [
  {
    id: 1,
    name: 'John Doe',
    email: 'john@example.com',
    type: 'Student',
    status: 'Active',
  },
  {
    id: 2,
    name: 'Jane Smith',
    email: 'jane@example.com',
    type: 'Staff',
    status: 'Active',
  },
  {
    id: 3,
    name: 'Peter Kamau',
    email: 'peter@example.com',
    type: 'Student',
    status: 'Inactive',
  },
]

export function getMembers() {
  return members
}