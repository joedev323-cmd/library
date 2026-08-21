const variants = {
  primary:
    'bg-blue-600 text-white hover:bg-blue-700 active:bg-blue-800 focus:ring-blue-200',

  secondary:
    'bg-white text-slate-700 border border-slate-200 hover:bg-slate-50 active:bg-slate-100 focus:ring-slate-200',

  ghost:
    'bg-transparent text-slate-600 hover:bg-slate-100 hover:text-slate-900 focus:ring-slate-200',

  danger:
    'bg-red-600 text-white hover:bg-red-700 active:bg-red-800 focus:ring-red-200',

  success:
    'bg-green-600 text-white hover:bg-green-700 active:bg-green-800 focus:ring-green-200',
}

const sizes = {
  sm: 'px-3 py-2 text-xs',
  md: 'px-4 py-2.5 text-sm',
  lg: 'px-5 py-3 text-sm',
}

function Button({
  children,
  variant = 'primary',
  size = 'md',
  fullWidth = false,
  className = '',
  type = 'button',
  ...props
}) {
  return (
    <button
      type={type}
      className={`
        inline-flex items-center justify-center
        gap-2
        rounded-lg
        font-medium
        transition-all
        duration-150

        focus:outline-none
        focus:ring-4

        disabled:opacity-50
        disabled:pointer-events-none

        ${variants[variant]}
        ${sizes[size]}

        ${fullWidth ? 'w-full' : ''}

        ${className}
      `}
      {...props}
    >
      {children}
    </button>
  )
}

export default Button