function ErrorMessage({
  title = 'Something went wrong',
  message,
  action,
}) {
  return (
    <div
      role="alert"
      className="
        rounded-xl
        border border-red-200
        bg-red-50
        p-4
      "
    >
      <div className="flex gap-3">

        <div className="mt-0.5 shrink-0 text-red-600">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 20 20"
            fill="currentColor"
            className="h-5 w-5"
            aria-hidden="true"
          >
            <path
              fillRule="evenodd"
              d="M10 18a8 8 0 1 0 0-16 8 8 0 0 0 0 16Zm.75-11.25a.75.75 0 0 0-1.5 0v4.5a.75.75 0 0 0 1.5 0v-4.5ZM10 14.5a1 1 0 1 0 0-2 1 1 0 0 0 0 2Z"
              clipRule="evenodd"
            />
          </svg>
        </div>

        <div className="min-w-0">
          <h3 className="text-sm font-semibold text-red-800">
            {title}
          </h3>

          {message && (
            <p className="mt-1 text-sm text-red-700">
              {message}
            </p>
          )}

          {action && (
            <div className="mt-3">
              {action}
            </div>
          )}
        </div>

      </div>
    </div>
  )
}

export default ErrorMessage
