# 🔧 Ferretería Molina — Sistema de Gestión Integral

> **Proyecto Integrador | CIBERTEC 2026**
> Desarrollo de Aplicaciones Web II — 6to Ciclo
> Arquitectura de Microservicios con Spring Cloud, Angular y MySQL

---

## 📐 Arquitectura del Sistema

```
                                ┌──────────────────────────────────┐
                                │     EUREKA SERVER :8761          │
                                │  (Service Discovery & Registry)  │
                                └───────────────┬──────────────────┘
                                                │  todos se registran
                         ┌──────────────────────┼──────────────────────┐
                         │                      │                      │
                ┌────────▼────────┐    ┌────────▼────────┐    ┌────────▼────────┐
                │ CONFIG SERVER   │    │   ms-auth       │    │  ms-clientes    │
                │     :8888       │    │     :8090       │    │     :8093       │
                │ (Config Central)│    │ (Login + JWT)   │    │ (CRUD Clientes) │
                └─────────────────┘    └─────────────────┘    └─────────────────┘
                         │                       │                      │
                ┌────────▼────────┐     ┌────────▼────────┐             │
                │  ms-productos   │     │   ms-ventas     │─── Feign ───┘
                │     :8091       │     │     :8092       │
                │ (Productos +    │◄────│ (Ventas +       │
                │  Proveedores)   │Feign│  Devoluciones)  │
                └─────────────────┘     └─────────────────┘
                         │                      │
                         └──────────┬───────────┘
                           ┌────────▼────────┐
                           │  MS-GATEWAY     │
                           │     :8080       │
                           │ (API Gateway +  │
                           │  JWT Filter)    │
                           └────────┬────────┘
                                    │
                           ┌────────▼────────┐
                           │   ANGULAR       │
                           │   FRONTEND      │
                           │     :4200       │
                           └─────────────────┘
```

### Flujo de comunicación

1. El **frontend Angular** envía todas las peticiones al **API Gateway** (`:8080`).
2. El **Gateway** valida el token JWT y enruta la petición al microservicio correspondiente usando **Eureka** para descubrir la instancia.
3. Cada microservicio obtiene su configuración del **Config Server** al arrancar.
4. **ms-ventas** se comunica con **ms-clientes** y **ms-productos** mediante **OpenFeign** para validar datos y descontar stock.

---

## 🗂️ Estructura del Proyecto

```
ProyectoFerreteria_DAWII/
│
├── pom.xml                        ← Parent POM (multi-módulo Maven)
├── DB_FERRETERIA.sql              ← Script SQL para crear la BD y datos iniciales
├── iniciar-microservicios.ps1     ← Script PowerShell para iniciar todo
├── docker-compose.yml             ← Despliegue con Docker (opcional)
│
├── eureka-server/                 ← Service Discovery (Netflix Eureka)     :8761
│
├── config-server/                 ← Configuración centralizada             :8888
│   └── src/main/resources/
│       ├── application.yml        ← Config del propio config-server
│       └── configs/               ← Archivos de configuración por servicio
│           ├── application.yml    ← Config compartida (Eureka URL)
│           ├── ms-auth.yml        ← Config de ms-auth
│           ├── ms-clientes.yml    ← Config de ms-clientes
│           ├── ms-productos.yml   ← Config de ms-productos
│           ├── ms-ventas.yml      ← Config de ms-ventas
│           └── ms-gateway.yml     ← Config de ms-gateway (rutas, CORS, JWT)
│
├── ms-auth/                       ← Autenticación y gestión de usuarios    :8090
│   └── controller/
│       ├── AuthController         ← POST /api/auth/login
│       └── UsuarioController      ← CRUD /api/usuarios
│
├── ms-clientes/                   ← Gestión de clientes                    :8093
│   └── controller/
│       └── ClienteController      ← CRUD /api/clientes
│
├── ms-productos/                  ← Productos y proveedores                :8091
│   └── controller/
│       ├── ProductoController     ← CRUD /api/productos + stock
│       └── ProveedorController    ← CRUD /api/proveedores
│
├── ms-ventas/                     ← Ventas y devoluciones                  :8092
│   ├── controller/
│   │   ├── VentaController        ← CRUD /api/ventas + dashboard
│   │   └── DevolucionController   ← /api/devoluciones
│   └── client/                    ← OpenFeign clients
│       ├── ClienteClient.java     ← Feign → ms-clientes
│       └── ProductoClient.java    ← Feign → ms-productos
│
├── ms-gateway/                    ← API Gateway (punto de entrada único)   :8080
│   └── filter/
│       └── JwtAuthGatewayFilter   ← Filtro global JWT
│
└── frontend-angular/              ← Frontend Angular 19                    :4200
```

