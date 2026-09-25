export type TaskStatus='BACKLOG'|'TODO'|'DOING'|'REVIEW'|'DONE';
export type Priority='LOW'|'MEDIUM'|'HIGH';
export interface User{id:number;name:string;email:string;role:string}
export interface Project{id:number;name:string;description:string;status:string;createdAt:string}
export interface Task{id:number;title:string;description:string;status:TaskStatus;priority:Priority;project:Project;assignee?:User;dueDate?:string}
export interface DashboardData{activeProjects:number;inProgress:number;completed:number;members:number;byStatus:Record<TaskStatus,number>;productivity:number[];activities:string[]}
