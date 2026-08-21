import { useMemo, useState } from 'react'
import { getBooks } from '../../features/books/services/bookService'

function LandingPage() {
  const [search, setSearch] = useState('')

  const books = getBooks()

  const popularBooks = useMemo(() => {
    return books.slice(0, 3)
  }, [books])

  const newArrivals = useMemo(() => {
    return [...books].reverse().slice(0, 3)
  }, [books])

  const categories = useMemo(() => {
    return [...new Set(books.map((book) => book.category))]
  }, [books])

  const availableBooks = books.filter(
    (book) => book.status === 'Available'
  ).length

  function handleSearch(event) {
    event.preventDefault()

    const query = search.trim()

    if (!query) return

    window.location.href = `/search?q=${encodeURIComponent(query)}`
  }

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800">

      {/* =========================
          NAVBAR
      ========================== */}
      <header className="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-slate-200">

        <div className="max-w-7xl mx-auto px-6 h-18 flex items-center justify-between">

          {/* Logo */}
          <a
            href="/"
            className="flex items-center gap-3 group"
          >
            <div className="w-10 h-10 rounded-xl bg-blue-600 flex items-center justify-center shadow-sm group-hover:bg-blue-700 transition">
              <span className="text-white font-bold text-lg">
                B
              </span>
            </div>

            <div>
              <span className="text-xl font-light text-slate-800">
                Biblio
              </span>

              <span className="text-xl font-bold text-blue-600">
                Hub
              </span>

              <p className="text-[10px] uppercase tracking-widest text-slate-400">
                Library Catalogue
              </p>
            </div>
          </a>

          {/* Navigation */}
          <nav className="hidden md:flex items-center gap-8 text-sm font-medium">

            <a
              href="/"
              className="text-blue-600"
            >
              Home
            </a>

            <a
              href="/search"
              className="text-slate-500 hover:text-blue-600 transition"
            >
              Catalogue
            </a>

            <a
              href="#popular"
              className="text-slate-500 hover:text-blue-600 transition"
            >
              Popular
            </a>

            <a
              href="#categories"
              className="text-slate-500 hover:text-blue-600 transition"
            >
              Categories
            </a>

          </nav>

          <a
            href="/search"
            className="hidden sm:flex items-center gap-2 px-4 py-2.5 rounded-xl bg-slate-100 text-sm font-medium text-slate-600 hover:bg-blue-50 hover:text-blue-600 transition"
          >
            <SearchIcon />
            Browse books
          </a>

        </div>

      </header>


      <main>

        {/* =========================
            HERO
        ========================== */}
        <section className="relative overflow-hidden">

          {/* Background decoration */}
          <div className="absolute inset-0 bg-gradient-to-br from-blue-50 via-white to-indigo-50" />

          <div className="absolute -top-32 -right-32 w-96 h-96 rounded-full bg-blue-200/30 blur-3xl" />

          <div className="absolute -bottom-32 -left-32 w-96 h-96 rounded-full bg-indigo-200/30 blur-3xl" />


          <div className="relative max-w-7xl mx-auto px-6 pt-20 pb-20">

            <div className="grid lg:grid-cols-2 gap-16 items-center">

              {/* Hero text */}
              <div>

                <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-blue-100 text-blue-700 text-xs font-semibold uppercase tracking-wider mb-6">
                  <span className="w-1.5 h-1.5 rounded-full bg-blue-600" />
                  Library Catalogue
                </div>


                <h1 className="text-5xl md:text-6xl font-bold tracking-tight text-slate-900 leading-[1.05]">

                  Find your next
                  <span className="block text-blue-600">
                    great read.
                  </span>

                </h1>


                <p className="mt-6 text-lg text-slate-500 leading-relaxed max-w-xl">

                  Explore our library collection, discover popular titles,
                  check availability, and find exactly where your next book
                  is waiting for you.

                </p>


                {/* Search */}
                <form
                  onSubmit={handleSearch}
                  className="mt-8"
                >

                  <div className="bg-white border border-slate-200 rounded-2xl shadow-lg shadow-slate-200/50 p-2 flex gap-2">

                    <div className="flex-1 flex items-center">

                      <SearchIcon />

                      <input
                        type="search"
                        value={search}
                        onChange={(event) =>
                          setSearch(event.target.value)
                        }
                        placeholder="Search title, author, ISBN..."
                        className="w-full px-3 py-3 text-sm bg-transparent outline-none placeholder:text-slate-400"
                      />

                    </div>


                    <button
                      type="submit"
                      className="px-6 py-3 rounded-xl bg-blue-600 text-white font-semibold hover:bg-blue-700 transition shadow-sm"
                    >
                      Search
                    </button>

                  </div>

                </form>


                {/* Quick links */}
                <div className="flex flex-wrap gap-2 mt-5">

                  <span className="text-xs text-slate-400 py-1.5">
                    Popular:
                  </span>

                  {categories.slice(0, 4).map((category) => (
                    <a
                      key={category}
                      href={`/search?q=${encodeURIComponent(category)}`}
                      className="px-3 py-1.5 rounded-full bg-white border border-slate-200 text-xs text-slate-500 hover:border-blue-300 hover:text-blue-600 transition"
                    >
                      {category}
                    </a>
                  ))}

                </div>

              </div>


              {/* Hero visual */}
              <div className="hidden lg:block relative h-[420px]">

                <div className="absolute top-12 left-12 w-64 h-80 bg-blue-600 rounded-2xl rotate-[-8deg] shadow-2xl shadow-blue-300/40 p-8 text-white">

                  <div className="h-full border border-white/20 rounded-xl p-5 flex flex-col justify-between">

                    <div>
                      <p className="text-xs uppercase tracking-widest text-blue-200">
                        BiblioHub
                      </p>

                      <div className="w-12 h-1 bg-white/60 mt-4 rounded" />
                    </div>

                    <div>
                      <p className="text-2xl font-bold">
                        Discover.
                      </p>

                      <p className="text-2xl font-bold">
                        Read.
                      </p>

                      <p className="text-2xl font-bold">
                        Explore.
                      </p>
                    </div>

                  </div>

                </div>


                <div className="absolute top-0 right-10 w-64 h-80 bg-white rounded-2xl rotate-[8deg] shadow-2xl border border-slate-100 p-7">

                  <div className="h-full rounded-xl bg-gradient-to-br from-amber-50 to-orange-100 p-6 flex flex-col justify-between">

                    <div>
                      <div className="w-10 h-10 rounded-lg bg-orange-500 flex items-center justify-center text-white font-bold">
                        B
                      </div>

                      <p className="text-xs uppercase tracking-widest text-orange-600 mt-6">
                        Featured collection
                      </p>
                    </div>

                    <div>
                      <p className="text-xl font-bold text-slate-800">
                        Stories that
                      </p>

                      <p className="text-xl font-bold text-orange-600">
                        stay with you.
                      </p>
                    </div>

                  </div>

                </div>


                <div className="absolute bottom-2 right-24 bg-white rounded-2xl shadow-xl border border-slate-200 p-5 w-60">

                  <div className="flex items-center gap-4">

                    <div className="w-12 h-12 rounded-xl bg-green-100 flex items-center justify-center">
                      <span className="text-green-600 font-bold">
                        ✓
                      </span>
                    </div>

                    <div>
                      <p className="text-sm font-bold text-slate-800">
                        {availableBooks} books
                      </p>

                      <p className="text-xs text-slate-400">
                        available now
                      </p>
                    </div>

                  </div>

                </div>

              </div>

            </div>

          </div>

        </section>


        {/* =========================
            STATS
        ========================== */}
        <section className="max-w-7xl mx-auto px-6 -mt-8 relative z-10">

          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">

            <Stat
              value={books.length}
              label="Books in catalogue"
              icon="📚"
            />

            <Stat
              value={availableBooks}
              label="Available now"
              icon="✓"
            />

            <Stat
              value={categories.length}
              label="Categories"
              icon="◈"
            />

            <Stat
              value="1,240+"
              label="Searches this month"
              icon="⌕"
            />

          </div>

        </section>


        {/* =========================
            POPULAR BOOKS
        ========================== */}
        <section
          id="popular"
          className="max-w-7xl mx-auto px-6 pt-24 pb-12"
        >

          <SectionHeading
            eyebrow="DISCOVER"
            title="Popular right now"
            description="Books people are discovering and looking for."
            linkText="View catalogue"
            linkHref="/search"
          />


          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">

            {popularBooks.map((book, index) => (
              <PublicBookCard
                key={book.id}
                book={book}
                index={index}
              />
            ))}

          </div>

        </section>


        {/* =========================
            NEW ARRIVALS
        ========================== */}
        <section className="max-w-7xl mx-auto px-6 py-12">

          <SectionHeading
            eyebrow="JUST ADDED"
            title="New arrivals"
            description="Fresh additions to the BiblioHub collection."
          />


          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">

            {newArrivals.map((book, index) => (
              <PublicBookCard
                key={book.id}
                book={book}
                index={index + 3}
              />
            ))}

          </div>

        </section>


        {/* =========================
            CATEGORIES
        ========================== */}
        <section
          id="categories"
          className="max-w-7xl mx-auto px-6 py-16"
        >

          <div className="bg-slate-900 rounded-3xl p-8 md:p-12 overflow-hidden relative">

            <div className="absolute -right-20 -top-20 w-72 h-72 rounded-full bg-blue-600/20 blur-3xl" />

            <div className="relative">

              <p className="text-xs font-semibold tracking-widest text-blue-400 uppercase">
                Explore
              </p>

              <h2 className="text-3xl font-bold text-white mt-2">
                Browse by category
              </h2>

              <p className="text-slate-400 mt-2 max-w-xl">
                Find something that matches your interests.
              </p>


              <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 mt-8">

                {categories.map((category) => (
                  <a
                    key={category}
                    href={`/search?q=${encodeURIComponent(category)}`}
                    className="group bg-white/5 border border-white/10 rounded-xl p-4 hover:bg-blue-600 hover:border-blue-500 transition"
                  >

                    <div className="w-9 h-9 rounded-lg bg-white/10 flex items-center justify-center text-white mb-4 group-hover:bg-white/20">
                      ◈
                    </div>

                    <p className="text-sm font-semibold text-white">
                      {category}
                    </p>

                    <p className="text-xs text-slate-500 mt-1 group-hover:text-blue-100">
                      Explore →
                    </p>

                  </a>
                ))}

              </div>

            </div>

          </div>

        </section>


        {/* =========================
            CTA
        ========================== */}
        <section className="max-w-5xl mx-auto px-6 py-20 text-center">

          <div className="w-14 h-14 mx-auto rounded-2xl bg-blue-100 flex items-center justify-center text-blue-600 mb-6">
            <SearchIcon large />
          </div>

          <h2 className="text-3xl md:text-4xl font-bold text-slate-900">
            Looking for something specific?
          </h2>

          <p className="text-slate-500 mt-3 max-w-xl mx-auto">
            Search our complete catalogue by title, author, category,
            or ISBN.
          </p>

          <a
            href="/search"
            className="inline-flex items-center gap-2 mt-7 px-6 py-3 rounded-xl bg-blue-600 text-white font-semibold hover:bg-blue-700 transition"
          >
            Explore the catalogue
            <span>→</span>
          </a>

        </section>

      </main>


      {/* =========================
          FOOTER
      ========================== */}
      <footer className="bg-slate-900 text-white">

        <div className="max-w-7xl mx-auto px-6 py-12">

          <div className="grid md:grid-cols-3 gap-10">

            <div>

              <div className="flex items-center gap-3">

                <div className="w-9 h-9 rounded-lg bg-blue-600 flex items-center justify-center font-bold">
                  B
                </div>

                <div>
                  <span className="font-light">
                    Biblio
                  </span>

                  <span className="font-bold text-blue-400">
                    Hub
                  </span>
                </div>

              </div>

              <p className="text-sm text-slate-400 mt-4 max-w-sm">
                Your digital window into the library catalogue.
                Discover books before you make the trip.
              </p>

            </div>


            <div>
              <h3 className="font-semibold">
                Catalogue
              </h3>

              <div className="flex flex-col gap-2 mt-4 text-sm text-slate-400">

                <a
                  href="/search"
                  className="hover:text-white transition"
                >
                  Search books
                </a>

                <a
                  href="#popular"
                  className="hover:text-white transition"
                >
                  Popular books
                </a>

                <a
                  href="#categories"
                  className="hover:text-white transition"
                >
                  Categories
                </a>

              </div>
            </div>


            <div>
              <h3 className="font-semibold">
                Library
              </h3>

              <p className="text-sm text-slate-400 mt-4">
                Check book availability and discover where
                your next read is waiting.
              </p>
            </div>

          </div>


          <div className="border-t border-white/10 mt-10 pt-6 flex flex-col sm:flex-row justify-between gap-3 text-xs text-slate-500">

            <p>
              BiblioHub Library Catalogue © 2026
            </p>

            <p>
              Discover · Read · Explore
            </p>

          </div>

        </div>

      </footer>

    </div>
  )
}


