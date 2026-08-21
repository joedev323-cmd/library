import { Navigate, Routes, Route } from 'react-router-dom'

import MainLayout from '../layouts/MainLayout'
import AuthLayout from '../layouts/AuthLayout'
import ProtectedRoute from './ProtectedRoute'

import LandingPage from '../pages/public/LandingPage'
import SearchPage from '../pages/public/SearchPage'
import LoginPage from '../pages/auth/LoginPage'

import DashboardPage from '../pages/dashboard/DashboardPage'
import CataloguePage from '../pages/catalogue/CataloguePage'
import CategoriesPage from '../pages/categories/CategoriesPage'
import MembersPage from '../pages/members/MembersPage'
import CirculationPage from '../pages/circulation/CirculationPage'
import ReportsPage from '../pages/reports/ReportsPage'

function AppRoutes() {
  return (
    <Routes>

      {/* Public */}
      <Route path="/" element={<LandingPage />} />
      <Route path="/search" element={<SearchPage />} />

      {/* Authentication */}
      <Route element={<AuthLayout />}>
        <Route path="/staff/login" element={<LoginPage />} />
      </Route>

      {/* Protected staff console */}
      <Route element={<ProtectedRoute />}>
        <Route element={<MainLayout />}>

          <Route
            path="/dashboard"
            element={<DashboardPage />}
          />

          <Route
            path="/catalogue"
            element={<CataloguePage />}
          />

          <Route
            path="/categories"
            element={<CategoriesPage />}
          />

          <Route
            path="/members"
            element={<MembersPage />}
          />

          <Route
            path="/circulation"
            element={<CirculationPage />}
          />

          <Route
            path="/reports"
            element={<ReportsPage />}
          />

        </Route>
      </Route>

      {/* Fallback */}
      <Route
        path="*"
        element={<Navigate to="/" replace />}
      />

    </Routes>
  )
}

export default AppRoutes
