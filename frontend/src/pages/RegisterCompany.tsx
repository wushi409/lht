import { useForm } from 'react-hook-form';
import { apiPost } from '../lib/api';
import { useNavigate } from 'react-router-dom';

interface FormValues {
  name: string;
  creditCode: string;
  scale?: string;
  industry?: string;
  description?: string;
  contactName?: string;
  contactPhone?: string;
  contactEmail?: string;
  password: string;
}

export default function RegisterCompanyPage() {
  const { register, handleSubmit, formState, reset } = useForm<FormValues>();
  const navigate = useNavigate();

  const onSubmit = async (data: FormValues) => {
    try {
      await apiPost('/auth/register/company', data);
      alert('企业申请已提交，待管理员审核');
      reset();
      navigate('/login');
    } catch (e: any) {
      alert(e.message || '提交失败');
    }
  };

  return (
    <div className="max-w-2xl mx-auto bg-white border rounded-lg p-6 shadow-sm">
      <h1 className="text-xl font-semibold mb-4">企业注册</h1>
      <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm mb-1">企业名称</label>
            <input className="w-full border rounded px-3 py-2" {...register('name', { required: true })} />
          </div>
          <div>
            <label className="block text-sm mb-1">统一社会信用代码</label>
            <input className="w-full border rounded px-3 py-2" {...register('creditCode', { required: true })} />
          </div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm mb-1">规模</label>
            <input className="w-full border rounded px-3 py-2" {...register('scale')} />
          </div>
          <div>
            <label className="block text-sm mb-1">行业</label>
            <input className="w-full border rounded px-3 py-2" {...register('industry')} />
          </div>
          <div>
            <label className="block text-sm mb-1">联系人</label>
            <input className="w-full border rounded px-3 py-2" {...register('contactName')} />
          </div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm mb-1">联系电话</label>
            <input className="w-full border rounded px-3 py-2" {...register('contactPhone')} />
          </div>
          <div>
            <label className="block text-sm mb-1">联系邮箱</label>
            <input type="email" className="w-full border rounded px-3 py-2" {...register('contactEmail')} />
          </div>
        </div>
        <div>
          <label className="block text-sm mb-1">企业简介</label>
          <textarea className="w-full border rounded px-3 py-2" rows={3} {...register('description')} />
        </div>
        <div>
          <label className="block text-sm mb-1">密码</label>
          <input type="password" className="w-full border rounded px-3 py-2" {...register('password', { required: true, minLength: 6 })} />
        </div>
        <button type="submit" disabled={formState.isSubmitting} className="w-full bg-blue-600 text-white py-2 rounded">
          {formState.isSubmitting ? '提交中...' : '提交审核'}
        </button>
      </form>
    </div>
  );
}
