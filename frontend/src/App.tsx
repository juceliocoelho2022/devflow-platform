import {useEffect,useState} from 'react';
import {LayoutDashboard,FolderKanban,CheckSquare,Columns3,Users,UserRound,BarChart3,Settings,Search,Bell,Plus,ArrowRight,LogOut,Menu,X,Clock3,ChevronRight} from 'lucide-react';
import {api,ApiError} from './api';
import {statusGradient,projectProgress} from './metrics';
import type {DashboardData,Project,Task,TaskStatus,User} from './types';

const menu=[
  ['Dashboard',LayoutDashboard],['Projetos',FolderKanban],['Tarefas',CheckSquare],['Kanban',Columns3],
  ['Equipes',Users],['Usuários',UserRound],['Relatórios',BarChart3],['Configurações',Settings]
] as const;
const statuses:TaskStatus[]=['BACKLOG','TODO','DOING','REVIEW','DONE'];
const labels:Record<TaskStatus,string>={BACKLOG:'Backlog',TODO:'A fazer',DOING:'Em andamento',REVIEW:'Revisão',DONE:'Concluído'};
const colors:Record<TaskStatus,string>={BACKLOG:'#3b82f6',TODO:'#8b5cf6',DOING:'#f59e0b',REVIEW:'#f43f5e',DONE:'#22c55e'};

function Login({onLogin}:{onLogin:(u:User)=>void}){
  const [email,setEmail]=useState(''),[password,setPassword]=useState(''),[error,setError]=useState(''),[loading,setLoading]=useState(false);
  async function submit(e:React.FormEvent){e.preventDefault();setLoading(true);setError('');try{onLogin((await api.login(email,password)).user)}catch(e){setError(e instanceof Error?e.message:'Erro ao entrar')}finally{setLoading(false)}}
  return <div className="login"><section className="login-brand"><Logo large/><div><span className="eyebrow">GESTÃO INTELIGENTE</span><h1>Projetos melhores.<br/><em>Equipes mais fortes.</em></h1><p>Planeje, organize e entregue resultados em uma plataforma criada para equipes de tecnologia.</p></div><div className="login-stats"><b>12 Projetos</b><b>86 Tarefas</b><b>24 Concluídas</b></div></section><form className="login-card" onSubmit={submit}><div className="mobile-logo"><Logo/></div><span className="eyebrow">BEM-VINDO DE VOLTA</span><h2>Acesse sua conta</h2><p>Entre com suas credenciais para continuar.</p><label>E-mail<input value={email} onChange={e=>setEmail(e.target.value)} type="email" required/></label><label>Senha<input value={password} onChange={e=>setPassword(e.target.value)} type="password" required/></label>{error&&<div className="error">{error}</div>}<button className="primary wide" disabled={loading}>{loading?'Entrando...':'Entrar'}<ArrowRight size={18}/></button><small>Use as credenciais fornecidas pelo administrador</small></form></div>
}

function Logo({large=false}:{large?:boolean}){return <div className={'logo '+(large?'large':'')}><span className="logo-mark">D</span><span>DevFlow<small>Enterprise</small></span></div>}

function Sidebar({page,setPage,open,setOpen,user,onLogout}:{page:string;setPage:(p:string)=>void;open:boolean;setOpen:(v:boolean)=>void;user:User;onLogout:()=>void}){
 return <aside className={open?'sidebar open':'sidebar'}><button className="close-menu" onClick={()=>setOpen(false)}><X/></button><Logo/><nav>{menu.map(([name,Icon])=><button key={name} className={page===name?'active':''} onClick={()=>{setPage(name);setOpen(false)}}><Icon size={19}/><span>{name}</span></button>)}</nav><div className="profile"><div className="avatar">{user.name[0]}</div><div><b>{user.name}</b><small>{user.role==='ADMIN'?'Administrador':user.role}</small></div><button title="Sair" onClick={onLogout}><LogOut size={17}/></button></div></aside>
}

function Header({page,onMenu}:{page:string;onMenu:()=>void}){return <header><button className="menu-button" onClick={onMenu}><Menu/></button><div><small>Workspace / DevFlow</small><h2>{page}</h2></div><div className="header-actions"><button><Search/></button><button><Bell/></button><div className="avatar">J</div></div></header>}

