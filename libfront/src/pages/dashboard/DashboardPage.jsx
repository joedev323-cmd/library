import { Link } from 'react-router-dom'

import StatCard from '../../components/ui/StatCard'

import { getBooks } from '../../features/books/services/bookService'
import { getMembers } from '../../features/members/services/memberService'
import { getCirculationRecords } from '../../features/circulation/services/circulationService'

function DashboardPage() {

  const books = getBooks()
  const members = getMembers()
  const records = getCirculationRecords()

  const totalBooks = books.length

  const totalMembers = members.length

  const borrowedBooks = records.filter(
    (record) => record.status === 'Borrowed'
  ).length

  const overdueBooks = records.filter(
    (record) => record.status === 'Overdue'
  ).length

  const stats = [
    {
      title: 'Total Books',
      value: totalBooks,
      description: 'Books in catalogue',
    },
    {
      title: 'Members',
      value: totalMembers,
      description: 'Registered members',
    },
    {
      title: 'On Loan',
      value: borrowedBooks,
      description: 'Currently borrowed',
    },
    {
      title: 'Overdue',
      value: overdueBooks,
      description: 'Need attention',
    },
  ]

  const recentRecords = [...records]
    .sort(
      (a, b) =>
        new Date(b.borrowedDate) -
        new Date(a.borrowedDate)
    )
    .slice(0, 5)

  const overdueRecords = records
    .filter((record) => record.status === 'Overdue')
    .slice(0, 5)

  return (
    <div className="space-y-8">

      {/* Page introduction */}
      <section>

        <h1 className="text-2xl font-bold text-slate-800">
          Good morning
        </h1>

        <p className="text-slate-500 mt-1">
          Here's what's happening in the library today.
        </p>

      </section>


      {/* Statistics */}
      <section>

        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-5">

          {stats.map((stat) => (
            <StatCard
              key={stat.title}
              title={stat.title}
              value={stat.value}
              description={stat.description}
            />
          ))}

        </div>

      </section>


      {/* Quick actions */}
      <section>

        <div className="mb-4">

          <h2 className="text-lg font-semibold text-slate-800">
            Quick Actions
          </h2>

          <p className="text-sm text-slate-500">
            Common tasks for the circulation desk.
          </p>

        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">

          <Link
            to="/circulation"
            className="bg-purple-600 hover:bg-purple-700 text-white rounded-xl p-5 transition shadow-sm"
          >
            <div className="text-2xl mb-3">
              ⇄
            </div>

            <h3 className="font-semibold">
              Issue / Return
            </h3>

            <p className="text-sm text-purple-100 mt-1">
              Process a book loan or return.
            </p>
          </Link>


          <Link
            to="/catalogue"
            className="bg-white border border-slate-200 hover:border-purple-300 hover:shadow-sm rounded-xl p-5 transition"
          >
            <div className="text-2xl mb-3">
              📚
            </div>

            <h3 className="font-semibold text-slate-800">
              Catalogue
            </h3>

            <p className="text-sm text-slate-500 mt-1">
              Find books and manage copies.
            </p>
          </Link>


          <Link
            to="/members"
            className="bg-white border border-slate-200 hover:border-purple-300 hover:shadow-sm rounded-xl p-5 transition"
          >
            <div className="text-2xl mb-3">
              👥
            </div>

            <h3 className="font-semibold text-slate-800">
              Members
            </h3>

            <p className="text-sm text-slate-500 mt-1">
              Find or register a member.
            </p>
          </Link>


          <Link
            to="/reports"
            className="bg-white border border-slate-200 hover:border-purple-300 hover:shadow-sm rounded-xl p-5 transition"
          >
            <div className="text-2xl mb-3">
              📊
            </div>

            <h3 className="font-semibold text-slate-800">
              Reports
            </h3>

            <p className="text-sm text-slate-500 mt-1">
              View library activity and statistics.
            </p>
          </Link>

        </div>

      </section>


      {/* Activity area */}
      <section className="grid grid-cols-1 xl:grid-cols-2 gap-6">


        {/* Recent circulation */}
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm">

          <div className="p-6 border-b border-slate-200">

            <h2 className="text-lg font-semibold text-slate-800">
              Recent Circulation
            </h2>

            <p className="text-sm text-slate-500 mt-1">
              Recently borrowed books.
            </p>

          </div>


          <div className="divide-y divide-slate-100">

            {recentRecords.length === 0 ? (

              <div className="p-8 text-center text-sm text-slate-400">
                No circulation activity yet.
              </div>

            ) : (

              recentRecords.map((record) => (

                <div
                  key={record.id}
                  className="p-5 flex items-center justify-between gap-4"
                >

                  <div className="min-w-0">

                    <p className="font-medium text-slate-800 truncate">
                      {record.book}
                    </p>

                    <p className="text-sm text-slate-500 mt-1">
                      Borrowed by {record.member}
                    </p>

                  </div>

                  <div className="text-right shrink-0">

                    <span
                      className={`px-2.5 py-1 rounded-full text-xs font-medium ${
                        record.status === 'Borrowed'
                          ? 'bg-blue-100 text-blue-700'
                          : 'bg-red-100 text-red-700'
                      }`}
                    >
                      {record.status}
                    </span>

                    <p className="text-xs text-slate-400 mt-2">
                      Due {record.dueDate}
                    </p>

                  </div>

                </div>

              ))

            )}

          </div>

          <div className="px-6 py-4 border-t border-slate-100">

            <Link
              to="/circulation"
              className="text-sm font-medium text-purple-600 hover:text-purple-700"
            >
              View all circulation →
            </Link>

          </div>

        </div>


        {/* Overdue */}
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm">

          <div className="p-6 border-b border-slate-200">

            <h2 className="text-lg font-semibold text-slate-800">
              Overdue Books
            </h2>

            <p className="text-sm text-slate-500 mt-1">
              Loans that require attention.
            </p>

          </div>


          <div className="divide-y divide-slate-100">

            {overdueRecords.length === 0 ? (

              <div className="p-8 text-center">

                <div className="text-2xl mb-2">
                  ✓
                </div>

                <p className="text-sm font-medium text-green-700">
                  No overdue books
                </p>

                <p className="text-xs text-slate-400 mt-1">
                  Everything is currently on schedule.
                </p>

              </div>

            ) : (

              overdueRecords.map((record) => (

                <div
                  key={record.id}
                  className="p-5 flex items-center justify-between gap-4"
                >

                  <div className="min-w-0">

                    <p className="font-medium text-slate-800 truncate">
                      {record.book}
                    </p>

                    <p className="text-sm text-slate-500 mt-1">
                      {record.member}
                    </p>

                  </div>

                  <div className="text-right shrink-0">

                    <span className="px-2.5 py-1 rounded-full text-xs font-medium bg-red-100 text-red-700">
                      Overdue
                    </span>

                    <p className="text-xs text-red-500 mt-2">
                      Due {record.dueDate}
                    </p>

                  </div>

                </div>

              ))

            )}

          </div>

          <div className="px-6 py-4 border-t border-slate-100">

            <Link
              to="/reports"
              className="text-sm font-medium text-purple-600 hover:text-purple-700"
            >
              View reports →
            </Link>

          </div>

        </div>

      </section>

    </div>
  )
}

export default DashboardPage