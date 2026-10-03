# UNIKO-RD — Web app móvil

Este repositorio reemplaza la aplicación Android anterior por la web app móvil de UNIKO-RD. El historial conserva el código anterior.

## Ejecutar

Requiere Node.js 22.16 o superior.

```sh
npm ci
cp .env.example .env
# Configura UNIKO_ADMIN_PASSWORD en .env.
npm run dev
```

En PowerShell usa `Copy-Item .env.example .env`.

Abre http://127.0.0.1:5056. Administración: http://127.0.0.1:5056/admin.

## Funciones

- Landing adaptable a celulares, registro de compradores y vendedores y acceso por usuario o correo.
- Catálogo conectado a la API existente, filtros, productos, tiendas y servicios.
- Inicio de sesión obligatorio para comprar.
- Mensajes entre compradores y tiendas guardados por el servidor local.
- Panel para editar portada, banner, colores, catálogo y aprobar o suspender tiendas.
- Footer compacto, animaciones suaves y soporte de movimiento reducido.

## Datos y alcance

La API remota mantiene las cuentas, tiendas y productos originales. Los alias de usuario, mensajes, cambios administrativos y aprobaciones de esta versión se guardan en `.local-data/`, excluido de Git. Las aprobaciones se aplican en esta app local; no modifican la política del sitio remoto. El listado de usuarios del panel contiene las cuentas creadas a través de esta versión.

Las funciones locales se sirven mediante middleware de Vite. Un build estático o de producción no incluye ese servidor. Para publicar con todas las funciones hace falta trasladarlo a un backend persistente y configurar autenticación y almacenamiento de producción.

## Comprobaciones

Con el servidor ejecutándose:

```sh
npx tsc --noEmit
node check-local.mjs
node check-admin.mjs
```

La comprobación de admin usa UNIKO_ADMIN_PASSWORD de `.env`, prueba cambios locales temporales y restaura la configuración al terminar.