function Dashboard({data,projects,user,onNew,canManage}:{data:DashboardData|null;projects:Project[];user:User;onNew:()=>void;canManage:boolean}){
 if(!data)return <Loading/>;
 const cards=[['Projetos Ativos',data.activeProjects,FolderKanban,'blue'],['Tarefas em andamento',data.inProgress,Clock3,'purple'],['Tarefas concluídas',data.completed,CheckSquare,'green'],['Membros da equipe',data.members,Users,'orange']] as const;
 const points=data.productivity.map((v,i)=>`${i*(100/(data.productivity.length-1))},${100-v}`).join(' ');
 return <div className="page"><div className="welcome"><div><span className="eyebrow">VISÃO GERAL</span><h1>Olá, {user.name.split(' ')[0]}! <span>👋</span></h1><p>Acompanhe o desempenho dos seus projetos e da sua equipe.</p></div>{canManage&&<button className="primary" onClick={onNew}><Plus size={18}/> Novo projeto</button>}</div><div className="stat-grid">{cards.map(([label,value,Icon,color])=><div className="stat-card" key={label}><div className={'icon '+color}><Icon/></div><div><strong>{value}</strong><span>{label}</span></div></div>)}</div><div className="charts"><section className="panel"><PanelTitle title="Tarefas por status" subtitle="Distribuição atual"/><div className="status-chart"><div className="donut" style={{background:statusGradient(statuses.map(s=>({count:data.byStatus[s]??0,color:colors[s]})))}}><span>{Object.values(data.byStatus).reduce((a,b)=>a+b,0)}<small>Tarefas</small></span></div><div className="legend">{statuses.map(s=><div key={s}><i style={{background:colors[s]}}/><span>{labels[s]}</span><b>{data.byStatus[s]}</b></div>)}</div></div></section><section className="panel"><PanelTitle title="Produtividade" subtitle="Últimas 8 semanas"/><div className="line-chart"><svg viewBox="0 0 100 100" preserveAspectRatio="none"><defs><linearGradient id="fill" x1="0" x2="0" y1="0" y2="1"><stop offset="0" stopColor="#1677ff" stopOpacity=".28"/><stop offset="1" stopColor="#1677ff" stopOpacity="0"/></linearGradient></defs><polygon points={`0,100 ${points} 100,100`} fill="url(#fill)"/><polyline points={points} fill="none" stroke="#1677ff" strokeWidth="2"/>{data.productivity.map((v,i)=><circle key={i} cx={i*(100/(data.productivity.length-1))} cy={100-v} r="2" fill="#1677ff"/>)}</svg><div><span>Sem 1</span><span>Sem 4</span><span>Sem 8</span></div></div></section></div><div className="lower-grid"><section className="panel"><PanelTitle title="Projetos recentes" subtitle="Em destaque"/><div className="project-list">{projects.slice(0,4).map((p,i)=><div key={p.id}><span className="project-dot" style={{background:['#1677ff','#8b5cf6','#f59e0b','#22c55e'][i]}}>{p.name[0]}</span><div><b>{p.name}</b><small>{p.description}</small></div><span className={'badge '+p.status.toLowerCase()}>{p.status==='IN_PROGRESS'?'Em andamento':p.status==='COMPLETED'?'Concluído':'Planejamento'}</span></div>)}</div></section><section className="panel"><PanelTitle title="Atividades recentes" subtitle="Hoje"/><div className="activity-list">{data.activities.map((a,i)=><div key={a}><span className="activity-icon"><CheckSquare size={15}/></span><div><b>{a}</b><small>Há {i===0?'10 min':i+' h'}</small></div><ChevronRight size={16}/></div>)}</div></section></div></div>
}

function PanelTitle({title,subtitle}:{title:string;subtitle:string}){return <div className="panel-title"><div><h3>{title}</h3><small>{subtitle}</small></div><button>•••</button></div>}
function Loading(){return <div className="loading"><i/><span>Carregando DevFlow...</span></div>}

