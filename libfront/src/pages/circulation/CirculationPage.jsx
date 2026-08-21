import { useState } from 'react'

import { getBooks } from '../../features/books/services/bookService'
import { getMembers } from '../../features/members/services/memberService'
import {
  getCirculationRecords,
} from '../../features/circulation/services/circulationService'

function CirculationPage() {
  // -----------------------------
  // Data
  // -----------------------------

  const books = getBooks()
  const members = getMembers()

  // -----------------------------
  // State
  // -----------------------------

  const [records, setRecords] = useState(
    getCirculationRecords()
  )

  const [selectedMember, setSelectedMember] = useState('')
  const [selectedBook, setSelectedBook] = useState('')
  const [dueDate, setDueDate] = useState('')

  // -----------------------------
  // Borrow book
  // -----------------------------

  function handleBorrowBook(event) {
    event.preventDefault()

    // Basic validation
    if (
      !selectedMember ||
      !selectedBook ||
      !dueDate
    ) {
      alert('Please complete all fields.')
      return
    }

    const bookId = Number(selectedBook)
    const memberId = Number(selectedMember)

    // Find selected book
    const book = books.find(
      (book) => book.id === bookId
    )

    // Find selected member
    const member = members.find(
      (member) => member.id === memberId
    )

    // Safety check
    if (!book || !member) {
      alert('Book or member could not be found.')
      return
    }

    // Check whether book is already borrowed
    const existingLoan = records.find(
      (record) =>
        record.bookId === bookId &&
        (
          record.status === 'Borrowed' ||
          record.status === 'Overdue'
        )
    )

    if (existingLoan) {
      alert('This book is currently unavailable.')
      return
    }

    // Create new circulation record
    const newRecord = {
      id: Date.now(),

      memberId: member.id,
      member: member.name,

      bookId: book.id,
      book: book.title,

      borrowedDate: new Date()
        .toISOString()
        .split('T')[0],

      dueDate,

      status: 'Borrowed',
    }

    // Add record to state
    setRecords((currentRecords) => [
      ...currentRecords,
      newRecord,
    ])

    // Reset form
    setSelectedMember('')
    setSelectedBook('')
    setDueDate('')
  }

  // -----------------------------
  // Return book
  // -----------------------------

  function handleReturnBook(recordId) {
    setRecords((currentRecords) =>
      currentRecords.map((record) =>
        record.id === recordId
          ? {
              ...record,
              status: 'Returned',
              returnedDate: new Date()
                .toISOString()
                .split('T')[0],
            }
          : record
      )
    )
  }

  // -----------------------------
  // UI
  // -----------------------------

  return (
    <div>

      {/* Page header */}
      <div className="mb-8">

        <h1 className="text-2xl font-bold text-slate-800">
          Circulation
        </h1>

        <p className="text-slate-500 mt-1">
          Manage book borrowing and returns.
        </p>

      </div>

      {/* Borrow form */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 mb-8">

        <h2 className="text-lg font-semibold text-slate-800 mb-6">
          Borrow a Book
        </h2>

        <form
          onSubmit={handleBorrowBook}
          className="grid grid-cols-1 md:grid-cols-3 gap-4"
        >

          {/* Member */}
          <div>

            <label
              htmlFor="member"
              className="block text-sm font-medium text-slate-700 mb-2"
            >
              Member
            </label>

            <select
              id="member"
              value={selectedMember}
              onChange={(event) =>
                setSelectedMember(event.target.value)
              }
              className="w-full border border-slate-300 rounded-lg px-4 py-3
                         focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">
                Select member
              </option>

              {members.map((member) => (
                <option
                  key={member.id}
                  value={member.id}
                >
                  {member.name}
                </option>
              ))}
            </select>

          </div>

          {/* Book */}
          <div>

            <label
              htmlFor="book"
              className="block text-sm font-medium text-slate-700 mb-2"
            >
              Book
            </label>

            <select
              id="book"
              value={selectedBook}
              onChange={(event) =>
                setSelectedBook(event.target.value)
              }
              className="w-full border border-slate-300 rounded-lg px-4 py-3
                         focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">
                Select book
              </option>

              {books.map((book) => {

                const unavailable = records.some(
                  (record) =>
                    record.bookId === book.id &&
                    (
                      record.status === 'Borrowed' ||
                      record.status === 'Overdue'
                    )
                )

                return (
                  <option
                    key={book.id}
                    value={book.id}
                    disabled={unavailable}
                  >
                    {book.title}
                    {unavailable
                      ? ' (Unavailable)'
                      : ''}
                  </option>
                )
              })}

            </select>

          </div>

          {/* Due date */}
          <div>

            <label
              htmlFor="dueDate"
              className="block text-sm font-medium text-slate-700 mb-2"
            >
              Due Date
            </label>

            <input
              id="dueDate"
              type="date"
              value={dueDate}
              onChange={(event) =>
                setDueDate(event.target.value)
              }
              className="w-full border border-slate-300 rounded-lg px-4 py-3
                         focus:outline-none focus:ring-2 focus:ring-blue-500"
            />

          </div>

          {/* Submit */}
          <div className="md:col-span-3 flex justify-end">

            <button
              type="submit"
              className="bg-blue-600 text-white px-6 py-3 rounded-lg
                         font-medium hover:bg-blue-700"
            >
              Borrow Book
            </button>

          </div>

        </form>

      </div>

      {/* Circulation records */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">

        <div className="p-6 border-b border-slate-200">

          <h2 className="text-lg font-semibold text-slate-800">
            Circulation Records
          </h2>

        </div>

        <div className="overflow-x-auto">

          <table className="w-full">

            <thead className="bg-slate-50">

              <tr>

                <th className="text-left px-6 py-4 text-sm font-semibold text-slate-600">
                  Member
                </th>

                <th className="text-left px-6 py-4 text-sm font-semibold text-slate-600">
                  Book
                </th>

                <th className="text-left px-6 py-4 text-sm font-semibold text-slate-600">
                  Borrowed
                </th>

                <th className="text-left px-6 py-4 text-sm font-semibold text-slate-600">
                  Due
                </th>

                <th className="text-left px-6 py-4 text-sm font-semibold text-slate-600">
                  Status
                </th>

                <th className="text-left px-6 py-4 text-sm font-semibold text-slate-600">
                  Action
                </th>

              </tr>

            </thead>

            <tbody>

              {records.map((record) => (

                <tr
                  key={record.id}
                  className="border-t border-slate-200"
                >

                  {/* Member */}
                  <td className="px-6 py-4 text-slate-700">
                    {record.member}
                  </td>

                  {/* Book */}
                  <td className="px-6 py-4 text-slate-700">
                    {record.book}
                  </td>

                  {/* Borrowed date */}
                  <td className="px-6 py-4 text-slate-600">
                    {record.borrowedDate}
                  </td>

                  {/* Due date */}
                  <td className="px-6 py-4 text-slate-600">
                    {record.dueDate}
                  </td>

                  {/* Status */}
                  <td className="px-6 py-4">

                    <span
                      className={`px-3 py-1 rounded-full text-xs font-medium ${
                        record.status === 'Borrowed'
                          ? 'bg-blue-100 text-blue-700'
                          : record.status === 'Overdue'
                            ? 'bg-red-100 text-red-700'
                            : 'bg-green-100 text-green-700'
                      }`}
                    >
                      {record.status}
                    </span>

                  </td>

                  {/* Action */}
                  <td className="px-6 py-4">

                    {(
                      record.status === 'Borrowed' ||
                      record.status === 'Overdue'
                    ) && (

                      <button
                        type="button"
                        onClick={() =>
                          handleReturnBook(record.id)
                        }
                        className="text-green-600 hover:text-green-800 font-medium"
                      >
                        Return
                      </button>

                    )}

                    {record.status === 'Returned' && (
                      <span className="text-slate-400">
                        Completed
                      </span>
                    )}

                  </td>

                </tr>

              ))}

            </tbody>

          </table>

        </div>

        {/* Empty state */}
        {records.length === 0 && (
          <div className="text-center py-12 text-slate-500">
            No circulation records found.
          </div>
        )}

      </div>

    </div>
  )
}

export default CirculationPage