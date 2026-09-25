import test from 'node:test';
import assert from 'node:assert/strict';
import { statusGradient, projectProgress } from '../src/metrics.ts';

test('status chart uses actual proportions and handles an empty board', () => {
  assert.equal(statusGradient([{count: 3, color: 'blue'}, {count: 1, color: 'green'}]), 'conic-gradient(blue 0% 75%,green 75% 100%)');
  assert.equal(statusGradient([{count: 0, color: 'blue'}]), '#e5eaf2');
});

test('project progress includes only its tasks and handles no tasks', () => {
  const tasks = [{project: {id: 1}, status: 'DONE'}, {project: {id: 1}, status: 'TODO'}, {project: {id: 2}, status: 'DONE'}];
  assert.deepEqual(projectProgress(tasks, 1), {total: 2, completed: 1, percent: 50});
  assert.deepEqual(projectProgress(tasks, 3), {total: 0, completed: 0, percent: 0});
  assert.equal(projectProgress(tasks, 2).percent, 100);
});
