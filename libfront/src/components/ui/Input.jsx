function Input({
  label,
  error,
  hint,
  id,
  className = '',
  ...props
}) {
  return (
    <div className="space-y-2">

      {label && (
        <label
          htmlFor={id}
          className="block text-sm font-medium text-slate-700"
        >
          {label}
        </label>
      )}

      <input
        id={id}
        className={`
          w-full
          rounded-lg
          border
          bg-white
          px-4
          py-2.5
          text-sm
          text-slate-900
          placeholder:text-slate-400

          transition

          focus:outline-none
          focus:ring-4

          ${
            error
              ? `
                border-red-300
                focus:border-red-500
                focus:ring-red-100
              `
              : `
                border-slate-200
                focus:border-blue-500
                focus:ring-blue-100
              `
          }

          disabled:bg-slate-50
          disabled:text-slate-500

          ${className}
        `}
        {...props}
      />

      {error && (
        <p className="text-xs font-medium text-red-600">
          {error}
        </p>
      )}

      {!error && hint && (
        <p className="text-xs text-slate-500">
          {hint}
        </p>
      )}

    </div>
  )
}

export default Input