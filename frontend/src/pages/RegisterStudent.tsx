import { useForm } from 'react-hook-form';
import { apiPost } from '../lib/api';
import { useNavigate } from 'react-router-dom';

interface FormValues {
  studentNo: string;
  name: string;
  college?: string;
  phone?: string;
  email?: string;
  password: string;
}

export default function RegisterStudentPage() {
  const { register, handleSubmit, formState, reset } = useForm<FormValues>();
  const navigate = useNavigate();

  const onSubmit = async (data: FormValues) => {
    try {
      await apiPost('/auth/register/student', data);
      alert('注册成功，请登录');
      reset();
      navigate('/login');
    } catch (e: any) {
      alert(e.message || '注册失败');
    }
  };

  return (
    <div className="max-w-lg mx-auto bg-white border rounded-lg p-6 shadow-sm">
      <h1 className="text-xl font-semibold mb-4">学生注册</h1>
      <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label className="block text-sm mb-1">学号</label>
          <input className="w-full border rounded px-3 py-2" {...register('studentNo', { required: true })} />
        </div>
        <div>
          <label className="block text-sm mb-1">姓名</label>
          <input className="w-full border rounded px-3 py-2" {...register('name', { required: true })} />
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm mb-1">学院</label>
            <input className="w-full border rounded px-3 py-2" {...register('college')} />
          </div>
          <div>
            <label className="block text-sm mb-1">联系电话</label>
            <input className="w-full border rounded px-3 py-2" {...register('phone')} />
          </div>
        </div>
        <div>
          <label className="block text-sm mb-1">邮箱</label>
          <input type="email" className="w-full border rounded px-3 py-2" {...register('email')} />
        </div>
        <div>
          <label className="block text-sm mb-1">密码</label>
          <input type="password" className="w-full border rounded px-3 py-2" {...register('password', { required: true, minLength: 6 })} />
        </div>
        <button type="submit" disabled={formState.isSubmitting} className="w-full bg-blue-600 text-white py-2 rounded">
          {formState.isSubmitting ? '提交中...' : '注册'}
        </button>
      </form>
    </div>
  );
}
