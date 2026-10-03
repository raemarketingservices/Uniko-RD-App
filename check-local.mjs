import assert from 'node:assert/strict';
const base = 'http://127.0.0.1:5056';
for (const path of ['/', '/productos', '/auth?modo=crear', '/tiendas', '/mensajes']) {
  const response = await fetch(base + path);
  assert.equal(response.status, 200, path);
  const html = await response.text();
  assert.ok(html.includes('UNIKO-RD'), path);
  assert.ok(!html.includes('fetch failed'), path);
}
for (const method of ['GET', 'POST']) {
  const response = await fetch(base + '/local-api/messages', { method });
  assert.equal(response.status, 401);
}
console.log('OK: páginas disponibles y mensajes protegidos sin sesión.');
const login = await fetch(base + '/local-api/auth/login', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ username: '__unknown_local_check__', password: 'invalid' }) });
assert.equal(login.status, 401);
const register = await fetch(base + '/local-api/auth/register', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ username: '!invalid' }) });
assert.equal(register.status, 400);
console.log('OK: acceso por usuario y validación de registro.');
const purchase = await fetch(base + '/local-api/marketplace/api/purchase-requests', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: '{}' });
assert.equal(purchase.status, 401);
console.log('OK: compra bloqueada sin sesión también en el servidor.');
