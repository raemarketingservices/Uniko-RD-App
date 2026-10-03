import assert from 'node:assert/strict';
import { existsSync, readFileSync, writeFileSync, mkdirSync } from 'node:fs';
import { loadEnvFile } from 'node:process';
if(existsSync('.env')) loadEnvFile('.env');
const base='http://127.0.0.1:5056/local-api/marketplace';
const file=new URL('./.local-data/admin.json',import.meta.url);
const before=existsSync(file)?readFileSync(file,'utf8'):JSON.stringify({edits:{},deleted:{},pending:[],profiles:[],settings:{}});
async function call(path,method='GET',data,token='') {
  const response=await fetch(base+path,{method,headers:{'Content-Type':'application/json',Authorization:`Bearer ${token}`},...(data?{body:JSON.stringify(data)}:{})});
  return {status:response.status,data:await response.json()};
}
try {
  assert.equal((await call('/api/admin/stores')).status,403);
  assert.equal((await call('/api/auth/admin','POST',{password:'wrong'})).status,401);
  assert.ok(process.env.UNIKO_ADMIN_PASSWORD,'Configura UNIKO_ADMIN_PASSWORD');
  const login=await call('/api/auth/admin','POST',{password:process.env.UNIKO_ADMIN_PASSWORD});
  assert.equal(login.status,200);
  const token=login.data.token;
  assert.equal((await call('/api/auth/me','GET',null,token)).data.profile.role,'admin');
  const stores=await call('/api/admin/stores','GET',null,token);
  assert.equal(stores.status,200); assert.ok(stores.data.length);
  const store=stores.data[0];
  assert.equal((await call(`/api/admin/stores/${store.id}`,'PATCH',{approved:false},token)).status,200);
  assert.ok(!(await call('/api/stores')).data.some(s=>s.id===store.id));
  assert.equal((await call('/api/products','POST',{store_id:store.id})).status,403);
  assert.equal((await call(`/api/admin/stores/${store.id}`,'PATCH',{approved:true},token)).status,200);
  assert.ok((await call('/api/stores')).data.some(s=>s.id===store.id));
  assert.equal((await call('/api/admin/app-settings','PATCH',{headline:'Prueba del editor'},token)).status,200);
  assert.equal((await call('/api/app-settings')).data.headline,'Prueba del editor');
  await call('/api/auth/logout','POST',{},token);
  assert.equal((await call('/api/admin/stores','GET',null,token)).status,403);
  console.log('OK: contraseña, permisos, aprobación de tiendas y edición persistente.');
} finally {mkdirSync(new URL('./.local-data/',import.meta.url),{recursive:true});writeFileSync(file,before);}
