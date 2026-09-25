import test from 'node:test';
import assert from 'node:assert/strict';

const storage = new Map([['devflow_token', 'test-token']]);
globalThis.localStorage = {
  getItem: key => storage.get(key) ?? null,
  setItem: (key, value) => storage.set(key, value),
  removeItem: key => storage.delete(key),
};
const {api, ApiError} = await import('../src/api.ts');

test('connection failures retain credentials and provide a retry message', async () => {
  globalThis.fetch = async () => {throw new TypeError('Failed to fetch')};
  await assert.rejects(api.dashboard(), error => error instanceof ApiError && error.status === 0 && error.message.includes('conexão'));
  assert.equal(storage.get('devflow_token'), 'test-token');
});

test('validation messages from the backend reach the form', async () => {
  globalThis.fetch = async () => new Response(JSON.stringify({message: 'Revise os campos informados.', fields: {title: 'Título obrigatório'}}), {status: 400});
  await assert.rejects(api.createTask({}), error => error.status === 400 && error.message.includes('Título obrigatório'));
});

test('unauthorized, forbidden and server errors remain distinguishable without a JSON body', async () => {
  for (const status of [401, 403, 503]) {
    globalThis.fetch = async () => new Response('Unavailable', {status});
    await assert.rejects(api.projects(), error => error instanceof ApiError && error.status === status);
  }
});

test('task movement sends the status and returns the saved task', async () => {
  globalThis.fetch = async (url, options) => {
    assert.equal(url, '/api/tasks/7/status');
    assert.equal(options.method, 'PATCH');
    assert.equal(options.headers.Authorization, 'Bearer test-token');
    assert.deepEqual(JSON.parse(options.body), {status: 'DONE'});
    return Response.json({id: 7, status: 'DONE'});
  };
  assert.deepEqual(await api.moveTask(7, 'DONE'), {id: 7, status: 'DONE'});
});
