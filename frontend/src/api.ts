import type {DashboardData,Project,Task,TaskStatus,User} from './types';
const base=import.meta.env?.VITE_API_URL||'/api';
let token=localStorage.getItem('devflow_token');
export class ApiError extends Error {
  status: number;
  constructor(status: number, message: string) {
    super(message);
    this.name='ApiError';
    this.status=status;
  }
}
async function request<T>(path:string,options:RequestInit={}):Promise<T>{
  let response: Response;
  try {
    response=await fetch(base+path,{...options,headers:{'Content-Type':'application/json',...(token?{Authorization:`Bearer ${token}`}:{ }),...options.headers}});
  } catch {
    throw new ApiError(0,'Não foi possível conectar ao servidor. Verifique sua conexão e tente novamente.');
  }
  if(!response.ok){
    const body=await response.json().catch(()=>null);
    const fallback=response.status===401?'Sua sessão expirou. Entre novamente.':response.status===403?'Você não tem permissão para realizar esta ação.':'Falha ao acessar a API. Tente novamente.';
    const message=typeof body?.message==='string'?body.message:fallback;
    const fields=body?.fields&&typeof body.fields==='object'?Object.values(body.fields).filter((value):value is string=>typeof value==='string'):[];
    throw new ApiError(response.status,[message,...new Set(fields)].join(' '));
  }
  return response.status===204?undefined as T:response.json();
}
export const api={
  async login(email:string,password:string){const data=await request<{token:string;user:User}>('/auth/login',{method:'POST',body:JSON.stringify({email,password})});token=data.token;localStorage.setItem('devflow_token',token);localStorage.setItem('devflow_user',JSON.stringify(data.user));return data;},
  logout(){token=null;localStorage.removeItem('devflow_token');localStorage.removeItem('devflow_user')},
  dashboard:()=>request<DashboardData>('/dashboard'),
  projects:()=>request<Project[]>('/projects'),
  createProject:(body:object)=>request<Project>('/projects',{method:'POST',body:JSON.stringify(body)}),
  tasks:(filters?:{search?:string;status?:TaskStatus;priority?:string;projectId?:number})=>{
    const params=new URLSearchParams();
    if(filters?.search)params.set('search',filters.search);
    if(filters?.status)params.set('status',filters.status);
    if(filters?.priority)params.set('priority',filters.priority);
    if(filters?.projectId)params.set('projectId',String(filters.projectId));
    const query=params.toString();
    return request<Task[]>(`/tasks${query?`?${query}`:''}`);
  },
  createTask:(body:object)=>request<Task>('/tasks',{method:'POST',body:JSON.stringify(body)}),
  moveTask:(id:number,status:TaskStatus)=>request<Task>(`/tasks/${id}/status`,{method:'PATCH',body:JSON.stringify({status})})
};
