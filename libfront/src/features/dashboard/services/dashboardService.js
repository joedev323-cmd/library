const recentActivity = [
  {
    id: 1,
    member: 'John Doe',
    action: 'Borrowed',
    book: 'Clean Code',
    date: 'Today, 10:30 AM',
  },
  {
    id: 2,
    member: 'Jane Smith',
    action: 'Returned',
    book: 'Effective Java',
    date: 'Today, 9:15 AM',
  },
  {
    id: 3,
    member: 'Peter Kamau',
    action: 'Borrowed',
    book: 'The Pragmatic Programmer',
    date: 'Yesterday, 4:45 PM',
  },
  {
    id: 4,
    member: 'Mary Wanjiku',
    action: 'Returned',
    book: 'Design Patterns',
    date: 'Yesterday, 2:20 PM',
  },
]

export function getRecentActivity() {
  return recentActivity
}