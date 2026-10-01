# UNIKO-RD — contexto completo (app Android)

## 0. Repos (clonar ambas)

| Repo | Clonación | Contenido |
|---|---|---|
| **App Android (este repo)** | `git clone https://github.com/raemarketingservices/Uniko-RD-App.git` | Kotlin + Jetpack Compose + Room + OkHttp |
| **Web** | `git clone https://github.com/raemarketingservices/unikordpaginaweb.git` | TanStack Start + Vite + Tailwind v4 + Supabase JS |

- Ambas bajo la organización `raemarketingservices`; remoto `origin` = la URL de arriba.
- La web es la fuente de verdad de la BD, el design system y el deploy: ver `uniko-rd-marketplace/CLAUDE.md` y, sobre todo, `supabase/OPERACION.md` de ese repo.
- Directorios actuales: app en `unikord/Uniko-RD-App`, web en `unikord/uniko-rd-marketplace`.

## 1. Qué es esta app
Cliente Android del marketplace UNIKO-RD (compra de productos, tiendas, servicios, checkout con envío de la solicitud de compra a Supabase). Habla **directo con la API de Supabase self-hosted** (no usa backend propio) y además guarda copia local en Room para funcionar con datos cacheados.

## 2. Comandos
- Compilar Kotlin: `.\gradlew.bat compileDebugKotlin` (verifica que el `.class` quede más nuevo que el `.kt` en `app\build\intermediates\built_in_kotlin\debug\compileDebugKotlin\classes\`).
- APK debug: `.\gradlew.bat assembleDebug`; release firmado con `debug.keystore` de la raíz.
- SDK en `local.properties` (`sdk.dir=C:\Users\Admin\AppData\Local\Android\Sdk`), JDK 21 (Eclipse Adoptium) en el PATH.
- APK más reciente: `C:\Users\Admin\Desktop\UNIKO-RD.apk`.
- No hay tests unitarios útiles (`app/src/test`, `app/src/androidTest` vacíos).

## 3. Estructura (`app/src/main/java/com/example/`)
- `data/Remoto.kt` → cliente OkHttp a Supabase: `BASE = "https://uniko-rd.com"`, `API_KEY = sb_publishable_qDTaqHyWWdy92o7G6InGDJ_WEugr_zv` (misma que la web). Endpoints: `signup`, `token` (password/refresh), `recover`, `stores`, `products`, `services` y `POST /rest/v1/purchase_requests` (`enviarSolicitud`).
- `data/AppDatabase.kt`, `data/Daos.kt`, `data/Entities.kt` → Room. Claves: `ProductEntity.storeId`/`storeName`, `StoreEntity.id`, `cart_items`.
- `data/UnikoRepository.kt` → sincroniza Supabase ↔ Room (`tiendas()` → `storeDao().insertStores(...)`).
- `data/Sesion.kt` → sesión/usuario guardado localmente.
- `ui/UnikoViewModel.kt` → estado global (`products`, `cartItems`, `_enviandoCompra`, toasts). **`enviarSolicitudCompra(...)`** valida el formulario y arma el JSON de la orden: `full_name`, `address`, `phone`, `email`, `cedula`, `note`, `items[] {id,titulo,tienda,precio,cantidad}`, `total`, `source:'app'`, `user_id` y **`store_ids[]`** (los `storeId` distintos del carrito, para que el vendedor vea la orden en el panel web `/ordenes`).
- `ui/screens/` → `HomeScreen`, `ProductsScreen`, `ProductDetailScreen`, `StoresScreen`, `StoreProfileScreen`, `ServicesScreen`, `ServiceDetailScreen`, `CheckoutScreen`, `PublishProductScreen`, `AuthScreen`, `AdminScreen`, `LegalDocScreen`.
- `ui/components/` → `SharedComponents.kt`, `FilterBottomSheet.kt`; `ui/theme/` → `themeColor.kt`, `themeTheme.kt`, `themeType.kt` (colores/marca: navy `#0033A0`, rojo `#CC0033`).

## 4. Integración con Supabase self-hosted (VPS `84.46.254.137`)
- Todo pasa por `https://uniko-rd.com` → Traefik de Coolify enruta `/rest/v1`, `/auth/v1`, `/storage/v1`, `/functions/v1`, `/realtime/v1` a `supabase-envoy`.
- Migraciones y RLS viven en el repo **web** (`supabase/migration_v2..v8.sql`). La `migration_v8.sql` añadió `purchase_requests.store_ids text[]` (GIN) + RLS de vendedor (por `store_ids` y respaldo por `items[].tienda` = `stores.name`) y la RPC `tienda_contactos(text[])`. **Sin `store_ids`, el vendedor no ve las órdenes hechas desde la app.**
- `stores.id` es **text**; `profiles` solo es legible por el propio usuario/admin.
- Prueba de integración (web): `node --env-file=.env.local scripts/verify-marketplace.mjs` (requiere `SUPABASE_SERVICE_ROLE_KEY`; crea datos temporales y los borra; los tests de WhatsApp envían mensajes **reales**).

## 5. VPS + Coolify (deploy del backend con el que habla la app)
- VPS `84.46.254.137`, password en la env `VPS_PASS`, helper del repo web `python scripts\vps-ssh.py "comando"`.
  **Riesgo de ban**: tras varios intentos de SSH el host puede filtrar 22/443 manteniendo el ping OK (fail2ban/CSF). Si pasa, esperar; no insistir.
- **Coolify** redespliega la web (`unikord-web`) en cada `git push a main` del repo web; la BD/Supabase es un stack aparte en el mismo host.
- Env vars cifradas en Coolify con `APP_KEY` (`Crypt::encrypt()`, serialize=true); DB `coolify-db` / tabla `environment_variables`.
- Traefik: `uniko-web` y `supabase-envoy` **deben** llevar `traefik.docker.network=coolify` (si no → 504 a los 30 s).
- SQL aplicado con stdin (el `-f` no ve `/tmp` del host): `docker exec -i supabase-db psql -U supabase_admin -d postgres ... < archivo.sql`.

## 6. WhatsApp / Meta Cloud API
- Lo implementa la **web** (`/api/whatsapp/avisar` y `/api/whatsapp/webhook`); la app solo dispara la orden y el web se encarga de avisar. Número `+1 849-627-9994`, ventana de 24 h (sin plantillas aprobadas), teléfonos DR se normalizan con prefijo `1`.

## 7. UI / estilo
- Textos en español, colores de marca de `ui/theme/themeColor.kt`, logos en `app/src/main/res`. Coherencia visual con la web (`btn-base`/`card-uniko`, patrón de tabla de `GestionTiendas.tsx`).

## 8. Reglas de entorno (leer antes de ejecutar nada)
- **Shell = Windows PowerShell 5.1**: NO existe `&&`/`||`, no hay `head`/`tail`/`timeout`/heredoc `<<`, `$PID` es read-only. Encadenar con `;` y `if ($?)`.
- **Nunca editar código con `Set-Content`/`Out-File`**: sin `-Encoding UTF8` produce mojibake (ya se rompieron archivos así). Usar el editor/escritura de herramientas y `Get-Content -Encoding UTF8` para leer.
- Scripts largos/complejos → guardarlos en archivo y ejecutarlos, no incrustarlos con comillas anidadas.
- **No reescribir historia de git**: sin force-push, rebase ni amend de commits publicados. Commits en `main` sin acentos, estilo `feat:`/`fix:`/`style:`.
- `git push origin main` = publicar. Confirmar antes de pushear.
- Secretos nunca en el repo (la publishable key sí va en código; las service role keys y `VPS_PASS` no).
- Después de cada cambio: `.\gradlew.bat compileDebugKotlin` → commit → push.

## 9. Estado conocido (punto de partida)
- App `main` = `e595be4` *feat: store_ids en la solicitud de compra de la app* (compilado antes de subir); web `main` = `d3ec07f`.
- El VPS puede estar filtrando nuestra IP en TCP (ping OK, 22/443 sin respuesta): afecta los builds/verificación contra `https://uniko-rd.com`, no es un bug del código.
- No hay credenciales de vendedor para probar el panel web; la validación RLS se hace por SQL.
