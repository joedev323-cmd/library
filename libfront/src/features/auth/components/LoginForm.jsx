import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Button from '../../../components/ui/Button'
import Input from '../../../components/ui/Input'

function LoginForm() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [rememberMe, setRememberMe] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const navigate = useNavigate()
  const { login } = useAuth()

  function handleSubmit(event) {
    event.preventDefault()
    setErrorMessage('')

    if (username === 'admin' && password === 'admin') {
      login()
      navigate('/dashboard')
      return
    }

    setErrorMessage(
      'Invalid username or password. Please try again.'
    )
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-5">

      {errorMessage && (
        <div className="rounded-lg border border-red-100 bg-red-50 px-4 py-3 text-sm text-red-700">
          {errorMessage}
        </div>
      )}

      <Input
        id="username"
        label="Username or Staff ID"
        type="text"
        placeholder="Enter your username"
        value={username}
        onChange={(event) =>
          setUsername(event.target.value)
        }
        required
      />

      <Input
        id="password"
        label="Password"
        type="password"
        placeholder="Enter your password"
        value={password}
        onChange={(event) =>
          setPassword(event.target.value)
        }
        required
      />

      <div className="flex items-center justify-between">

        <label className="flex items-center gap-2 text-sm text-slate-500 cursor-pointer">
          <input
            type="checkbox"
            checked={rememberMe}
            onChange={(event) =>
              setRememberMe(event.target.checked)
            }
            className="h-4 w-4 rounded border-slate-300 text-blue-600 focus:ring-blue-500"
          />

          Remember me
        </label>

        <button
          type="button"
          className="text-sm font-medium text-blue-600 hover:text-blue-700"
        >
          Forgot password?
        </button>

      </div>

      <Button
        type="submit"
        size="lg"
        className="w-full"
      >
        Sign in
      </Button>

    </form>
  )
}

export default LoginForm