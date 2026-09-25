import type {Task} from './types';

export function statusGradient(segments: {count: number; color: string}[]): string {
  const total = segments.reduce((sum, segment) => sum + segment.count, 0);
  if (!total) return '#e5eaf2';
  let accumulated = 0;
  return `conic-gradient(${segments.map(({count, color}) => {
    const start = accumulated / total * 100;
    accumulated += count;
    return `${color} ${start}% ${accumulated / total * 100}%`;
  }).join(',')})`;
}

export function projectProgress(tasks: Pick<Task, 'project' | 'status'>[], projectId: number) {
  const projectTasks = tasks.filter(task => task.project.id === projectId);
  const total = projectTasks.length;
  const completed = projectTasks.filter(task => task.status === 'DONE').length;
  return {total, completed, percent: total ? Math.round(completed / total * 100) : 0};
}
