const variants = {
  available: 'bg-green-50 text-green-700 ring-green-600/20',
  success: 'bg-green-50 text-green-700 ring-green-600/20',

  loan: 'bg-orange-50 text-orange-700 ring-orange-600/20',
  warning: 'bg-orange-50 text-orange-700 ring-orange-600/20',

  danger: 'bg-red-50 text-red-700 ring-red-600/20',

  neutral: 'bg-slate-100 text-slate-600 ring-slate-500/10',

  info: 'bg-blue-50 text-blue-700 ring-blue-600/20',
}

function Badge({
  children,
  variant = 'neutral',
}) {
  return (
    <span
      className={`
        inline-flex
        items-center
        rounded-full
        px-2.5
        py-1
        text-xs
        font-medium
        ring-1
        ring-inset
        ${variants[variant]}
      `}
    >
      {children}
    </span>
  )
}

export default Badge