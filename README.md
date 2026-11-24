# Kaiju Inventario - Backend Spring Boot

## Descripción

Backend desarrollado para la aplicación móvil Kaiju Inventario.

Permite administrar productos, controlar el stock disponible y registrar movimientos de inventario mediante servicios REST.

---

## Integrantes

- Jonathan Pozo
- Sebastian Fica

---

## Tecnologías Utilizadas

- Java
- Spring Boot
- Spring Data JPA
- H2 Database
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

### MovimientoInventario

Atributos:

- id
- tipoMovimiento
- cantidad
- fecha
- codigoProducto

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

### Reglas de Negocio

- El stock aumenta mediante entradas.
- El stock disminuye mediante salidas.
- No se permite stock negativo.
- Validación de existencia de producto.

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

Configuración:

```text
http://localhost:8080/h2-console
```

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

---

## Integración

Este backend se encuentra conectado a una aplicación Android desarrollada en Kotlin utilizando Retrofit para la comunicación mediante servicios REST.
