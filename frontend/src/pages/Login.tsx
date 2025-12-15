import { useForm } from 'react-hook-form';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { apiPost } from '../lib/api';
import { useAuth } from '../lib/auth';

interface LoginForm {
  username: string;
  password: string;
}

export default function LoginPage() {
  const { register, handleSubmit, formState } = useForm<LoginForm>();
  const navigate = useNavigate();
  const location = useLocation();
  const { setAuth } = useAuth();

  const onSubmit = async (data: LoginForm) => {
    try {
      const res = await apiPost<{ token: string; role: string }>('/auth/login', data);
      setAuth({ token: res.token, role: res.role });
      const redirect = (location.state as any)?.from?.pathname || '/';
      navigate(redirect, { replace: true });
    } catch (e: any) {
      alert(e.message || '登录失败');
    }
  };

  return (
    <div className="max-w-md mx-auto bg-white border rounded-lg p-6 shadow-sm">
      <h1 className="text-xl font-semibold mb-4">登录</h1>
      <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label className="block text-sm mb-1">用户名</label>
          <input className="w-full border rounded px-3 py-2" {...register('username', { required: true })} />
        </div>
        <div>
          <label className="block text-sm mb-1">密码</label>
          <input type="password" className="w-full border rounded px-3 py-2" {...register('password', { required: true })} />
        </div>
        <button
          type="submit"
          disabled={formState.isSubmitting}
          className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
        >
          {formState.isSubmitting ? '登录中...' : '登录'}
        </button>
      </form>
      <div className="text-sm text-gray-600 mt-4 flex flex-col gap-1">
        <span>没有账号？</span>
        <div className="flex gap-3">
          <Link to="/register/student" className="text-blue-600">学生注册</Link>
          <Link to="/register/company" className="text-blue-600">企业注册</Link>
        </div>
      </div>
    </div>
  );
}
