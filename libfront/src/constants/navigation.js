import {
  LayoutDashboard,
  BookOpen,
  Tags,
  Users,
  ArrowLeftRight,
  BarChart3,
} from 'lucide-react'

export const navigation = [
  {
    label: 'Dashboard',
    path: '/dashboard',
    icon: LayoutDashboard,
  },
  {
    label: 'Catalogue',
    path: '/catalogue',
    icon: BookOpen,
  },
  {
    label: 'Categories',
    path: '/categories',
    icon: Tags,
  },
  {
    label: 'Members',
    path: '/members',
    icon: Users,
  },
  {
    label: 'Circulation',
    path: '/circulation',
    icon: ArrowLeftRight,
  },
  {
    label: 'Reports',
    path: '/reports',
    icon: BarChart3,
  },
]
