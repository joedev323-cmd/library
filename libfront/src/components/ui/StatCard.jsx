import Card from './Card'

function StatCard({
  label,
  value,
  description,
  icon,
  trend,
}) {
  return (
    <Card className="relative overflow-hidden">

      <div className="flex items-start justify-between gap-4">

        <div>

          <p className="text-sm font-medium text-slate-500">
            {label}
          </p>

          <p className="mt-2 text-3xl font-bold tracking-tight text-slate-900">
            {value}
          </p>

          {description && (
            <p className="mt-1 text-xs text-slate-500">
              {description}
            </p>
          )}

          {trend && (
            <p className="mt-3 text-xs font-medium text-green-600">
              {trend}
            </p>
          )}

        </div>

        {icon && (
          <div className="
            flex h-10 w-10
            shrink-0 items-center justify-center
            rounded-lg
            bg-blue-50
            text-blue-600
          ">
            {icon}
          </div>
        )}

      </div>

    </Card>
  )
}

export default StatCard