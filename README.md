# Kaiju Inventario - Backend Spring Boot

## Descripción

Backend desarrollado para la aplicación móvil Kaiju Inventario.

Permite administrar productos, controlar el stock disponible, registrar movimientos de inventario y gestionar el registro e inicio de sesión de usuarios mediante servicios REST.

---

## Integrantes

- Jonathan Pozo
- Sebastian Fica

---

## Tecnologías Utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- H2 Database
- Spring Security Crypto (BCrypt)
- Maven

---

## Arquitectura

Controller
↓
Service
↓
Repository
↓
Base de Datos

---

## Entidades

### Producto

Atributos:

- codigo
- nombre
- categoria
- stock
- stockMinimo
- creadoPor (nombre de quién lo agregó; lo define el servidor)
- creadoPorEmail

### MovimientoInventario

Atributos:

- id
- tipoMovimiento
- cantidad
- fecha
- codigoProducto
- registradoPor (nombre de quién lo registró; lo define el servidor)

### UsuarioKaiju

Tabla: `usuario_kaiju`

Atributos:

- id
- rut (único)
- nombreCompleto
- email (único)
- passwordHash (contraseña cifrada con BCrypt)
- rol (BODEGA o ADMIN)

### ConfiguracionKaiju

Tabla: `configuracion_kaiju` (una sola fila, vale para todos los usuarios)

Atributos:

- id
- modoEspecial

### UsuarioKaiju

Tabla: `usuario_kaiju`

Atributos:

- id
- rut (único)
- nombreCompleto
- email (único)
- passwordHash (contraseña cifrada con BCrypt)
- rol (BODEGA, VENTAS o ADMIN)

---

## Funcionalidades

### Gestión de Productos

- Crear productos
- Consultar productos
- Actualizar productos
- Eliminar productos

### Gestión de Movimientos

- Registrar entradas de inventario
- Registrar salidas de inventario
- Actualizar automáticamente el stock

### Gestión de Usuarios

- Registrar usuarios
- Iniciar sesión y entregar un token JWT
- Consultar los usuarios registrados (Admin)
- Eliminar cuentas (Admin)

### Configuración global

- Consultar y cambiar el Modo especial. El cambio lo hace un Admin y vale para todos los usuarios.

### Reglas de Negocio

- El stock aumenta mediante entradas.
- El stock disminuye mediante salidas.
- No se permite stock negativo.
- Validación de existencia de producto.
- El RUT se valida con su dígito verificador y se guarda con formato `12345678-5`.
- No se permiten RUT ni correos repetidos.
- La contraseña debe tener al menos 6 caracteres y se guarda cifrada (BCrypt), nunca en texto plano.
- El rol debe ser BODEGA o ADMIN. Si no se indica, se asigna BODEGA.
- Solo puede existir un ADMIN. Si se intenta registrar otro, responde 409 "Ya hay un administrador registrado".
- El ADMIN puede eliminar su propia cuenta. Después se puede registrar una cuenta nueva con rol ADMIN.
- Los roles no se pueden cambiar.
- El servidor registra automáticamente quién agregó cada producto y quién registró cada movimiento, usando la sesión (no se acepta ese dato desde el cliente).
- Un producto con un código que ya existe no se puede volver a crear (error 409).
- El correo se guarda en minúsculas.
- Si el correo o la contraseña fallan al iniciar sesión, se responde el mismo mensaje para no revelar cuál de los dos es incorrecto.

---

## Permisos por rol

Todas las rutas, salvo registro e inicio de sesión, exigen el encabezado `Authorization: Bearer <accessToken>`.

| Acción | Admin | Bodega |
|--------|:-----:|:------:|
| Ver productos (GET /productos) | Sí | Sí |
| Agregar productos (POST /productos) | Sí | Sí |
| Editar productos (PUT /productos/{codigo}) | Sí | Sí |
| Eliminar productos (DELETE /productos/{codigo}) | Sí | Sí |
| Ver y registrar movimientos (/movimientos) | Sí | Sí |
| Ver usuarios y eliminar cuentas | Sí | No |
| Ver la configuración | Sí | Sí |
| Cambiar la configuración | Sí | No |

El rol se consulta en la base de datos en cada llamada, por lo que si una cuenta se elimina, el cambio rige de inmediato aunque su token no haya vencido.

---

## Endpoints

### Productos

#### Obtener todos

```http
GET /productos
```

