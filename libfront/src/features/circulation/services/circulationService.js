const circulationRecords = [
  {
    id: 1,
    memberId: 1,
    member: 'John Doe',
    bookId: 1,
    book: 'Clean Code',
    borrowedDate: '2026-08-10',
    dueDate: '2026-08-24',
    status: 'Borrowed',
  },
  {
    id: 2,
    memberId: 2,
    member: 'Jane Smith',
    bookId: 2,
    book: 'The Pragmatic Programmer',
    borrowedDate: '2026-08-05',
    dueDate: '2026-08-19',
    status: 'Borrowed',
  },
  {
    id: 3,
    memberId: 3,
    member: 'Peter Kamau',
    bookId: 3,
    book: 'Design Patterns',
    borrowedDate: '2026-07-20',
    dueDate: '2026-08-03',
    status: 'Overdue',
  },
]

export function getCirculationRecords() {
  return circulationRecords
}