/* =================================
   STAT
================================= */

function Stat({ value, label, icon }) {
  return (
    <div className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm hover:shadow-md transition">

      <div className="flex items-center gap-4">

        <div className="w-11 h-11 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center font-bold">
          {icon}
        </div>

        <div>

          <p className="text-2xl font-bold text-slate-900">
            {value}
          </p>

          <p className="text-xs text-slate-500 mt-0.5">
            {label}
          </p>

        </div>

      </div>

    </div>
  )
}


/* =================================
   SECTION HEADING
================================= */

function SectionHeading({
  eyebrow,
  title,
  description,
  linkText,
  linkHref,
}) {
  return (
    <div className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4 mb-7">

      <div>

        {eyebrow && (
          <p className="text-xs font-bold tracking-widest text-blue-600 uppercase">
            {eyebrow}
          </p>
        )}

        <h2 className="text-3xl font-bold text-slate-900 mt-1">
          {title}
        </h2>

        <p className="text-slate-500 mt-1">
          {description}
        </p>

      </div>

      {linkText && (
        <a
          href={linkHref}
          className="text-sm font-semibold text-blue-600 hover:text-blue-700"
        >
          {linkText} →
        </a>
      )}

    </div>
  )
}


/* =================================
   BOOK CARD
================================= */