---

## 🚀 Tecnologías

### Backend — Microservicios

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 LTS | Lenguaje base |
| Spring Boot | 3.2.5 | Framework principal |
| Spring Cloud | 2023.0.1 | Ecosistema de microservicios |
| Spring Cloud Config | 4.1.x | Configuración centralizada |
| Spring Cloud Gateway | 4.1.x | API Gateway reactivo |
| Netflix Eureka | 4.1.x | Service Discovery |
| OpenFeign | 4.1.x | Comunicación síncrona entre servicios |
| Spring Security | 6.x | Seguridad |
| Spring Data JPA | 3.2.x | Persistencia ORM |
| jjwt (io.jsonwebtoken) | 0.12.6 | Generación y validación de tokens JWT |
| Lombok | 1.18.x | Reducción de código boilerplate |
| MySQL | 8.x | Base de datos relacional |
| Maven | 3.9.x | Gestión de dependencias y build |

### Frontend

| Tecnología | Versión | Uso |
|---|---|---|
| Angular | 19 | Framework SPA |
| TypeScript | 5.x | Lenguaje tipado |
| Node.js | 18+ | Runtime para Angular CLI |

### Infraestructura (opcional)

| Tecnología | Uso |
|---|---|
| Docker Desktop | Contenerización de todos los servicios |
| Docker Compose | Orquestación multi-contenedor |

---

## ▶️ Cómo ejecutar el proyecto (Manual)

### Prerrequisitos

| Requisito | Detalle |
|---|---|
| Java 17 | Instalado en `C:\Program Files\Java\jdk-17` |
| Maven | Instalado y disponible en el PATH del sistema |
| MySQL 8 | Corriendo en `localhost:3306` con usuario `root` y contraseña `123456` |
| Node.js 18+ | Para ejecutar el frontend Angular |
| Angular CLI | `npm install -g @angular/cli` |

### Paso 1 — Crear la base de datos

Abrir MySQL Workbench o una terminal MySQL y ejecutar:

```sql
-- Opción A: Desde MySQL Workbench
-- Abrir el archivo DB_FERRETERIA.sql y ejecutarlo

-- Opción B: Desde la terminal
mysql -u root -p123456 < DB_FERRETERIA.sql
```

Esto crea la base de datos `ferreteria` con las tablas: `usuario`, `cliente`, `proveedor`, `producto`, `venta`, `detalle_venta`, `devolucion` y datos iniciales de ejemplo (5 proveedores, 15 productos, 5 clientes, 3 ventas).

### Paso 2 — Configurar Java 17

