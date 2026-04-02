# Backend - Spring Boot

## Estructura de carpetas y tipos de archivos


backend/src/main/java/com/miempresa/ferreteria/
│
├─ controller # Clases que coordinan la lógica y llamadas a servicios
├─ service # Clases con la lógica de negocio
├─ repository # Interfaces para acceso a la base de datos (JpaRepository)
└─ model # Clases de entidades JPA (@Entity) que representan tablas

backend/src/test/java/com/miempresa/ferreteria/
├─ controller # Tests para los controladores
├─ service # Tests para los servicios
└─ repository # Tests para los repositorios


### Qué va en cada carpeta

| Carpeta | Contenido | Ejemplos de clases |
|---------|-----------|------------------|
| `model` | Entidades que representan tablas de la base de datos (`@Entity`) | `Producto.java`, `Cliente.java` |
| `repository` | Interfaces que extienden `JpaRepository` para operaciones CRUD | `ProductoRepository.java`, `ClienteRepository.java` |
| `service` | Lógica de negocio, validaciones y coordinación de repositorios | `ProductoService.java`, `ClienteService.java` |
| `controller` | Coordinación de operaciones o endpoints (aunque no haya interfaz web aún) | `ProductoController.java` |
| `test` | Pruebas unitarias o de integración para cada tipo de clase | `ProductoServiceTest.java` |

---

## Definición de paquetes

- El **package siempre coincide con la ruta de carpetas dentro de `src/main/java` o `src/test/java`**.  
- Ejemplo: archivo `ProductoService.java` en `backend/src/main/java/com/miempresa/ferreteria/service`:

```java
package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Producto;
import com.miempresa.ferreteria.repository.ProductoRepository;

public class ProductoService {
    // código...
}
Todos los archivos dentro de la misma carpeta pertenecen al mismo paquete.
Para usar clases de otro paquete, se hace un import explícito:
import com.miempresa.ferreteria.model.Producto; // Trae solo esa clase
import com.miempresa.ferreteria.repository.*;   // Trae todas las clases del paquete repository