function Kanban({tasks,projects,onMove,onNew,canManage}:{tasks:Task[];projects:Project[];onMove:(id:number,s:TaskStatus)=>void;onNew:()=>void;canManage:boolean}){
 const [search,setSearch]=useState(''),[project,setProject]=useState(0),[priority,setPriority]=useState('');
 const [dragged,setDragged]=useState<number|null>(null);
 const filtered=tasks.filter(t=>(!search||`${t.title} ${t.description}`.toLowerCase().includes(search.toLowerCase()))&&(!project||t.project.id===project)&&(!priority||t.priority===priority));
 function drop(status:TaskStatus){if(dragged!==null){const task=tasks.find(t=>t.id===dragged);if(task&&task.status!==status)onMove(dragged,status)}setDragged(null)}
 return <div className="page kanban-page"><div className="title-row"><div><span className="eyebrow">DEVFLOW WEB APP</span><h1>Quadro Kanban</h1><p>Arraste o trabalho pelo fluxo ou use as setas.</p></div>{canManage&&<button className="primary" onClick={onNew}><Plus size={18}/> Nova tarefa</button>}</div><div className="kanban-toolbar"><label className="search-field"><Search size={16}/><input value={search} onChange={e=>setSearch(e.target.value)} placeholder="Buscar tarefas..."/></label><select value={project} onChange={e=>setProject(Number(e.target.value))}><option value={0}>Todos os projetos</option>{projects.map(p=><option key={p.id} value={p.id}>{p.name}</option>)}</select><select value={priority} onChange={e=>setPriority(e.target.value)}><option value="">Todas as prioridades</option><option value="HIGH">Alta</option><option value="MEDIUM">Média</option><option value="LOW">Baixa</option></select><span>{filtered.length} tarefa{filtered.length===1?'':'s'}</span></div><div className="kanban">{statuses.map((status,si)=><section className={'column '+(dragged!==null?'drop-ready':'')} key={status} onDragOver={e=>e.preventDefault()} onDrop={()=>drop(status)}><div className="column-title"><span><i style={{background:colors[status]}}/>{labels[status]}</span><b>{filtered.filter(t=>t.status===status).length}</b></div>{filtered.filter(t=>t.status===status).map(t=><article className={'task '+(dragged===t.id?'dragging':'')} draggable onDragStart={()=>setDragged(t.id)} onDragEnd={()=>setDragged(null)} key={t.id}><div className="task-top"><span className={'priority '+t.priority.toLowerCase()}>{t.priority==='HIGH'?'Alta':t.priority==='MEDIUM'?'Média':'Baixa'}</span><small>DF-{String(t.id).padStart(2,'0')}</small></div><h3>{t.title}</h3><p>{t.description}</p><div className="task-footer"><span>{t.project.name}</span><div><button disabled={si===0} onClick={()=>onMove(t.id,statuses[si-1])}>‹</button><div className="mini-avatar">{t.assignee?.name[0]||'?'}</div><button disabled={si===statuses.length-1} onClick={()=>onMove(t.id,statuses[si+1])}>›</button></div></div></article>)}{canManage&&<button className="add-task" onClick={onNew}><Plus size={15}/> Adicionar tarefa</button>}</section>)}</div></div>
}

function Projects({projects,tasks,onNew,canManage}:{projects:Project[];tasks:Task[];onNew:()=>void;canManage:boolean}){return <div className="page"><div className="title-row"><div><span className="eyebrow">PORTFÓLIO</span><h1>Projetos</h1><p>Organize todas as iniciativas da sua equipe.</p></div>{canManage&&<button className="primary" onClick={onNew}><Plus size={18}/> Novo projeto</button>}</div><div className="project-grid">{projects.map((p,i)=>{const progress=projectProgress(tasks,p.id);return <article className="project-card" key={p.id}><div className="project-cover" style={{background:`linear-gradient(135deg,${['#0f58d6','#7046d8','#e18b16','#12a66b'][i%4]},#071629)`}}><FolderKanban/><span className={'badge '+p.status.toLowerCase()}>{p.status==='IN_PROGRESS'?'Em andamento':p.status==='COMPLETED'?'Concluído':'Planejamento'}</span></div><div><h3>{p.name}</h3><p>{p.description}</p><div className="progress"><span><i style={{width:`${progress.percent}%`}}/></span><small>{progress.percent}%</small></div><div className="card-foot"><span>{progress.completed} concluída{progress.completed===1?'':'s'}</span><b>{progress.total} tarefa{progress.total===1?'':'s'}</b></div></div></article>})}</div></div>}

function Placeholder({page}:{page:string}){return <div className="page"><div className="title-row"><div><span className="eyebrow">DEVFLOW ENTERPRISE</span><h1>{page}</h1><p>Este módulo está preparado para a próxima incrementação.</p></div></div><div className="placeholder panel"><div className="icon blue">{page==='Relatórios'?<BarChart3/>:<Settings/>}</div><h2>{page} em evolução</h2><p>A fundação visual e a navegação já estão prontas. Na próxima etapa podemos definir e implementar os fluxos completos deste módulo.</p><button className="secondary">Ver planejamento <ArrowRight size={16}/></button></div></div>}

