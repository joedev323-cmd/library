function LoadingState({
  message = 'Loading...',
}) {
  return (
    <div
      className="
        flex min-h-40
        flex-col items-center justify-center
        rounded-xl
        border border-slate-200
        bg-white
        p-6
      "
      role="status"
      aria-live="polite"
    >
      <div
        className="
          h-6 w-6
          animate-spin
          rounded-full
          border-2
          border-slate-200
          border-t-blue-600
        "
        aria-hidden="true"
      />

      <p className="mt-3 text-sm text-slate-500">
        {message}
      </p>
    </div>
  )
}

export default LoadingState
