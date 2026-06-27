# 🔧 Ferretería Molina — Sistema de Gestión Integral

> **Proyecto Integrador | CIBERTEC 2026**
> Desarrollo de Aplicaciones Web II — 6to Ciclo
> Arquitectura de Microservicios desplegada **100% en la nube** con Spring Cloud, Angular y MySQL

---

> [!IMPORTANT]
> ## ☁️ Proyecto Desplegado en Railway
> Este proyecto está ejecutándose **completamente en la nube** utilizando [Railway](https://railway.app).
> Eso incluye **todos los microservicios**, el **frontend Angular** y la **base de datos MySQL**.
> No se requiere Docker, ni MySQL local, ni ninguna instalación adicional para usarlo.
>
> | Componente | URL Pública |
> |---|---|
> | 🌐 Frontend Angular | https://frontend-angular-production-4c28.up.railway.app |
> | 🚪 API Gateway | https://ms-gateway-production-527d.up.railway.app |
> | 📋 Eureka Dashboard | https://eureka-server-production-4946.up.railway.app |
> | ⚙️ Config Server | https://config-server-production-49a0.up.railway.app |

---

## 📌 Descripción General

**Ferretería Molina** es un sistema de gestión empresarial completo diseñado para digitalizar y automatizar las operaciones de una ferretería. Permite controlar en tiempo real el inventario de productos, registrar ventas, gestionar clientes, administrar proveedores, procesar devoluciones y controlar el acceso de usuarios mediante roles diferenciados (Administrador y Empleado).

El sistema está construido sobre una **arquitectura de microservicios** desplegada íntegramente en la nube de **Railway**, lo que garantiza disponibilidad 24/7, escalabilidad independiente por módulo y tolerancia a fallos.

---

## 🏗️ Arquitectura del Sistema

```
                         ┌────────────────────────────────────┐
                         │         INTERNET / USUARIOS        │
                         └───────────────┬────────────────────┘
                                         │
                         ┌───────────────▼────────────────────┐
                         │        FRONTEND ANGULAR 19          │
                         │  (Railway — Static Site via Caddy)  │
                         └───────────────┬────────────────────┘
                                         │ HTTPS
                         ┌───────────────▼────────────────────┐
                         │          MS-GATEWAY :8080           │
                         │   (API Gateway + Filtro JWT Global) │
                         └──┬──────┬──────┬──────┬────────────┘
                            │      │      │      │  (Eureka lb://)
          ┌─────────────────▼─┐  ┌─▼──────▼─┐  ┌─▼──────────────┐
          │   ms-auth :8090   │  │ ms-clien. │  │  ms-productos  │
          │  (Login + JWT +   │  │  :8093    │  │    :8091       │
          │  Gestión Usuarios)│  │ (Clientes)│  │(Prod+Proveed.) │
          └───────────────────┘  └─────┬─────┘  └──────┬─────────┘
                                       │  Feign         │  Feign
                               ┌───────▼────────────────▼────────┐
                               │         ms-ventas :8092          │
                               │  (Ventas + Devoluciones)         │
                               └─────────────────────────────────┘
                                         ▲         ▲
                         ┌───────────────┘         │
          ┌──────────────┴──────────┐   ┌──────────┴───────────┐
          │    EUREKA SERVER :8761  │   │  CONFIG SERVER :8888  │
          │  (Service Discovery)    │   │  (Config Centralizada)│
          └─────────────────────────┘   └──────────────────────┘
                                         ▲
                              ┌──────────┘
                              │ MySQL 8 (Railway DB)
                              │  Base de datos compartida
                              └─────────────────────────
```

### Flujo de Comunicación

1. El **usuario** accede al **Frontend Angular** desplegado en Railway.
2. Angular envía todas sus peticiones HTTP al **API Gateway** (`ms-gateway`).
3. El **Gateway** intercepta cada petición, valida el **token JWT** y, si es válido, reenvía la petición al microservicio correspondiente descubriéndolo mediante **Eureka**.
4. Al arrancar, cada microservicio descarga su configuración del **Config Server**.
5. **ms-ventas** se comunica con **ms-clientes** y **ms-productos** a través de **OpenFeign** para validar datos y descontar stock automáticamente.

---

## 🗂️ Estructura del Proyecto

```
ProyectoFerreteria_DAWII/
│
├── pom.xml                        ← Parent POM (proyecto multi-módulo Maven)
├── README.md                      ← Este archivo
│
├── eureka-server/                 ← Service Discovery (Netflix Eureka)      :8761
│   └── src/main/resources/
│       └── application.yml
│
├── config-server/                 ← Configuración Centralizada              :8888
│   └── src/main/resources/
│       ├── application.yml        ← Config del propio config-server
│       └── configs/               ← YAMLs por microservicio
│           ├── application.yml    ← Config compartida (Eureka, timeouts)
│           ├── ms-auth.yml        ← BD, JPA, JWT secret
│           ├── ms-clientes.yml    ← BD, JPA, Feign
│           ├── ms-productos.yml   ← BD, JPA
│           ├── ms-ventas.yml      ← BD, JPA, Feign
│           └── ms-gateway.yml     ← Rutas, CORS, JWT
│
├── ms-auth/                       ← Autenticación y Usuarios                :8090
│   └── src/main/java/com/ferreteria/auth/
│       ├── controller/
│       │   ├── AuthController.java       ← POST /api/auth/login
│       │   └── UsuarioController.java    ← CRUD /api/usuarios
│       ├── service/
│       ├── repository/
│       ├── model/
│       ├── filter/
│       │   └── JwtAuthFilter.java        ← Valida JWT en cada petición
│       └── util/JwtUtil.java             ← Genera y valida tokens
│
├── ms-clientes/                   ← Gestión de Clientes                     :8093
│   └── src/main/java/com/ferreteria/clientes/
│       ├── controller/ClienteController.java
│       ├── service/, repository/, model/
│
├── ms-productos/                  ← Productos y Proveedores                 :8091
│   └── src/main/java/com/ferreteria/productos/
│       ├── controller/
│       │   ├── ProductoController.java   ← CRUD + stock
│       │   └── ProveedorController.java
│       ├── service/, repository/, model/
│
├── ms-ventas/                     ← Ventas y Devoluciones                   :8092
│   └── src/main/java/com/ferreteria/ventas/
│       ├── controller/
│       │   ├── VentaController.java      ← CRUD + dashboard
│       │   └── DevolucionController.java
│       ├── client/                       ← Clientes OpenFeign
│       │   ├── ClienteClient.java        ← Feign → ms-clientes
│       │   └── ProductoClient.java       ← Feign → ms-productos
│       ├── service/, repository/, model/
│
├── ms-gateway/                    ← API Gateway                             :8080
│   └── src/main/java/com/ferreteria/gateway/
│       └── filter/JwtAuthGatewayFilter.java  ← Filtro JWT global reactivo
│
└── frontend-angular/              ← Frontend SPA Angular 19
    └── src/
        ├── app/
        │   ├── components/        ← Componentes de UI
        │   ├── services/          ← Servicios HTTP
        │   ├── guards/            ← Protección de rutas
        │   └── models/            ← Interfaces TypeScript
        └── environments/
            ├── environment.ts          ← URL para desarrollo local
            └── environment.prod.ts     ← URL del Gateway en Railway
```

---

## 🚀 Tecnologías y Herramientas

### Backend — Microservicios

| Tecnología | Versión | Rol en el proyecto |
|---|---|---|
| **Java** | 21 LTS | Lenguaje base de todos los microservicios |
| **Spring Boot** | 3.2.5 | Framework principal para crear APIs REST |
| **Spring Cloud** | 2023.0.1 | Ecosistema completo de microservicios |
| **Spring Cloud Config** | 4.1.x | Servidor de configuración centralizada |
| **Spring Cloud Gateway** | 4.1.x | API Gateway reactivo (punto de entrada único) |
| **Netflix Eureka** | 4.1.x | Registro y descubrimiento de servicios |
| **OpenFeign** | 4.1.x | Comunicación declarativa HTTP entre servicios |
| **Spring Security** | 6.x | Framework de seguridad y autorización |
| **Spring Data JPA** | 3.2.x | Persistencia ORM con Hibernate |
| **jjwt** | 0.12.6 | Generación y validación de tokens JWT |
| **Lombok** | 1.18.x | Eliminación de código repetitivo (getters, constructores) |
| **MySQL** | 8.x | Base de datos relacional en la nube |
| **Maven** | 3.9.x | Gestión de dependencias y compilación |

### Frontend

| Tecnología | Versión | Rol en el proyecto |
|---|---|---|
| **Angular** | 19/21 | Framework SPA de interfaz de usuario |
| **TypeScript** | 5.x | JavaScript tipado para mayor seguridad |
| **TailwindCSS** | 4.x | Estilos utilitarios para el diseño |
| **Node.js** | 22.x | Runtime de ejecución para Angular |

### Infraestructura y DevOps

| Herramienta | Uso |
|---|---|
| **Railway** | Plataforma de despliegue en la nube (PaaS) |
| **Nixpacks** | Builder automático de Railway (detecta Spring Boot y Angular) |
| **Caddy** | Servidor web de archivos estáticos para el frontend |
| **GitHub** | Control de versiones y trigger de deploys automáticos |
| **Git** | Versionado del código fuente |

---

## ✅ Ventajas de la Arquitectura de Microservicios

### 1. 🔧 Independencia de despliegue
Cada microservicio se compila, despliega y actualiza de forma completamente independiente. Un cambio en la lógica de ventas no requiere tocar ni reiniciar el módulo de clientes o el de autenticación.

### 2. 📈 Escalabilidad granular
Si el módulo de ventas recibe mucha carga (por ejemplo, en temporada alta), se puede escalar horizontalmente solo ese servicio, sin necesidad de escalar toda la aplicación. Esto optimiza costos de infraestructura.

### 3. 🛡️ Tolerancia a fallos aislada
Si `ms-productos` cae o tiene un error, el módulo de autenticación y el de clientes siguen funcionando con normalidad. Los fallos están contenidos y no propagan un caos general.

### 4. ⚙️ Configuración centralizada
El **Config Server** almacena toda la configuración sensible (credenciales de BD, secretos JWT, URLs) en un único lugar. Para cambiar la contraseña de la base de datos de todos los servicios, solo se modifica un archivo, sin recompilar nada.

### 5. 🔍 Descubrimiento automático de servicios
**Eureka** actúa como una "guía telefónica" interna. Cuando el Gateway necesita hablar con `ms-ventas`, no necesita saber su IP ni su puerto exacto; le pregunta a Eureka y él le da la dirección actualizada. Esto hace la red interna dinámica y resiliente.

### 6. 🚪 Punto de entrada único y seguro
El **API Gateway** centraliza la seguridad en un solo lugar. Valida el token JWT antes de permitir el paso. Los microservicios internos no necesitan implementar seguridad propia; confían en que el Gateway ya filtró el tráfico.

### 7. ☁️ Nativo para la nube
La arquitectura fue diseñada desde el principio para correr en la nube (Railway). Usa variables de entorno para toda la configuración sensible, sin credenciales hardcodeadas, siguiendo los principios del **12-Factor App**.

---

## 🌐 Despliegue en la Nube (Railway)

El proyecto está desplegado 100% en Railway, distribuido en múltiples servicios:

| Servicio | URL Pública | Cuenta Railway |
|---|---|---|
| Eureka Server | https://eureka-server-production-4946.up.railway.app | Cuenta 2 |
| Config Server | https://config-server-production-49a0.up.railway.app | Cuenta 2 |
| ms-auth | *(dominio interno Railway)* | Cuenta 1 |
| ms-clientes | *(dominio interno Railway)* | Cuenta 1 |
| ms-productos | *(dominio interno Railway)* | Cuenta 1 |
| ms-ventas | *(dominio interno Railway)* | Cuenta 1 |
| ms-gateway | https://ms-gateway-production-527d.up.railway.app | Cuenta 1 |
| Frontend Angular | https://frontend-angular-production-4c28.up.railway.app | Cuenta 1 |
| MySQL Database | *(conexión interna Railway)* | Cuenta 1 |

### Variables de entorno requeridas por microservicio

Cada microservicio (`ms-auth`, `ms-clientes`, `ms-productos`, `ms-ventas`, `ms-gateway`) requiere estas variables en Railway:

| Variable | Descripción |
|---|---|
| `PORT` | Puerto en que escucha el servicio |
| `CONFIG_SERVER_URL` | URL pública del config-server en Railway |
| `EUREKA_URL` | URL pública de Eureka + `/eureka` al final |
| `RAILWAY_PUBLIC_DOMAIN` | Dominio público del servicio (sin `https://`) |
| `MYSQLHOST` | Host de la base de datos MySQL de Railway |
| `MYSQLPORT` | Puerto de MySQL (normalmente 3306) |
| `MYSQLDATABASE` | Nombre de la base de datos |
| `MYSQLUSER` | Usuario de MySQL |
| `MYSQLPASSWORD` | Contraseña de MySQL |

El **frontend-angular** solo requiere:

| Variable | Descripción |
|---|---|
| `API_URL` | URL pública del gateway + `/api` al final |

---

## 🔐 Credenciales del Sistema

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@ferreteria.com | admin123 |
| Empleado | empleado@ferreteria.com | empleado123 |

> Las tablas y datos iniciales se crean automáticamente al arrancar via JPA `ddl-auto: update`.

---

## 📡 Endpoints de la API (vía Gateway)

Todas las peticiones se realizan a través del API Gateway.
El endpoint de login es público; todos los demás requieren `Authorization: Bearer <token>`.

### Autenticación
| Método | Endpoint | Auth |
|---|---|---|
| POST | `/api/auth/login` | Público |

### Usuarios
| Método | Endpoint |
|---|---|
| GET | `/api/usuarios` |
| GET | `/api/usuarios/{id}` |
| POST | `/api/usuarios` |
| PUT | `/api/usuarios/{id}` |
| DELETE | `/api/usuarios/{id}` |

### Clientes
| Método | Endpoint |
|---|---|
| GET | `/api/clientes` |
| GET | `/api/clientes/activos` |
| GET | `/api/clientes/{id}` |
| GET | `/api/clientes/buscar?dni=...` |
| POST | `/api/clientes` |
| PUT | `/api/clientes/{id}` |
| DELETE | `/api/clientes/{id}` |
| PATCH | `/api/clientes/{id}/activar` |
| PATCH | `/api/clientes/{id}/desactivar` |

### Productos
| Método | Endpoint |
|---|---|
| GET | `/api/productos` |
| GET | `/api/productos/{id}` |
| GET | `/api/productos/buscar?nombre=...` |
| GET | `/api/productos/categorias` |
| GET | `/api/productos/stock-bajo?umbral=5` |
| POST | `/api/productos` |
| PUT | `/api/productos/{id}` |
| DELETE | `/api/productos/{id}` |

### Proveedores
| Método | Endpoint |
|---|---|
| GET | `/api/proveedores` |
| POST | `/api/proveedores` |
| PUT | `/api/proveedores/{id}` |
| DELETE | `/api/proveedores/{id}` |

### Ventas
| Método | Endpoint |
|---|---|
| GET | `/api/ventas` |
| GET | `/api/ventas/{id}` |
| GET | `/api/ventas/hoy` |
| GET | `/api/ventas/dashboard/resumen` |
| POST | `/api/ventas` |

### Devoluciones
| Método | Endpoint |
|---|---|
| GET | `/api/devoluciones` |
| POST | `/api/devoluciones` |
| GET | `/api/devoluciones/venta/{id}/tiene-devolucion` |

---

## 🔄 Flujo de una Venta (Comunicación entre Servicios)

```
Usuario en Angular
    │
    │  POST /api/ventas  { idCliente, items: [...] }
    │  Header: Authorization: Bearer <token>
    ▼
ms-gateway :8080
    │  → JwtAuthGatewayFilter valida el token JWT
    │  → Inyecta headers X-User-Email, X-User-Role
    │  → Consulta Eureka para localizar ms-ventas
    ▼
ms-ventas :8092
    │
    ├── Feign GET /api/clientes/{id}  →  ms-clientes :8093
    │       Verifica que el cliente existe y está activo
    │
    └── Por cada ítem en el pedido:
            Feign GET /api/productos/{id}  →  ms-productos :8091
            Verifica stock suficiente
            Feign PUT /api/productos/{id}/stock/descontar
            Descuenta la cantidad del inventario
    │
    └── Guarda la Venta + DetallesVenta en la BD MySQL
```

---

## 🗄️ Base de Datos

Todos los microservicios comparten una instancia MySQL alojada en Railway:

| Tabla | Propietario | Descripción |
|---|---|---|
| `usuario` | ms-auth | Usuarios del sistema con roles |
| `cliente` | ms-clientes | Clientes de la ferretería |
| `proveedor` | ms-productos | Proveedores de productos |
| `producto` | ms-productos | Catálogo de productos con stock |
| `venta` | ms-ventas | Cabecera de ventas |
| `detalle_venta` | ms-ventas | Ítems de cada venta |
| `devolucion` | ms-ventas | Registro de devoluciones |

---

## ▶️ Ejecución Local (Opcional)

Si deseas ejecutar el proyecto localmente, necesitas:
- Java 21, Maven, MySQL 8, Node.js 22, Angular CLI

**Orden de arranque obligatorio:**
1. Eureka Server → 2. Config Server → 3. ms-auth → 4. ms-clientes → 5. ms-productos → 6. ms-ventas → 7. ms-gateway → 8. Frontend Angular

Configura un MySQL local con usuario `root`, contraseña `123456` y base de datos `ferreteria`. Restaura los valores por defecto en los YAMLs del config-server antes de ejecutar.

---

*Proyecto desarrollado para el curso de Desarrollo de Aplicaciones Web II — CIBERTEC 2026*
*Autor: Dayron — 6to Ciclo*
