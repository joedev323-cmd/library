const books = [
  {
    id: 1,
    title: 'Clean Code',
    author: 'Robert C. Martin',
    category: 'Programming',
    status: 'Available',
  },
  {
    id: 2,
    title: 'Effective Java',
    author: 'Joshua Bloch',
    category: 'Programming',
    status: 'Borrowed',
  },
  {
    id: 3,
    title: 'The Pragmatic Programmer',
    author: 'Andrew Hunt',
    category: 'Programming',
    status: 'Available',
  },
  {
    id: 4,
    title: 'Design Patterns',
    author: 'Erich Gamma',
    category: 'Software Engineering',
    status: 'Available',
  },
  {
    id: 5,
    title: 'Atomic Habits',
    author: 'James Clear',
    category: 'Self Development',
    status: 'Borrowed',
  }
  
]

export function getBooks() {
  return books
}