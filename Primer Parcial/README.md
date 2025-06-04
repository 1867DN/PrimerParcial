# Primer Parcial - Aplicación Java con JDBC

**Trabajo Práctico / Primer Parcial**

**Nombre:** Leandro Nuñez 
**Legajo:** 52720 
**Materia:** Programación 2 

---

## Descripción

Aplicación de consola en Java que permite gestionar Clientes y Pedidos en una base de datos relacional (H2, modo archivo).  
Incluye operaciones CRUD completas, relación entre entidades, validaciones, y uso del patrón DAO para el acceso a datos.  
El sistema implementa logging con Log4j2 y se ejecuta con Gradle.

---

## Estructura del Proyecto
Primer Parcial/
├── README.md
├── settings.gradle
├── .gitignore
├── gradle/
├── app/
│ ├── build.gradle
│ └── src/
│ └── main/
│ ├── java/
│ │ └── PrimerParcial/
│ │ ├── App.java
│ │ ├── model/
│ │ ├── dao/
│ │ └── util/
│ └── resources/

---

- **model/**: Clases de dominio (Cliente, Pedido)
- **dao/**: Interfaces y DAOs (patrón DAO)
- **util/**: Utilidades de conexión (ConnectionManager)
- **App.java**: Clase principal (menú y lógica general)

---

## Requisitos

- **Java 22** (o versión compatible)
- **Gradle** (ya configurado en el proyecto)
- No requiere instalar ni configurar la base de datos, ya que se crea automáticamente con H2 al ejecutar el programa.

---

## Cómo Compilar y Ejecutar

1. **Abrir terminal en la carpeta principal del proyecto**  
   (donde está este README.md y la carpeta `app/`).

2. **Compilar y ejecutar con Gradle:**
   cd app
   gradle build
   gradle run

- O bien, desde la raíz usando el wrapper:
"./gradlew :app:run"

3. El sistema mostrará el menú de opciones por consola.

---

## Uso del Sistema
- Navegá el menú con las opciones numéricas (1-8, 0 para salir).
- En cualquier entrada de dato, podés cancelar la operación y volver al menú principal escribiendo: "0000"
- El sistema valida todas las entradas y evita errores por valores incorrectos.
- Los clientes y pedidos quedan guardados en la base hasta que borres el archivo de base de datos.

---

## Notas sobre la Base de Datos
- La base de datos H2 se almacena como archivo en la carpeta /database o /app (según tu configuración). 
- Para reiniciar el sistema desde cero, borrá el archivo .mv.db correspondiente.
- El ID de clientes/pedidos no se resetea automáticamente si solo borrás registros; se resetea al borrar el archivo de base.

---

## Logging
- El sistema utiliza Log4j2 para registrar información, advertencias y errores importantes.
- Los logs quedan registrados en consola y en archivos de log según la configuración (log4j2.xml en /resources).

---

## Patrón DAO y Organización
- Se implementó el patrón DAO para separar la lógica de acceso a datos.
- Cada entidad tiene su interfaz DAO (ClienteDAO, PedidoDAO) y su implementación (ClienteDAOImpl, PedidoDAOImpl).

## Uso de genéricos:
Se implementó una interfaz genérica GenericDAO<T> que permite reutilizar la lógica de operaciones CRUD para cualquier entidad de la aplicación.