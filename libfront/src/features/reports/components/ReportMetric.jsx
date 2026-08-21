function ReportMetric({ label, value }) {
  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
      <p className="text-sm font-medium text-slate-500">
        {label}
      </p>

      <p className="text-3xl font-bold text-slate-800 mt-2">
        {value}
      </p>
    </div>
  )
}

export default ReportMetric