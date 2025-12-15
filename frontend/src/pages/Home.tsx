import { Link } from 'react-router-dom';

const cards = [
  { title: '职位', desc: '浏览并搜索发布的职位', to: '/jobs' },
  { title: '企业', desc: '查看已审核的企业信息', to: '/companies' },
  { title: '招聘会', desc: '查看校园招聘会与日程', to: '/fairs' },
  { title: '公告', desc: '平台公告与通知', to: '/announcements' },
];

export default function HomePage() {
  return (
    <div className="space-y-6">
      <div className="bg-white border rounded-lg p-6 shadow-sm">
        <h1 className="text-2xl font-semibold mb-2">校园招聘会管理平台</h1>
        <p className="text-gray-600">支持学生投递、企业管理岗位、管理员审核与统计。</p>
      </div>
      <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-4">
        {cards.map((card) => (
          <Link key={card.to} to={card.to} className="block bg-white border rounded-lg p-4 shadow-sm hover:border-blue-300">
            <h3 className="font-semibold text-lg mb-2">{card.title}</h3>
            <p className="text-gray-600 text-sm">{card.desc}</p>
          </Link>
        ))}
      </div>
    </div>
  );
}
