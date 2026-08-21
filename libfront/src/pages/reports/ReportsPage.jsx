import { useEffect, useState } from 'react'

import ReportMetric from '../../features/reports/components/ReportMetric'

import {
  getReports,
  exportInventoryManifest,
} from '../../features/reports/services/reportService'

function ReportsPage() {
  const [report, setReport] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadReports() {
      try {
        setLoading(true)
        setError('')

        const data = await getReports()

        setReport(data)
      } catch (err) {
        console.error(err)
        setError('Unable to load reports from the server.')
      } finally {
        setLoading(false)
      }
    }

    loadReports()
  }, [])

  // ----------------------------------------
  // Loading
  // ----------------------------------------

  if (loading) {
    return (
      <div>
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-slate-800">
            Reports
          </h1>

          <p className="mt-1 text-slate-500">
            Library activity and operational analytics.
          </p>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white p-8 text-center shadow-sm">
          <p className="text-sm text-slate-500">
            Loading reports...
          </p>
        </div>
      </div>
    )
  }

  // ----------------------------------------
  // Error
  // ----------------------------------------

  if (error) {
    return (
      <div>
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-slate-800">
            Reports
          </h1>

          <p className="mt-1 text-slate-500">
            Library activity and operational analytics.
          </p>
        </div>

        <div className="rounded-xl border border-red-200 bg-red-50 p-6">
          <h2 className="font-semibold text-red-800">
            Unable to load reports
          </h2>

          <p className="mt-1 text-sm text-red-600">
            {error}
          </p>
        </div>
      </div>
    )
  }

  if (!report) {
    return null
  }

  // ----------------------------------------
  // Backend response
  // ----------------------------------------

  const summary = report.summary || {}

  const popularCategories =
    report.popularCategories || []

  const overdueLoans =
    report.overdueLoans || []

  const totalCopies =
    Number(summary.totalCopies || 0)

  const availableCopies =
    Number(summary.availableCopies || 0)

  const activeLoans =
    Number(summary.activeLoans || 0)

  const overdueCount =
    Number(summary.overdueLoans || 0)

  // ----------------------------------------
  // Inventory state
  // ----------------------------------------

  const inventoryAvailable =
    summary.inventoryAvailable === true

  // ----------------------------------------
  // Main metrics
  // ----------------------------------------

  const metrics = [
    {
      label: 'Books in Catalogue',
      value: summary.totalBooks || 0,
    },
    {
      label: 'Physical Copies',
      value: totalCopies,
    },
    {
      label: 'Registered Members',
      value: summary.totalMembers || 0,
    },
    {
      label: 'Total Loans',
      value: summary.totalLoans || 0,
    },
    {
      label: 'Active Loans',
      value: activeLoans,
    },
    {
      label: 'Returned Loans',
      value: summary.returnedLoans || 0,
    },
  ]

  // ----------------------------------------
  // Inventory percentages
  // ----------------------------------------

  const inventoryRows = inventoryAvailable
    ? [
        {
          label: 'Available in Library',
          value: Number(
            summary.availablePercentage || 0
          ),
          color: 'bg-green-500',
        },
        {
          label: 'Currently on Loan',
          value: Number(
            summary.activeLoanPercentage || 0
          ),
          color: 'bg-blue-500',
        },
        {
          label: 'Overdue',
          value: Number(
            summary.overduePercentage || 0
          ),
          color: 'bg-red-500',
        },
      ]
    : []

  return (
    <div>

      {/* ===================================== */}
      {/* Header */}
      {/* ===================================== */}

      <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">

        <div>
          <h1 className="text-2xl font-bold text-slate-800">
            Reports
          </h1>

          <p className="mt-1 text-slate-500">
            Library activity and operational analytics.
          </p>
        </div>

        <div className="inline-flex w-fit items-center rounded-lg bg-blue-50 px-3 py-2 text-sm font-medium text-blue-600">
          Live Operational Analytics
        </div>

      </div>

      {/* ===================================== */}
      {/* Main metrics */}
      {/* ===================================== */}

      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">

        {metrics.map((metric) => (
          <ReportMetric
            key={metric.label}
            label={metric.label}
            value={metric.value}
          />
        ))}

      </div>

      {/* ===================================== */}
      {/* Financial + inventory */}
      {/* ===================================== */}

      <div className="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-2">

        {/* Financial */}

        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">

          <h2 className="text-lg font-semibold text-slate-800">
            Financial Summary
          </h2>

          <p className="mt-1 text-sm text-slate-500">
            Fines collected during the current month.
          </p>

          <div className="mt-6">

            <p className="text-3xl font-bold text-slate-800">
              KSh{' '}
              {Number(
                summary.finesCollectedMtd || 0
              ).toFixed(2)}
            </p>

            <p className="mt-2 text-sm text-slate-500">
              Fines collected this month
            </p>

          </div>

        </div>

        {/* Inventory */}

        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">

          <div className="flex items-start justify-between">

            <div>
              <h2 className="text-lg font-semibold text-slate-800">
                Inventory Status
              </h2>

              <p className="mt-1 text-sm text-slate-500">
                Current physical copy distribution.
              </p>
            </div>

            {!inventoryAvailable && (
              <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-500">
                No inventory
              </span>
            )}

          </div>

          {!inventoryAvailable ? (
            <div className="mt-6 rounded-lg border border-dashed border-slate-200 bg-slate-50 p-5">

              <p className="text-sm font-medium text-slate-700">
                No physical copies have been added yet.
              </p>

              <p className="mt-1 text-sm text-slate-500">
                Inventory percentages will appear here once
                books have been accessioned into the library.
              </p>

            </div>
          ) : (
            <div className="mt-6 space-y-4">

              {inventoryRows.map((row) => (
                <InventoryRow
                  key={row.label}
                  label={row.label}
                  value={row.value}
                  color={row.color}
                />
              ))}

            </div>
          )}

        </div>

      </div>

      {/* ===================================== */}
      {/* Popular categories */}
      {/* ===================================== */}

      <div className="mt-8 rounded-xl border border-slate-200 bg-white p-6 shadow-sm">

        <div className="mb-6">
          <h2 className="text-lg font-semibold text-slate-800">
            Popular Catalogue Categories
          </h2>

          <p className="mt-1 text-sm text-slate-500">
            Categories currently generating the most loan activity.
          </p>
        </div>

        {popularCategories.length === 0 ? (
          <div className="rounded-lg border border-dashed border-slate-200 bg-slate-50 p-8 text-center">

            <p className="text-sm font-medium text-slate-700">
              No category activity yet
            </p>

            <p className="mt-1 text-sm text-slate-500">
              Popular categories will appear here after books
              have been catalogued and loans have been recorded.
            </p>

          </div>
        ) : (
          <div className="space-y-5">

            {popularCategories.map((category, index) => (
              <CategoryRow
                key={`${category.category}-${index}`}
                category={category}
                index={index}
              />
            ))}

          </div>
        )}

      </div>

      {/* ===================================== */}
      {/* Overdue loans */}
      {/* ===================================== */}

      <div className="mt-8 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">

        <div className="border-b border-slate-100 px-6 py-5">

          <h2 className="text-lg font-semibold text-slate-800">
            Overdue Loans
          </h2>

          <p className="mt-1 text-sm text-slate-500">
            Loans that currently require attention.
          </p>

        </div>

        {overdueLoans.length === 0 ? (
          <div className="px-6 py-10 text-center">

            <div className="mx-auto flex h-10 w-10 items-center justify-center rounded-full bg-green-50 text-green-600">
              ✓
            </div>

            <p className="mt-3 text-sm font-medium text-slate-700">
              No overdue loans
            </p>

            <p className="mt-1 text-sm text-slate-400">
              There are currently no overdue loans requiring attention.
            </p>

          </div>
        ) : (
          <div className="overflow-x-auto">

            <table className="w-full text-left">

              <thead>
                <tr className="border-b border-slate-100 bg-slate-50 text-xs font-semibold uppercase tracking-wider text-slate-500">

                  <th className="px-6 py-3.5">
                    Borrower
                  </th>

                  <th className="px-6 py-3.5">
                    Book
                  </th>

                  <th className="px-6 py-3.5">
                    Due Date
                  </th>

                  <th className="px-6 py-3.5">
                    Days Overdue
                  </th>

                  <th className="px-6 py-3.5">
                    Fine
                  </th>

                </tr>
              </thead>

              <tbody className="divide-y divide-slate-100 text-sm text-slate-600">

                {overdueLoans.map((loan) => (
                  <tr
                    key={loan.loanId}
                    className="transition hover:bg-slate-50"
                  >

                    {/* Borrower */}

                    <td className="px-6 py-4">

                      <p className="font-medium text-slate-800">
                        {loan.borrowerName}
                      </p>

                      <p className="mt-0.5 font-mono text-xs text-slate-400">
                        {loan.borrowerId}
                      </p>

                    </td>

                    {/* Book */}

                    <td className="px-6 py-4">

                      <p className="font-medium text-slate-700">
                        {loan.title}
                      </p>

                      <p className="mt-0.5 font-mono text-xs text-slate-400">
                        {loan.isbn}
                      </p>

                    </td>

                    {/* Due date */}

                    <td className="px-6 py-4">

                      {formatDate(loan.dueDate)}

                    </td>

                    {/* Days overdue */}

                    <td className="px-6 py-4">

                      <span className="inline-flex rounded-full bg-red-50 px-2.5 py-1 text-xs font-medium text-red-600">

                        {loan.daysOverdue} day
                        {loan.daysOverdue === 1 ? '' : 's'}

                      </span>

                    </td>

                    {/* Fine */}

                    <td className="px-6 py-4">

                      <span className="font-semibold text-red-600">
                        KSh{' '}
                        {Number(
                          loan.fineAccrued || 0
                        ).toFixed(2)}
                      </span>

                    </td>

                  </tr>
                ))}

              </tbody>

            </table>

          </div>
        )}

      </div>

      {/* ===================================== */}
      {/* Export */}
      {/* ===================================== */}

      <div className="mt-8 flex justify-end">

        <button
          type="button"
          onClick={exportInventoryManifest}
          className="
            rounded-lg
            border border-slate-200
            bg-white
            px-4 py-2.5
            text-sm font-medium text-slate-700
            shadow-sm
            transition
            hover:border-blue-200
            hover:bg-blue-50
            hover:text-blue-600
            focus:outline-none
            focus:ring-4
            focus:ring-blue-100
          "
        >
          Export Inventory Audit
        </button>

      </div>

    </div>
  )
}