Si tienes varias versiones de Java instaladas, en cada terminal de PowerShell donde vayas a ejecutar un microservicio debes forzar Java 17:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
```

Verificar con:
```powershell
java -version
# Debe mostrar: openjdk version "17.x.x"
```

### Paso 3 — Iniciar los microservicios (en orden)

> ⚠️ **El orden es OBLIGATORIO.** Eureka debe arrancar primero, Config Server segundo, y el Gateway al final.

Abrir **7 terminales** de PowerShell (una por cada microservicio) y ejecutar en este orden:

**Terminal 1 — Eureka Server (esperar ~25 segundos a que arranque)**
```powershell
cd eureka-server
mvn spring-boot:run
```

**Terminal 2 — Config Server (esperar ~15 segundos)**
```powershell
cd config-server
mvn spring-boot:run
```

**Terminal 3 — ms-auth**
```powershell
cd ms-auth
mvn spring-boot:run
```

**Terminal 4 — ms-clientes**
```powershell
cd ms-clientes
mvn spring-boot:run
```

**Terminal 5 — ms-productos**
```powershell
cd ms-productos
mvn spring-boot:run
```

**Terminal 6 — ms-ventas**
```powershell
cd ms-ventas
mvn spring-boot:run
```

**Terminal 7 — ms-gateway (siempre al final)**
```powershell
cd ms-gateway
mvn spring-boot:run
```

### Paso 4 — Iniciar el Frontend Angular

**Terminal 8 — Frontend Angular**
```powershell
cd frontend-angular
npm install
ng serve
```

### Alternativa: Script automático

En lugar de abrir 7 terminales manualmente, puedes ejecutar:

```powershell
.\iniciar-microservicios.ps1
```

Este script abre automáticamente una ventana de PowerShell por cada microservicio en el orden correcto, con las pausas necesarias entre cada uno.

### Paso 5 — Verificar que todo funciona

| URL | Descripción |
|---|---|
| http://localhost:8761 | 📋 Eureka Dashboard — debes ver los 5 servicios registrados |
| http://localhost:8888/ms-auth/default | ⚙️ Config Server — muestra la configuración de ms-auth |
| http://localhost:4200 | 🌐 Frontend Angular — interfaz del sistema |
| http://localhost:8080/api/auth/login | 🚪 API Gateway — endpoint público de login |

---

## 🔐 Credenciales por defecto

> Los usuarios se crean automáticamente al ejecutar la aplicación (JPA `ddl-auto: update`).
> Las credenciales iniciales están en la carga de datos del sistema.

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@ferreteria.com | admin123 |
| Empleado | empleado@ferreteria.com | empleado123 |

---

## 📡 Endpoints del API (vía Gateway :8080)

Todas las peticiones se realizan a través del API Gateway (`http://localhost:8080`).
El endpoint de login es público; todos los demás requieren un token JWT en el header `Authorization: Bearer <token>`.

### Autenticación (público)
| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/auth/login` | Iniciar sesión, devuelve token JWT |

### Usuarios (requiere token)
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/usuarios` | Listar todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtener usuario por ID |
| POST | `/api/usuarios` | Crear un nuevo usuario |
| PUT | `/api/usuarios/{id}` | Actualizar un usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar un usuario |

### Clientes (requiere token)
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/clientes` | Listar todos los clientes |
| GET | `/api/clientes/activos` | Listar solo clientes activos |
| GET | `/api/clientes/{id}` | Obtener cliente por ID |
| GET | `/api/clientes/buscar?dni=...` | Buscar cliente por DNI |
| POST | `/api/clientes` | Registrar un nuevo cliente |
| PUT | `/api/clientes/{id}` | Actualizar un cliente |
| DELETE | `/api/clientes/{id}` | Eliminar un cliente |
| PATCH | `/api/clientes/{id}/activar` | Activar un cliente |
| PATCH | `/api/clientes/{id}/desactivar` | Desactivar un cliente |

### Productos (requiere token)
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/productos` | Listar todos los productos |
| GET | `/api/productos/{id}` | Obtener producto por ID |
| GET | `/api/productos/buscar?nombre=...` | Buscar por nombre |
| GET | `/api/productos/categorias` | Listar categorías |
| GET | `/api/productos/stock-bajo?umbral=5` | Productos con stock bajo |
| POST | `/api/productos` | Crear un nuevo producto |
| PUT | `/api/productos/{id}` | Actualizar un producto |
| DELETE | `/api/productos/{id}` | Eliminar un producto |

