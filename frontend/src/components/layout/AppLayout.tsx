import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../lib/auth';

const navItems = [
  { to: '/', label: '首页' },
  { to: '/jobs', label: '职位' },
  { to: '/companies', label: '企业' },
  { to: '/fairs', label: '招聘会' },
  { to: '/announcements', label: '公告' },
];

export const AppLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { auth, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen flex flex-col">
      <header className="bg-white shadow">
        <div className="container flex items-center justify-between h-14">
          <Link to="/" className="font-semibold text-lg">校园招聘会</Link>
          <nav className="flex items-center gap-4 text-sm">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `hover:text-blue-600 ${isActive ? 'text-blue-600 font-medium' : 'text-gray-700'}`
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
          <div className="flex items-center gap-3 text-sm">
            {auth.token ? (
              <>
                <span className="text-gray-600">角色：{auth.role}</span>
                <NavLink to="/dashboard" className="text-blue-600">控制台</NavLink>
                <button onClick={handleLogout} className="text-gray-600 hover:text-gray-800">退出</button>
              </>
            ) : (
              <>
                <Link to="/login" className="text-blue-600">登录</Link>
                <Link to="/register/student" className="text-gray-700 hover:text-gray-900">学生注册</Link>
                <Link to="/register/company" className="text-gray-700 hover:text-gray-900">企业注册</Link>
              </>
            )}
          </div>
        </div>
      </header>
      <main className="flex-1 container py-6">{children}</main>
    </div>
  );
};
