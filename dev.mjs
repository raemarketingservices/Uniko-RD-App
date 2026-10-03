import { spawn } from 'node:child_process';
import { existsSync } from 'node:fs';
import { loadEnvFile } from 'node:process';
if (existsSync('.env')) loadEnvFile('.env');
const child = spawn(process.execPath, ['node_modules/vite/bin/vite.js', '--host', '127.0.0.1', '--port', '5056', ...process.argv.slice(2)], {
  stdio: 'inherit', env: { ...process.env, UNIKO_LOCAL_MESSAGES: 'true', VITE_LOCAL_CATALOG: 'false' },
});
child.on('exit', code => process.exit(code ?? 1));
child.on('error', error => { console.error(error.message); process.exit(1); });
