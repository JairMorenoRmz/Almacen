# Almacen
 Sistema de Inventario

1.- Resumen Ejecutivo.
a.- El sistema de inventarios en Almacén es una aplicación diseñada para gestionar eficientemente el inventario de productos en un almacén. La aplicación permite registrar las entradas y salidas de productos, gestionar usuarios con diferentes roles y proporcionar una interfaz gráfica amigable para facilitar estas operaciones.

b.- Problema identificado.
La gestión manual del inventario en un almacén puede ser propensa a errores y consume mucho tiempo. Además, la falta de control sobre quién puede modificar el inventario puede llevar a problemas de seguridad y pérdidas.

c.- Solución.
La aplicación proporciona una solución integral que automatiza la gestión del inventario, permitiendo registrar y consultar entradas y salidas de productos de manera eficiente. También incorpora roles de usuario para asegurar que solo usuarios autorizados puedan realizar ciertas operaciones.

d.- Arquitectura.
La aplicación está construida usando Java y SQLite. Utiliza una arquitectura cliente-servidor simple donde el cliente es una aplicación de escritorio que se conecta a una base de datos SQLite. La interfaz gráfica está desarrollada utilizando Swing.

- Tabla de Contenidos. (ToC)
1. Resumen Ejecutivo (1-resumen-ejecutivo)
2. Requerimientos (2-requerimientos)
3. Instalación (3-instalación)
4. Configuración (4-configuración)
5. Uso (5-uso)
6. Contribución (6-contribución)
7. Roadmap (7-roadmap)

2.- Requerimientos

a.- 
- No se requiere un servidor de aplicaciones. La aplicación es una aplicación de escritorio autónoma.
- Base de datos: SQLite.

b.- Paquetes Adicionales
- HikariCP (https://github.com/brettwooldridge/HikariCP) para gestión de conexiones.
- JUnit (https://junit.org/junit5/) para pruebas unitarias.

c.- Versión de Java
Java 11 o superior.

3.- Instalación - Configuración
Clonar el repositorio:
'''bash
git clone https://github.com/JairMorenoRmz/Almacen/
cd almacen
- Configurar el entorno de desarrollo en NetBeans o cualquier otro IDE compatible con Java.
- Asegurarse de tener SQLite y los paquetes adicionales (HikariCP, JUnit) en el classpath.
- ¿Cómo ejecutar pruebas manualmente?
- Ejecutar la aplicación desde el IDE.
- Iniciar sesión con las credenciales de administrador (admin/admin123)
- Probar las funcionalidades de gestión de usuarios, entrada, salidas, y visualización del inventario.

5.- Uso
a.- Sección de referencia de Usuario Final.
- Iniciar sesión: Introducir el nombre de usuario y contraseña.
- Registrar Entrada de producto: Navegar a "Entrada de producto", introducir lo que se necesita y registrar.
- Registrar salida de producto: Navegar a "Salida de producto", colocar lo que se necesita y resitrarlo.
- Consultar inventario: Ir a la sección "Mostrar Inventario", para ver el estado actual del inventario.

b.- Sección de referencia para usuario administrador
- Gestión de Usuarios: Crear y eliminar usuarios desde el menú de opciones.
- Gestión de inventario: Realizar entradas y salidas de productos, consultar el inventario y eliminar registros antiguos.

6.- Guía de Contribución para Usuarios.
a.- Clonar el repositorio:
git clone https://github.com/JairMorenoRmz/Almacen/
cd almacen
git checkout -b ....
git add .
git commit -m " ... "
git push origin ....

7.- Roadmap
Requerimientos que se implementarán en un futuro:
a.- Integración con servicios web - Permitir la integración con servicios web externos para sincronizcación de inventarios.
b.- Interfaz web - Desarrollar una interfaz web para permitir acceso remoto al sistema.

