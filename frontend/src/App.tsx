import { Navigate, Route, Routes } from 'react-router-dom';
import { AppLayout } from './components/layout/AppLayout';
import { ProtectedRoute } from './components/ProtectedRoute';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import RegisterStudentPage from './pages/RegisterStudent';
import RegisterCompanyPage from './pages/RegisterCompany';
import JobsPage from './pages/Jobs';
import JobDetailPage from './pages/JobDetail';
import CompaniesPage from './pages/Companies';
import CompanyDetailPage from './pages/CompanyDetail';
import FairsPage from './pages/Fairs';
import FairDetailPage from './pages/FairDetail';
import AnnouncementsPage from './pages/Announcements';
import AnnouncementDetailPage from './pages/AnnouncementDetail';
import { DashboardLayout } from './components/layout/DashboardLayout';
import StudentProfilePage from './pages/dashboard/student/Profile';
import StudentResumesPage from './pages/dashboard/student/Resumes';
import StudentApplicationsPage from './pages/dashboard/student/Applications';
import StudentInterviewsPage from './pages/dashboard/student/Interviews';
import StudentFavoritesPage from './pages/dashboard/student/Favorites';
import StudentEventsPage from './pages/dashboard/student/Events';
import StudentNotificationsPage from './pages/dashboard/student/Notifications';
import CompanyProfilePage from './pages/dashboard/company/Profile';
import CompanyJobsPage from './pages/dashboard/company/Jobs';
import CompanyInterviewsPage from './pages/dashboard/company/Interviews';
import AdminCompaniesPage from './pages/dashboard/admin/Companies';
import AdminAnnouncementsPage from './pages/dashboard/admin/Announcements';
import AdminFairsPage from './pages/dashboard/admin/Fairs';
import AdminExportsPage from './pages/dashboard/admin/Exports';
import AdminAuditPage from './pages/dashboard/admin/AuditLogs';
import AdminStatsPage from './pages/dashboard/admin/Stats';

function App() {
  return (
    <AppLayout>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register/student" element={<RegisterStudentPage />} />
        <Route path="/register/company" element={<RegisterCompanyPage />} />

        <Route path="/jobs" element={<JobsPage />} />
        <Route path="/jobs/:id" element={<JobDetailPage />} />
        <Route path="/companies" element={<CompaniesPage />} />
        <Route path="/companies/:id" element={<CompanyDetailPage />} />
        <Route path="/fairs" element={<FairsPage />} />
        <Route path="/fairs/:id" element={<FairDetailPage />} />
        <Route path="/announcements" element={<AnnouncementsPage />} />
        <Route path="/announcements/:id" element={<AnnouncementDetailPage />} />

        <Route
          path="/dashboard/*"
          element={
            <ProtectedRoute>
              <DashboardLayout />
            </ProtectedRoute>
          }
        >
          <Route path="" element={<Navigate to="/dashboard/student" replace />} />
          <Route
            path="student"
            element={
              <ProtectedRoute roles={['STUDENT']}>
                <StudentProfilePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="student/resumes"
            element={
              <ProtectedRoute roles={['STUDENT']}>
                <StudentResumesPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="student/applications"
            element={
              <ProtectedRoute roles={['STUDENT']}>
                <StudentApplicationsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="student/interviews"
            element={
              <ProtectedRoute roles={['STUDENT']}>
                <StudentInterviewsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="student/favorites"
            element={
              <ProtectedRoute roles={['STUDENT']}>
                <StudentFavoritesPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="student/events"
            element={
              <ProtectedRoute roles={['STUDENT']}>
                <StudentEventsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="student/notifications"
            element={
              <ProtectedRoute roles={['STUDENT']}>
                <StudentNotificationsPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="company"
            element={
              <ProtectedRoute roles={['COMPANY']}>
                <CompanyProfilePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="company/jobs"
            element={
              <ProtectedRoute roles={['COMPANY']}>
                <CompanyJobsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="company/interviews"
            element={
              <ProtectedRoute roles={['COMPANY']}>
                <CompanyInterviewsPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="admin"
            element={
              <ProtectedRoute roles={['ADMIN']}>
                <AdminCompaniesPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="admin/announcements"
            element={
              <ProtectedRoute roles={['ADMIN']}>
                <AdminAnnouncementsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="admin/fairs"
            element={
              <ProtectedRoute roles={['ADMIN']}>
                <AdminFairsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="admin/exports"
            element={
              <ProtectedRoute roles={['ADMIN']}>
                <AdminExportsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="admin/audit"
            element={
              <ProtectedRoute roles={['ADMIN']}>
                <AdminAuditPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="admin/stats"
            element={
              <ProtectedRoute roles={['ADMIN']}>
                <AdminStatsPage />
              </ProtectedRoute>
            }
          />
        </Route>
      </Routes>
    </AppLayout>
  );
}

export default App;