// ========================================
// Inventory row
// ========================================

function InventoryRow({
  label,
  value,
  color,
}) {
  return (
    <div>

      <div className="flex items-center justify-between">

        <span className="flex items-center gap-2 text-sm text-slate-600">

          <span
            className={`h-2.5 w-2.5 rounded-full ${color}`}
          />

          {label}

        </span>

        <span className="font-semibold text-slate-800">
          {Number(value || 0).toFixed(1)}%
        </span>

      </div>

      <div className="mt-2 h-2 w-full rounded-full bg-slate-100">

        <div
          className={`h-2 rounded-full ${color}`}
          style={{
            width: `${Math.min(
              Math.max(Number(value || 0), 0),
              100
            )}%`,
          }}
        />

      </div>

    </div>
  )
}


// ========================================
// Category row
// ========================================

function CategoryRow({
  category,
  index,
}) {
  const name =
    category.category ||
    category.name ||
    'Unknown category'

  const loanCount =
    Number(
      category.activeLoans ||
      category.loanCount ||
      category.count ||
      0
    )

  return (
    <div>

      <div className="flex items-center justify-between">

        <div className="flex items-center gap-3">

          <span className="
            flex h-7 w-7 items-center justify-center
            rounded-full bg-blue-50
            text-xs font-semibold text-blue-600
          ">
            {index + 1}
          </span>

          <span className="text-sm font-medium text-slate-700">
            {name}
          </span>

        </div>

        <span className="text-sm text-slate-500">
          {loanCount} active loan
          {loanCount === 1 ? '' : 's'}
        </span>

      </div>

      <div className="mt-2 h-2 w-full rounded-full bg-slate-100">

        <div
          className="h-2 rounded-full bg-blue-600"
          style={{
            width: `${Math.min(
              loanCount * 10,
              100
            )}%`,
          }}
        />

      </div>

    </div>
  )
}


// ========================================
// Date formatter
// ========================================

function formatDate(value) {
  if (!value) {
    return '—'
  }

  const date = new Date(value)

  if (Number.isNaN(date.getTime())) {
    return value
  }

  return date.toLocaleDateString()
}


export default ReportsPage
