# SpeedFast Maven

Aplicación de escritorio Java Swing para administrar repartidores, pedidos y entregas con MySQL y JDBC.

La instrucción que menciona `ClienteDAO` no coincide con el caso ni con el esquema entregado, que definen `repartidores` y no tienen tabla de clientes. Por eso, el CRUD de esa entidad se implementa en `RepartidorDAO`.

## Requisitos

- JDK 17 o superior.
- Maven (o IntelliJ IDEA con soporte Maven).
- MySQL Server y MySQL Workbench opcional.

El conector MySQL JDBC está declarado en `pom.xml`; Maven/IntelliJ lo descarga como dependencia.

## 1. Crear la base de datos

1. Inicia MySQL Server.
2. Abre MySQL Workbench y conéctate a tu servidor.
3. Abre `src/main/resources/db/schema.sql`.
4. Ejecuta todo el script. Este crea `speedfast_db` y las tablas `repartidores`, `pedidos` y `entregas`.

Las claves foráneas impiden eliminar un pedido o repartidor que tenga una entrega asociada. Elimina primero esa entrega desde la pestaña **Entregas**.

## 2. Configurar el acceso a MySQL

La aplicación lee primero propiedades Java y luego variables de entorno:

| Dato | Propiedad Java | Variable de entorno | Valor predeterminado |
| --- | --- | --- | --- |
| URL | `speedfast.db.url` | `SPEEDFAST_DB_URL` | `jdbc:mysql://localhost:3306/speedfast_db?...` |
| Usuario | `speedfast.db.user` | `SPEEDFAST_DB_USER` | `root` |
| Contraseña | `speedfast.db.password` | `SPEEDFAST_DB_PASSWORD` | `ADMIN` |

El proyecto tiene configurada `ADMIN` como contraseña predeterminada. Si la contraseña de tu MySQL es distinta, configúrala antes de iniciar la aplicación. En PowerShell, para la sesión actual:

```powershell
$env:SPEEDFAST_DB_USER = "root"
$env:SPEEDFAST_DB_PASSWORD = "tu_contraseña"
```

También puedes definir esas variables en la configuración de ejecución de IntelliJ (**Run > Edit Configurations > Environment variables**). Si MySQL usa otro puerto o servidor, cambia `SPEEDFAST_DB_URL`.

## 3. Abrir y ejecutar en IntelliJ IDEA

1. Abre la carpeta del proyecto y espera la importación de Maven.
2. Comprueba que el SDK del proyecto sea JDK 17 o superior.
3. Abre **Maven > Reload All Maven Projects** si el conector JDBC todavía no aparece en las dependencias.
4. Configura las variables de conexión si tu servidor MySQL usa credenciales distintas a las predeterminadas.
5. Ejecuta `src/main/java/app/Main.java` o la clase `vista.VentanaPrincipal`.

También puedes iniciar desde una terminal en la raíz del proyecto, tras configurar las variables de entorno:

```powershell
mvn compile
mvn exec:java -Dexec.mainClass="app.Main"
```

## 4. Uso de la aplicación

1. En **Repartidores**, registra el nombre. Selecciona una fila para editarla o eliminarla.
2. En **Pedidos**, ingresa dirección, tipo y estado. Puedes filtrar por estado, tipo o ambos.
3. En **Entregas**, selecciona el pedido y el repartidor desde los combos, ingresa fecha (`AAAA-MM-DD`) y hora (`HH:mm`), y registra. Los filtros permiten listar entregas por pedido o repartidor.
4. Para modificar o eliminar cualquier registro, selecciónalo primero en su tabla.
5. Elimina las entregas antes de intentar eliminar pedidos o repartidores que estén asociados.

## Capas principales

- `modelo`: entidades y tipos concretos de pedido.
- `DAO`: CRUD JDBC con `PreparedStatement`, `ResultSet` y cierre automático de recursos.
- `Conexiones/ConexionBD.java`: configuración centralizada de la conexión.
- `vista`: ventanas y paneles Swing con validación, tablas y mensajes.
