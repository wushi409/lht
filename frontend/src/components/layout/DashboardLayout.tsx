import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../../lib/auth';

const studentNav = [
  { to: '/dashboard/student', label: '个人资料' },
  { to: '/dashboard/student/resumes', label: '简历' },
  { to: '/dashboard/student/applications', label: '投递记录' },
  { to: '/dashboard/student/interviews', label: '面试' },
  { to: '/dashboard/student/favorites', label: '收藏' },
  { to: '/dashboard/student/events', label: '活动报名' },
  { to: '/dashboard/student/notifications', label: '通知' },
];

const companyNav = [
  { to: '/dashboard/company', label: '企业资料' },
  { to: '/dashboard/company/jobs', label: '岗位发布' },
  { to: '/dashboard/company/interviews', label: '面试安排' },
];

const adminNav = [
  { to: '/dashboard/admin', label: '待审核企业' },
  { to: '/dashboard/admin/announcements', label: '公告发布' },
  { to: '/dashboard/admin/fairs', label: '招聘会/展位' },
  { to: '/dashboard/admin/exports', label: '导出任务' },
  { to: '/dashboard/admin/audit', label: '审计日志' },
  { to: '/dashboard/admin/stats', label: '统计' },
];

const Sidebar: React.FC<{ items: { to: string; label: string }[] }> = ({ items }) => (
  <aside className="w-52 shrink-0 bg-white border rounded-lg p-3 h-fit">
    <nav className="flex flex-col gap-2 text-sm">
      {items.map((item) => (
        <NavLink
          key={item.to}
          to={item.to}
          className={({ isActive }) =>
            `rounded px-3 py-2 hover:bg-blue-50 ${isActive ? 'bg-blue-100 text-blue-700 font-medium' : 'text-gray-700'}`
          }
          end
        >
          {item.label}
        </NavLink>
      ))}
    </nav>
  </aside>
);

export const DashboardLayout: React.FC = () => {
  const { auth } = useAuth();

  const nav = auth.role === 'STUDENT' ? studentNav : auth.role === 'COMPANY' ? companyNav : adminNav;

  return (
    <div className="flex gap-6">
      <Sidebar items={nav} />
      <section className="flex-1 min-w-0">
        <Outlet />
      </section>
    </div>
  );
};
