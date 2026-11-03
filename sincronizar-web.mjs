/**
 * Arma `www/` para Capacitor a partir del build de la web.
 *
 * La web se construye con SSR en Cloudflare Pages y no emite index.html estático,
 * así que la cáscara (head + script de arranque) se toma de la URL publicada y el
 * resto de los archivos del build local.
 *
 * Uso:  node sincronizar-web.mjs [--dist=<ruta>] [--shell=<url>]
 */
import { cpSync, mkdirSync, readdirSync, rmSync, writeFileSync, existsSync, readFileSync } from "node:fs";
import { join, resolve } from "node:path";

const args = Object.fromEntries(
  process.argv.slice(2).map((a) => {
    const [k, v] = a.replace(/^--/, "").split("=");
    return [k, v];
  }),
);

const DIST = resolve(
  args.dist ??
    "C:/Users/Admin/Desktop/Open Code APPs/unikord/uniko-rd-marketplace/dist",
);
const SHELL_URL = args.shell ?? "https://app.uniko-rd.com/";
const WWW = resolve("www");

if (!existsSync(join(DIST, "assets"))) {
  console.error(`No hay build en ${DIST} (falta assets/). Construya la web primero.`);
  process.exit(1);
}

const res = await fetch(SHELL_URL, { headers: { "Accept-Encoding": "identity" } });
if (!res.ok) {
  console.error(`No se pudo descargar la cáscara de ${SHELL_URL}: HTTP ${res.status}`);
  process.exit(1);
}
const html = await res.text();
if (!/<script[^>]+src="\/assets\/[^"]+"/.test(html)) {
  console.error("La cáscara descargada no referencia /assets/: se aborta.");
  process.exit(1);
}

// `www/` puede estar bloqueado por un proceso (Android Studio, un server local): se vacía por dentro.
mkdirSync(WWW, { recursive: true });
for (const hijo of readdirSync(WWW)) {
  rmSync(join(WWW, hijo), { recursive: true, force: true, maxRetries: 3 });
}

for (const carpeta of ["assets", "icons", "products"]) {
  if (existsSync(join(DIST, carpeta))) {
    cpSync(join(DIST, carpeta), join(WWW, carpeta), { recursive: true });
  }
}
for (const archivo of [
  "favicon.png",
  "uniko-logo.png",
  "uniko-logo-blanco.png",
  "og-image.png",
  "manifest.webmanifest",
]) {
  if (existsSync(join(DIST, archivo))) {
    cpSync(join(DIST, archivo), join(WWW, archivo));
  }
}

// En el envoltorio nativo no sirve de nada el service worker de la web.
writeFileSync(join(WWW, "index.html"), html.replace(/<link rel="manifest"[^>]*>/, ""));

const total = readFileSync(join(WWW, "index.html"), "utf8").length;
console.log(`www/ listo: shell de ${SHELL_URL} (${total} bytes) + assets de ${DIST}`);
