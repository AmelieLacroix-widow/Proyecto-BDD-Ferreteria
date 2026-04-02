# Proyecto-BDD-Ferreteria
Este espacio esta destinado para almacenar y trabajar con un control de versiones en nuestro proyecto de la Ferretería, de no ser parte de nuestra organización/equipo 3 , evite cualquier tipo de interacción


# 🛠️ Sistema de Ferretería

Proyecto desarrollado en Java para la gestión de una ferretería, con arquitectura cliente-servidor mediante red LAN.

---

# 🧱 Estructura del Proyecto

```plaintext
ferreteria-app/
│
├── backend/      → API (Spring Boot)
├── frontend/     → Aplicación de escritorio (JavaFX) [pendiente]
├── database/     → Scripts de base de datos
│
├── .gitignore
└── README.md
```

---

# 📦 Descripción de carpetas

## 🔹 backend/

Contiene el servidor desarrollado con Spring Boot.

Aquí se encuentra:

* Código fuente (`src/`)
* Configuración (`application.properties`)
* Dependencias (`pom.xml`)

👉 Este es el **núcleo del sistema**, encargado de:

* Manejar la lógica de negocio
* Conectarse a la base de datos
* Exponer endpoints (API REST)

---

## 🔹 frontend/

Contendrá la aplicación de escritorio (JavaFX).

👉 Aquí se desarrollará:

* Interfaz gráfica
* Conexión al backend mediante HTTP (LAN)

⚠️ Actualmente vacía (se implementará después)

---

## 🔹 database/

Contiene scripts SQL para la base de datos.

Ejemplo:

* `schema.sql` → creación de tablas
* `data.sql` → datos iniciales

---

# ⚙️ Requisitos del sistema

Todos los integrantes deben instalar:

## ☕ Java

* Versión: **Java 21**

Verificar:

```bash
java -version
```

---

## 🧱 Maven

* Versión recomendada: 3.9.14

Verificar:

```bash
mvn -version
```

---

## 🌐 Git

Para control de versiones.

---

## 🧑‍💻 Editor

* Visual Studio Code (recomendado)
* Extensión: Extension Pack for Java

---

# 🚀 Cómo ejecutar el proyecto

## 1. Clonar repositorio

```bash
git clone https://github.com/AmelieLacroix-widow/Proyecto-BDD-Ferreteria.git
cd Proyecto-BDD-Ferreteria
```

---

## 2. Ejecutar backend

```bash
cd backend
mvn spring-boot:run
```

---

## 🌐 Acceso

Por defecto:

```plaintext
http://localhost:8080
```

---

# 🗄️ Configuración de base de datos

Editar archivo:

```plaintext
backend/src/main/resources/application.properties
```

Ejemplo:

```properties
spring.datasource.url=jdbc:mysql://192.168.1.100:3306/ferreteria
spring.datasource.username=root
spring.datasource.password=1234

spring.jpa.hibernate.ddl-auto=update
```

👉 Cambiar según la configuración local

---

# 🧠 Flujo de trabajo en equipo

Antes de trabajar:

```bash
git pull
```

Después de cambios:

```bash
git add .
git commit -m "Descripción de cambios"
git push
```

---

# 🚫 Archivos que NO se deben subir

Ya están incluidos en `.gitignore`, pero evitar subir:

* `target/`
* `*.class`
* `.env`
* archivos temporales

---

# 🔥 Notas importantes

* El backend corre en una PC servidor
* Las demás PCs se conectan vía LAN
* NO conectar directamente a la base de datos desde el cliente
* Toda comunicación debe pasar por el backend

---

# 📌 Próximos pasos

* Implementar API de productos
* Diseñar base de datos completa
* Desarrollar aplicación de escritorio (frontend)

---

# 👥 Equipo

Proyecto desarrollado en equipo para la gestión de una ferretería.

