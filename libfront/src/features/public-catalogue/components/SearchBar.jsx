import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Button from '../../../components/ui/Button'
import Input from '../../../components/ui/Input'

function SearchBar({
  initialValue = '',
  placeholder = 'Search title, author, ISBN...',
}) {
  const [query, setQuery] = useState(initialValue)
  const navigate = useNavigate()

  function handleSubmit(event) {
    event.preventDefault()

    const value = query.trim()

    if (!value) return

    navigate(`/search?q=${encodeURIComponent(value)}`)
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="flex flex-col gap-3 sm:flex-row sm:items-end"
    >
      <div className="flex-1">
        <Input
          label="Search catalogue"
          type="search"
          value={query}
          placeholder={placeholder}
          onChange={(event) => setQuery(event.target.value)}
        />
      </div>

      <Button type="submit" size="md">
        Search
      </Button>
    </form>
  )
}

export default SearchBar