function PublicBookCard({ book, index }) {
  const available = book.status === 'Available'

  const colors = [
    'from-blue-600 to-indigo-700',
    'from-emerald-500 to-teal-700',
    'from-orange-500 to-rose-600',
    'from-violet-600 to-purple-800',
    'from-cyan-500 to-blue-700',
    'from-pink-500 to-rose-700',
  ]

  const color = colors[index % colors.length]

  return (
    <a
      href={`/books/${book.id}`}
      className="group block bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
    >

      {/* Book cover */}
      <div
        className={`h-52 bg-gradient-to-br ${color} p-7 relative overflow-hidden`}
      >

        <div className="absolute -right-10 -bottom-10 w-40 h-40 rounded-full bg-white/10" />

        <div className="absolute top-5 right-5">

          <span
            className={`px-3 py-1.5 rounded-full text-xs font-semibold backdrop-blur ${
              available
                ? 'bg-white/20 text-white'
                : 'bg-orange-500/90 text-white'
            }`}
          >
            {available ? 'Available' : 'On Loan'}
          </span>

        </div>


        <div className="relative h-full flex flex-col justify-end">

          <p className="text-[10px] uppercase tracking-widest text-white/60">
            BiblioHub Collection
          </p>

          <h3 className="text-xl font-bold text-white mt-2 max-w-[80%] line-clamp-2">
            {book.title}
          </h3>

        </div>

      </div>


      {/* Details */}
      <div className="p-6">

        <p className="text-sm text-slate-500">
          by{' '}
          <span className="text-slate-700 font-medium">
            {book.author}
          </span>
        </p>


        <div className="flex items-center justify-between mt-5">

          <span className="px-3 py-1.5 rounded-full bg-slate-100 text-xs font-medium text-slate-600">
            {book.category}
          </span>

          <span className="text-sm font-semibold text-blue-600 group-hover:translate-x-1 transition-transform">
            View book →
          </span>

        </div>


        <div className="border-t border-slate-100 mt-5 pt-4">

          <p className="text-xs text-slate-400">
            Availability
          </p>

          <p
            className={`text-sm font-semibold mt-1 ${
              available
                ? 'text-green-600'
                : 'text-orange-600'
            }`}
          >
            {available
              ? 'Available in the library'
              : 'Currently checked out'}
          </p>

        </div>

      </div>

    </a>
  )
}


/* =================================
   SEARCH ICON
================================= */

function SearchIcon({ large = false }) {
  return (
    <svg
      width={large ? 24 : 18}
      height={large ? 24 : 18}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      className="text-slate-400 shrink-0"
    >
      <circle cx="11" cy="11" r="7" />
      <path d="m20 20-4-4" />
    </svg>
  )
}


export default LandingPage