function Modal({kind,projects,onClose,onDone,onSessionExpired}:{kind:'task'|'project';projects:Project[];onClose:()=>void;onDone:()=>void;onSessionExpired:()=>void}){
 const [title,setTitle]=useState(''),[desc,setDesc]=useState(''),[projectId,setProjectId]=useState(projects[0]?.id||0),[busy,setBusy]=useState(false),[error,setError]=useState('');
 async function submit(e:React.FormEvent){e.preventDefault();if(busy)return;setError('');if(kind==='task'&&!projectId){setError('Crie um projeto antes de adicionar tarefas.');return;}setBusy(true);try{kind==='task'?await api.createTask({title,description:desc,projectId,status:'BACKLOG',priority:'MEDIUM'}):await api.createProject({name:title,description:desc,status:'PLANNING'});onDone()}catch(error){if(error instanceof ApiError&&error.status===401){onSessionExpired();return;}setError(error instanceof Error?error.message:'Não foi possível salvar. Tente novamente.')}finally{setBusy(false)}}
 return <div className="modal-backdrop" onMouseDown={()=>{if(!busy)onClose()}}><form className="modal" role="dialog" aria-modal="true" aria-label={kind==='task'?'Criar tarefa':'Criar projeto'} onSubmit={submit} onMouseDown={e=>e.stopPropagation()}><div className="modal-head"><div><span className="eyebrow">NOVO REGISTRO</span><h2>{kind==='task'?'Criar tarefa':'Criar projeto'}</h2></div><button type="button" disabled={busy} aria-label="Fechar" onClick={onClose}><X/></button></div><label>{kind==='task'?'Título da tarefa':'Nome do projeto'}<input autoFocus value={title} onChange={e=>setTitle(e.target.value)} required placeholder="Digite um título"/></label><label>Descrição<textarea value={desc} onChange={e=>setDesc(e.target.value)} placeholder="Adicione contexto..."/></label>{kind==='task'&&<label>Projeto<select value={projectId} onChange={e=>setProjectId(Number(e.target.value))}>{projects.map(p=><option key={p.id} value={p.id}>{p.name}</option>)}</select></label>}{error&&<div className="error" role="alert">{error}</div>}{kind==='task'&&!projects.length&&<div className="error" role="alert">Crie um projeto antes de adicionar tarefas.</div>}<div className="modal-actions"><button type="button" disabled={busy} className="secondary" onClick={onClose}>Cancelar</button><button className="primary" disabled={busy||(kind==='task'&&!projectId)}>{busy?'Salvando...':'Criar agora'}</button></div></form></div>
}

function storedUser():User|null {
 try {const stored=localStorage.getItem('devflow_user');return stored?JSON.parse(stored):null}
 catch {api.logout();return null}
}

export default function App(){
 const [user,setUser]=useState<User|null>(storedUser);
 const [page,setPage]=useState('Dashboard'),[open,setOpen]=useState(false),[dashboard,setDashboard]=useState<DashboardData|null>(null),[projects,setProjects]=useState<Project[]>([]),[tasks,setTasks]=useState<Task[]>([]),[modal,setModal]=useState<'task'|'project'|null>(null);
 const [error,setError]=useState(''),[loading,setLoading]=useState(false),[moving,setMoving]=useState(false);
 function logout(){api.logout();setUser(null);setDashboard(null);setProjects([]);setTasks([]);setModal(null);setError('')}
 function handleError(error:unknown){
   if(error instanceof ApiError&&error.status===401){logout();return}
   setError(error instanceof Error?error.message:'Não foi possível concluir a operação. Tente novamente.');
 }
 async function load(){
   setLoading(true);setError('');
   try{const [d,p,t]=await Promise.all([api.dashboard(),api.projects(),api.tasks()]);setDashboard(d);setProjects(p);setTasks(t)}
   catch(error){handleError(error)}finally{setLoading(false)}
 }
 useEffect(()=>{if(user)void load()},[user]);
 async function move(id:number,status:TaskStatus){
   if(moving)return;
   setMoving(true);setError('');
   try{
     const updated=await api.moveTask(id,status);
     setTasks(v=>v.map(t=>t.id===id?updated:t));
     const d=await api.dashboard();setDashboard(d);
   }catch(error){handleError(error)}finally{setMoving(false)}
 }
 if(!user)return <Login onLogin={setUser}/>;
 const canManage=user.role==='ADMIN'||user.role==='MANAGER';
 const content=page==='Dashboard'?<Dashboard canManage={canManage} data={dashboard} projects={projects} user={user} onNew={()=>setModal('project')}/>:page==='Kanban'||page==='Tarefas'?<Kanban canManage={canManage} tasks={tasks} projects={projects} onMove={move} onNew={()=>setModal('task')}/>:page==='Projetos'?<Projects canManage={canManage} projects={projects} tasks={tasks} onNew={()=>setModal('project')}/>:<Placeholder page={page}/>;
 return <div className="app"><Sidebar page={page} setPage={setPage} open={open} setOpen={setOpen} user={user} onLogout={logout}/><main><Header page={page} onMenu={()=>setOpen(true)}/>{error&&<div className="error app-error" role="alert"><span>{error}</span><button className="secondary" disabled={loading} onClick={()=>void load()}>{loading?'Carregando...':'Atualizar dados'}</button></div>}{!dashboard?(loading?<Loading/>:null):content}</main>{modal&&canManage&&<Modal kind={modal} projects={projects} onClose={()=>setModal(null)} onSessionExpired={logout} onDone={()=>{setModal(null);void load()}}/>}</div>
}