### Proveedores (requiere token)
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/proveedores` | Listar todos los proveedores |
| GET | `/api/proveedores/{id}` | Obtener proveedor por ID |
| POST | `/api/proveedores` | Crear un nuevo proveedor |
| PUT | `/api/proveedores/{id}` | Actualizar un proveedor |
| DELETE | `/api/proveedores/{id}` | Eliminar un proveedor |

### Ventas (requiere token)
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/ventas` | Listar todas las ventas |
| GET | `/api/ventas/{id}` | Obtener venta por ID |
| GET | `/api/ventas/{id}/detalles` | Ver detalles de una venta |
| GET | `/api/ventas/hoy` | Ventas del día |
| GET | `/api/ventas/dashboard/resumen` | Resumen para dashboard |
| POST | `/api/ventas` | Registrar una venta (descuenta stock vía Feign) |

### Devoluciones (requiere token)
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/devoluciones` | Listar todas las devoluciones |
| POST | `/api/devoluciones` | Registrar una devolución (restaura stock vía Feign) |
| GET | `/api/devoluciones/venta/{id}/tiene-devolucion` | Verificar si una venta tiene devolución |

---

## 🔄 Flujo de comunicación entre servicios (Ejemplo: Registrar Venta)

```
Cliente (Angular :4200)
    │
    │  POST /api/ventas  { idCliente, items: [...] }
    │  Header: Authorization: Bearer <token>
    ▼
ms-gateway :8080
    │  JwtAuthGatewayFilter → valida token JWT
    │  Extrae X-User-Email, X-User-Role
    │  Enruta a ms-ventas (descubierto vía Eureka)
    ▼
ms-ventas :8092
    │
    ├── Feign → ms-clientes :8093
    │       GET /api/clientes/{id}
    │       → Verifica que el cliente existe y está activo
    │
    └── Feign → ms-productos :8091 (por cada ítem)
            GET /api/productos/{id}
            → Verifica existencia y stock suficiente
            PUT /api/productos/{id}/stock/descontar
            → Descuenta la cantidad vendida del inventario
```

---

## ⚙️ Configuración centralizada (Config Server)

El **Config Server** centraliza la configuración de todos los microservicios. Funciona así:

1. Cada microservicio tiene un `application.yml` mínimo con solo su nombre y la URL del Config Server.
2. Al arrancar, solicita su configuración al Config Server (puerto `8888`).
3. El Config Server sirve los archivos YAML desde `classpath:/configs/` usando perfil `native` (archivos locales).

**Ejemplo de application.yml de un microservicio:**
```yaml
spring:
  application:
    name: ms-clientes
  config:
    import: "optional:configserver:http://localhost:8888"
```

> El prefijo `optional:` permite que el microservicio arranque aun si el Config Server no está disponible.

---

## 📦 Compilar todo desde la raíz

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
mvn clean compile
```

Esto compila los 7 módulos: `eureka-server`, `config-server`, `ms-auth`, `ms-clientes`, `ms-productos`, `ms-ventas` y `ms-gateway`.

---

## 🗄️ Base de datos

Todos los microservicios comparten una misma base de datos MySQL llamada `ferreteria`. Las tablas son:

| Tabla | Microservicio | Descripción |
|---|---|---|
| `usuario` | ms-auth | Usuarios del sistema (admin, empleado) |
| `cliente` | ms-clientes | Clientes de la ferretería |
| `proveedor` | ms-productos | Proveedores de productos |
| `producto` | ms-productos | Catálogo de productos con stock |
| `venta` | ms-ventas | Registro de ventas |
| `detalle_venta` | ms-ventas | Ítems de cada venta |
| `devolucion` | ms-ventas | Devoluciones de ventas |

---

## 🖥️ Mensajes en terminal

Al realizar operaciones REST, cada microservicio imprime un mensaje de confirmación en su terminal:

```
========== [MS-CLIENTES] ==========
Consultando y listando todos los clientes.
===================================
```

Esto permite verificar visualmente que las peticiones están llegando al microservicio correcto.

---

*Proyecto desarrollado para el curso de Desarrollo de Aplicaciones Web II — CIBERTEC 2026*