#### Obtener por código

```http
GET /productos/{codigo}
```

#### Crear producto

```http
POST /productos
```

#### Actualizar producto

```http
PUT /productos/{codigo}
```

#### Eliminar producto

```http
DELETE /productos/{codigo}
```

---

### Movimientos

#### Obtener movimientos

```http
GET /movimientos
```

#### Registrar movimiento

```http
POST /movimientos
```

---

### Autenticación

#### Registrar usuario

```http
POST /api/v1/kaiju/auth/registro
```

Cuerpo (JSON):

```json
{
  "nombreCompleto": "Juan Perez",
  "rut": "12.345.678-5",
  "email": "juan@correo.cl",
  "password": "123456",
  "rol": "BODEGA"
}
```

Respuesta: `201 Created` con los datos del usuario (sin contraseña). Si se envía `"rol": "ADMIN"` y ya existe un administrador, responde `409 Conflict`.

#### Iniciar sesión

```http
POST /api/v1/kaiju/auth/login
```

Cuerpo (JSON):

```json
{
  "email": "juan@correo.cl",
  "password": "123456"
}
```

Respuesta: `200 OK` con `accessToken` (JWT), `tokenType` (`Bearer`), `expiresIn` (segundos) y los datos del `usuario`.

#### Listar usuarios registrados (Admin)

```http
GET /api/v1/kaiju/auth/usuarios
```

Respuesta: `200 OK` con la lista de usuarios (sin contraseñas).

#### Eliminar usuario (Admin)

```http
DELETE /api/v1/kaiju/auth/usuarios/{id}
```

Respuesta: `204 No Content`. El Admin también puede eliminar su propia cuenta.

---

### Configuración

#### Obtener configuración

```http
GET /api/v1/kaiju/configuracion
```

Respuesta: `{ "modoEspecial": false }`

#### Cambiar configuración (Admin)

```http
PUT /api/v1/kaiju/configuracion
```

Cuerpo (JSON):

```json
{
  "modoEspecial": true
}
```

El cambio se aplica a todos los usuarios.

#### Códigos de error

Los errores responden con el formato `{"message": "..."}`:

| Código | Cuándo ocurre |
|--------|---------------|
| 400 | Datos inválidos (RUT, correo, contraseña corta, rol inexistente) |
| 401 | Correo o contraseña incorrectos, o sesión no válida o vencida |
| 403 | El rol del usuario no tiene permiso para la acción |
| 404 | Usuario no encontrado |
| 409 | RUT o correo ya registrados, ya existe un administrador, o código de producto repetido |

---

## Pruebas Unitarias

Implementadas:

- MovimientoInventarioServiceTest
- ProductoEntradaTest
- ProductoSalidaTest
- ProductoNoExisteTest
- KaijuinventarioApplicationTests

Validaciones probadas:

- Stock insuficiente.
- Entrada de inventario.
- Salida de inventario.
- Producto inexistente.
- Inicio correcto del contexto Spring Boot.

---

## Base de Datos

Motor utilizado:

```text
H2 Database
```

Los datos se guardan en un archivo local (`./data/kaijudb`), por lo que productos, movimientos y usuarios se conservan al reiniciar el servidor.

Consola web:

```text
http://localhost:8080/h2-console
```

Datos de conexión:

- JDBC URL: `jdbc:h2:file:./data/kaijudb`
- Usuario: `sa`
- Contraseña: (vacía)

Consulta para ver los usuarios registrados (también disponible en `GET /api/v1/kaiju/auth/usuarios` con una sesión de Admin):

```sql
SELECT * FROM usuario_kaiju;
```

---

## Configuración

En `src/main/resources/application.properties`:

- `kaiju.jwt.secret`: clave para firmar el token JWT (mínimo 32 caracteres). Puede definirse con la variable de entorno `KAIJU_JWT_SECRET`.
- `kaiju.jwt.expires-in`: duración del token en segundos (por defecto 3600).

---

## Ejecución

### Ejecutar aplicación

```bash
mvn spring-boot:run
```

### Puerto

```text
http://localhost:8080
```

Desde el emulador de Android la misma dirección es `http://10.0.2.2:8080`.

---

## Integración

Este backend se encuentra conectado a una aplicación Android desarrollada en Kotlin utilizando Retrofit para la comunicación mediante servicios REST, tanto para el inventario como para el registro e inicio de sesión de usuarios.
