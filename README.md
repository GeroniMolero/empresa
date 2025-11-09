# Sistema de Gestión de Empleados y Nóminas

Aplicación web Java EE para la gestión de empleados y cálculo de nóminas, implementando patrones de diseño profesionales y arquitectura por capas.

---

## Descripción del Proyecto

Sistema web que permite:
- **Gestión de empleados**: Listar, buscar, editar y visualizar información de empleados
- **Gestión de nóminas**: Consultar salarios individuales y listar todas las nóminas
- **Cálculo automático**: El sistema calcula salarios según categoría y años trabajados, o recupera el valor almacenado en base de datos

---

## Stack Tecnológico

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 8+ | Lenguaje principal |
| **Java Servlet** | 3.1+ | Controladores web |
| **JSP + JSTL** | 2.3 / 1.2 | Presentación |
| **Maven** | 3.x | Gestión de dependencias y build |
| **MariaDB / MySQL** | MariaDB 10.5+ / MySQL 5.7+ | Base de datos |
| **Apache DBCP2** | 2.9.0 | Pool de conexiones |
| **Apache Tomcat** | 9.x | Servidor de aplicaciones |

---

## Arquitectura del Sistema

### Estructura por Capas (4 capas con Service Layer integrado)

```
┌─────────────────────────────────────────────┐
│           CAPA DE PRESENTACIÓN              │
│  JSP + JSTL (index, empleados, nominas...)  │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│         FRONT CONTROLLER (/app/*)           │
│      Punto único de entrada y routing       │
└──────────────────┬──────────────────────────┘
                   │
       ┌───────────┴───────────┐
       │                       │
┌──────▼────────┐    ┌────────▼──────┐
│  Empleados    │    │    Nominas    │
│  Controller   │    │   Controller  │
└──────┬────────┘    └────────┬──────┘
       │                      │
       └──────────┬───────────┘
                  │
┌─────────────────▼─────────────────┐
│       CAPA DE SERVICIO            │
│  IEmpleadoService │ INominaService │
│  (Lógica de negocio + validación) │
└─────────────────┬─────────────────┘
                  │
┌─────────────────▼─────────────────┐
│        CAPA DAO (Interfaces)      │
│  IEmpleadoDAO  │  INominaDAO      │
└─────────────────┬─────────────────┘
                  │
┌─────────────────▼─────────────────┐
│    IMPLEMENTACIONES CONCRETAS     │
│  EmpleadosDAO  │  NominasDAO      │
└─────────────────┬─────────────────┘
                  │
┌─────────────────▼─────────────────┐
│          BASE DE DATOS            │
│    MariaDB / MySQL (empleados, nominas) │
└───────────────────────────────────┘
```

### Responsabilidades por Capa

- **Presentación (JSP)**: Renderiza HTML usando datos del request, sin lógica de negocio
- **Front Controller**: Recibe todas las peticiones `/app/*` y las enruta al controlador correspondiente
- **Controladores**: Procesan peticiones HTTP, delegan a servicios, preparan datos y hacen forward a JSPs
- **Servicios**: Encapsulan lógica de negocio, validaciones, coordinan múltiples DAOs, transacciones
- **DAOs**: Acceso exclusivo a base de datos, implementan interfaces para desacoplamiento
- **Modelos**: Objetos del dominio (Empleado, Nomina, Persona)

---

## Patrones de Diseño Implementados

### 1. Singleton Pattern
**Clase**: `Conexion.java`

Garantiza una única instancia del pool de conexiones (DataSource) en toda la aplicación.

**Características**:
- Thread-safe con double-check locking
- Constructor privado
- Variable estática `volatile`
- Método `closeDataSource()` para liberar recursos

**Uso**:
```java
Connection conn = Conexion.getConnection();
// Usar conexión...
conn.close(); // Devuelve al pool
```

**Beneficios**: Eficiencia de recursos, control centralizado, evita múltiples pools

---

### 2. Factory Pattern
**Clase**: `EmpleadoFactory.java`

Centraliza la creación de objetos `Empleado` desde diferentes fuentes de datos.

**Métodos**:
- `crearDesdeResultSet(ResultSet rs)` - Desde consulta SQL
- `crearDesdeRequest(HttpServletRequest request)` - Desde formulario web
- `crearEmpleadoBasico(nombre, dni, sexo)` - Con valores por defecto
- `crearEmpleadoVacio()` - Objeto vacío para inicialización

**Uso**:
```java
// Desde base de datos
Empleado emp = EmpleadoFactory.crearDesdeResultSet(rs);

// Desde formulario
Empleado emp = EmpleadoFactory.crearDesdeRequest(request);
```

**Beneficios**: Elimina duplicación, lógica centralizada, fácil de mantener y testear

---

### 3. Builder Pattern
**Clase**: `EmpleadoBuilder.java`

Construcción fluida y legible de objetos `Empleado`.

**Uso**:
```java
Empleado empleado = EmpleadoBuilder.builder()
    .nombre("Juan Pérez")
    .dni("12345678A")
    .sexo("M")
    .categoria(5)
    .anyos(10)
    .build();

// Empleado básico (categoría 1, 0 años)
Empleado basico = EmpleadoBuilder.builder()
    .nombre("María García")
    .dni("87654321B")
    .sexo("F")
    .buildBasico();
```

**Beneficios**: Código legible, validación en construcción, valores por defecto

---

### 4. DAO Pattern con Interfaces (Dependency Inversion)
**Interfaces**: `IEmpleadoDAO`, `INominaDAO`  
**Implementaciones**: `EmpleadosDAO`, `NominasDAO`

Abstrae el acceso a datos mediante interfaces, cumpliendo el principio SOLID de inversión de dependencias.

**Arquitectura**:
```
Controller → IEmpleadoDAO (interfaz) → EmpleadosDAO (implementación) → BD
                    ↑
          Dependencia de abstracción
```

**Contratos definidos**:

`IEmpleadoDAO`:
- `listar()` - Todos los empleados
- `obtenerEmpleado(String dni)` - Por DNI
- `actualizarEmpleado(HttpServletRequest)` - Actualización
- `buscarPorCriterio(HttpServletRequest)` - Búsqueda flexible

`INominaDAO`:
- `obtenerNomina(String dni)` - Recuperar sueldo almacenado
- `actualizarSueldo(String dni, double sueldo)` - Actualizar sueldo

**Uso en Controllers (actualizado con Service Layer)**:
```java
public class EmpleadosController {
    private IEmpleadoService empleadoService; // Inyectamos servicio
    
    @Override
    public void init() {
        empleadoService = new EmpleadoService(); // Service maneja DAOs
    }
    
    protected void doGet(...) {
        List<Empleado> empleados = empleadoService.listarEmpleados();
    }
}
```

**Beneficios**:
- Desacoplamiento entre capas
- Fácil testing con mocks
- Múltiples implementaciones posibles (MySQL, PostgreSQL, MongoDB, in-memory)
- Cumple Dependency Inversion Principle (SOLID)

---

### 5. Front Controller Pattern
**Clase**: `FrontController.java`

Punto único de entrada para todas las peticiones HTTP (`/app/*`).

**Mapeo**: `@WebServlet("/app/*")`

**Enrutamiento**:
```
/app/empleados?action=*  → EmpleadosController
/app/nominas?action=*    → NominasController
```

**Características**:
- Centralización de peticiones
- Logging automático de todas las requests
- Manejo de errores unificado
- Validación de rutas
- Redirección a home si no hay pathInfo

**Flujo**:
```
Cliente → FrontController → Controller específico → DAO → BD
                           ↓
                    Manejo de errores centralizado
```

**Beneficios**: Control centralizado, logging uniforme, gestión de errores consistente, escalabilidad

---

### 6. Service Layer Pattern
**Interfaces**: `IEmpleadoService`, `INominaService`  
**Implementaciones**: `EmpleadoService`, `NominaService`

Encapsula la lógica de negocio en una capa intermedia entre Controllers y DAOs.

**¿Por qué Service Layer?**
| Problema sin Service Layer | Solución con Service Layer |
|----------------------------|----------------------------|
| Lógica mezclada en Controllers | Lógica centralizada en servicios |
| Difícil reutilizar lógica | Servicios reutilizables |
| Testing complicado | Mock de servicios es simple |
| Transacciones multi-DAO | Coordinación de múltiples DAOs |

**Arquitectura actualizada**:
```
Controller → IEmpleadoService → IEmpleadoDAO → BD
                   ↓
            Validaciones
            Lógica de negocio
            Coordinación de DAOs
```

**Responsabilidades**:
1. **Validación de negocio**: Reglas que no son de persistencia
2. **Coordinación**: Orquesta llamadas a múltiples DAOs
3. **Transformación**: Prepara datos para controllers
4. **Cálculos complejos**: Ej: calcular salario desde categoría

**Ejemplo: NominaService**
```java
public class NominaService implements INominaService {
    private IEmpleadoDAO empleadoDAO;
    private INominaDAO nominaDAO;
    
    @Override
    public Map<String, Object> consultarSalarioEmpleado(String dni) {
        // 1. Validación de negocio
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("DNI vacío");
        }
        
        // 2. Coordinación de múltiples DAOs
        Empleado empleado = empleadoDAO.obtenerEmpleado(dni);
        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no encontrado");
        }
        
        Map<String, Object> registro = nominaDAO.obtenerNomina(dni);
        
        // 3. Lógica de negocio: calcular salario si no existe
        double salario;
        if (registro != null) {
            salario = (double) registro.get("sueldo");
        } else {
            salario = new Nomina().sueldo(empleado); // Cálculo complejo
        }
        
        // 4. Preparar resultado
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("empleado", empleado);
        resultado.put("salario", salario);
        return resultado;
    }
}
```

**Testing con Mockito** (sin base de datos):
```java
@Test
public void testConsultarSalario() {
    // Mocks de DAOs
    IEmpleadoDAO mockEmpleadoDAO = mock(IEmpleadoDAO.class);
    INominaDAO mockNominaDAO = mock(INominaDAO.class);
    
    Empleado empleado = new Empleado("Juan", "12345678A", 'M', 5, 10);
    when(mockEmpleadoDAO.obtenerEmpleado("12345678A")).thenReturn(empleado);
    
    NominaService service = new NominaService(mockEmpleadoDAO, mockNominaDAO);
    Map<String, Object> resultado = service.consultarSalarioEmpleado("12345678A");
    
    assertNotNull(resultado);
    verify(mockEmpleadoDAO).obtenerEmpleado("12345678A");
}
```

**Beneficios concretos en este proyecto**:
- `NominaService` coordina `EmpleadosDAO` + `NominasDAO`
- Validaciones centralizadas (DNI vacío, empleado no encontrado)
- Cálculo de salario desde categoría si no hay registro
- Tests sin BD usando Mockito (ver `EmpleadoServiceTest`)
- Controllers más simples (solo delegan)

---

## Flujo de una Petición Completa (con Service Layer integrado)

**Ejemplo: Listar empleados**

```
1. Navegador → GET /empresa/app/empleados?action=listar

2. FrontController.service()
   - Detecta pathInfo: /empleados
   - Delega a EmpleadosController

3. EmpleadosController.doGet()
   - Lee parameter action=listar
   - Invoca empleadoService.listarEmpleados()
   
4. EmpleadoService.listarEmpleados()
   - Validaciones de negocio
   - Invoca empleadoDAO.listar()
   - Retorna List<Empleado>

5. EmpleadosDAO.listar()
   - Obtiene conexión del Singleton
   - Ejecuta SELECT * FROM empleados
   - Usa Factory para crear objetos Empleado
   - Retorna List<Empleado>

6. Controller prepara respuesta
   - request.setAttribute("listaEmpleados", lista)
   - forward a /empleados.jsp

7. JSP renderiza HTML
   - Itera con <c:forEach>
   - Genera tabla HTML

8. Respuesta al navegador
```

**Ejemplo: Consultar salario**

```
1. GET /empresa/app/nominas?action=formularioSalario
   → Muestra formulario salarioForm.jsp

2. Usuario introduce DNI → Submit

3. POST /empresa/app/nominas (action=consultarSalario, dni=12345678A)

4. NominasController.consultarSalario()
   - Invoca nominaService.consultarSalarioEmpleado(dni)

5. NominaService.consultarSalarioEmpleado(dni)
   - Valida DNI no nulo ni vacío
   - Busca empleado: empleadoDAO.obtenerEmpleado(dni)
   - Busca nómina: nominaDAO.obtenerNomina(dni)
   - Si no hay sueldo en BD: calcula con Nomina.sueldo(empleado)
   - Retorna Map con empleado + salario

6. Controller prepara atributos y forward a salarioResultado.jsp

7. JSP muestra resultado
```

---

## Modelo de Datos

### Tabla: empleados
```sql
CREATE TABLE empleados (
    dni VARCHAR(9) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    sexo CHAR(1) NOT NULL,
    categoria INT NOT NULL,
    anyos INT NOT NULL DEFAULT 0
);
```

### Tabla: nominas
```sql
CREATE TABLE nominas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    dni VARCHAR(9) NOT NULL,
    sueldo DOUBLE NOT NULL,
    FOREIGN KEY (dni) REFERENCES empleados(dni)
);
```

---

## Rutas de la Aplicación

| Función | Método | URL | Parámetros |
|---------|--------|-----|------------|
| **Página inicio** | GET | `/empresa/` | - |
| **Listar empleados** | GET | `/app/empleados` | `action=listar` |
| **Buscar empleados (form)** | GET | `/app/empleados` | `action=buscarForm` |
| **Buscar empleados (resultado)** | POST | `/app/empleados` | `action=buscarResultado`, `campo`, `valor` |
| **Editar empleado (form)** | POST | `/app/empleados` | `action=editar`, `dni` |
| **Actualizar empleado** | POST | `/app/empleados` | `action=actualizar` + campos formulario |
| **Form consultar salario** | GET | `/app/nominas` | `action=formularioSalario` |
| **Consultar salario** | POST | `/app/nominas` | `action=consultarSalario`, `dni` |
| **Listar nóminas** | GET | `/app/nominas` | `action=listarNominas` |

> Nota: Las acciones sensibles que incluyen DNI u otros criterios de búsqueda ahora usan POST para no exponer datos en la URL.

---

## Seguridad de rutas (GET vs POST)

Para mejorar la privacidad de los usuarios y evitar que datos personales queden expuestos en URLs, historiales del navegador, logs de proxies o analíticas, se han realizado estos cambios:

- Consultar salario: de GET a POST (`/app/nominas`, action=consultarSalario, dni)
- Buscar empleados (resultado): de GET a POST (`/app/empleados`, action=buscarResultado, campo, valor)
- Editar empleado (form): de GET a POST (`/app/empleados`, action=editar, dni)

Implicaciones prácticas:
- Los enlaces que antes eran anchors (a href) han sido reemplazados por formularios con método POST.
- Los formularios incluyen campos ocultos para action y, cuando aplique, dni/campo/valor.
- No cambia el flujo de usuario ni los JSP; solo el método HTTP con el que se envían los datos.

---

## Seguridad y Protección de Datos

### Medidas de seguridad implementadas

#### 1. Sanitización de errores según entorno

**Problema resuelto**: Evitar que mensajes de error técnicos (SQL, stack traces, rutas internas) se expongan al usuario final.

**Implementación**:
- Parámetro `app.environment` en `web.xml` (valores: `development` o `production`)
- Método `sanitizeErrorMessage()` en todos los controladores
- **Lista blanca**: Permite mensajes de negocio (DNI, empleado, nómina)
- **Lista negra**: Oculta mensajes técnicos (SQL, connection, table, exception)

**Ejemplo**:
```java
// Modo production
SQLException: "Table 'nominas' doesn't exist at line 45"
    ↓
Usuario ve: "Error al procesar la solicitud. Contacte al administrador."

// Modo development  
Usuario ve: mensaje completo + stack trace en desplegable
```

**Configuración**:
```xml
<!-- web.xml -->
<context-param>
    <param-name>app.environment</param-name>
    <param-value>development</param-value> <!-- Cambiar a 'production' en producción -->
</context-param>
```

**Beneficios**:
- No expone estructura de base de datos
- No revela rutas de archivos del servidor
- No muestra nombres de clases internas
- Logs del servidor siempre conservan error completo para administradores
- En desarrollo: debugging fácil con stack traces visibles

#### 2. Sesiones seguras (solo cookies)

**Problema resuelto**: Prevenir session hijacking por URLs compartidas con `jsessionid` visible.

**Implementación**:
```xml
<!-- web.xml -->
<session-config>
    <tracking-mode>COOKIE</tracking-mode>
</session-config>
```

**Efecto**:
- **Antes**: URLs podían incluir `;jsessionid=9F0DDD3B19456B59F6AB0CE96467D64D`
- **Ahora**: Session ID solo viaja en cookies HTTP, nunca en URL
- Si cookies están deshabilitadas: no hay sesión (más seguro que exponer ID en URL)

**Beneficios**:
- URLs limpias y compartibles sin riesgo de secuestro de sesión
- Session ID no queda en historiales del navegador
- Session ID no aparece en logs de proxies/analíticas

#### 3. Protección de datos personales (POST en acciones sensibles)

Las acciones que manejan DNI u otros datos personales usan **POST** en lugar de GET:
- Consultar salario: `POST /app/nominas` (action=consultarSalario, dni)
- Buscar empleados: `POST /app/empleados` (action=buscarResultado, campo, valor)
- Editar empleado: `POST /app/empleados` (action=editar, dni)

**Beneficios**:
- DNI no queda expuesto en barra de direcciones
- No se guarda en historial del navegador
- Cumplimiento con RGPD/LOPD

#### 4. Recursos protegidos

- JSPs internos en `WEB-INF/` (no accesibles directamente vía URL)
- `application.properties` dentro de `WEB-INF/classes/` (no servible por HTTP)
- Contenedor bloquea acceso a `WEB-INF/` y `META-INF/` por especificación Servlet

### Modo Development vs Production

| Aspecto | Development | Production |
|---------|-------------|------------|
| **Parámetro en web.xml** | `development` | `production` |
| **Mensajes de error** | Originales + técnicos | Sanitizados y genéricos |
| **Stack traces** | Visibles en error.jsp | NO se envían al navegador |
| **Logs del servidor** | Error completo | Error completo (igual) |
| **Uso recomendado** | Local, debugging | Servidores públicos |

**Cambiar de modo**: Editar `web.xml` → `<param-value>production</param-value>` → Recompilar (`mvn package`)

### Clase utilitaria ErrorHandler

Para evitar duplicación de código, toda la lógica de sanitización y manejo de errores está centralizada en **`com.util.ErrorHandler`**:

**Métodos disponibles**:

```java
// Sanitizar mensaje sin forward (útil para logging o validaciones)
String mensaje = ErrorHandler.sanitizeErrorMessage(exception.getMessage());

// Manejo completo con modo development/production (usado en FrontController)
ErrorHandler.handleError(e, request, response, getServletContext());

// Manejo simplificado con logging automático (EmpleadosController, NominasController)
ErrorHandler.handleErrorSimple(e, request, response);

// Obtener stack trace como String (para logging personalizado)
String stackTrace = ErrorHandler.getStackTraceAsString(exception);
```

**Ventajas de la centralización**:
- **DRY**: Lógica de sanitización en un solo lugar
- **Mantenibilidad**: Cambios en reglas de sanitización solo requieren modificar `ErrorHandler`
- **Reutilización**: Cualquier clase puede usar los métodos estáticos
- **Testing**: Fácil probar unitariamente sin instanciar servlets
- **Logging automático**: Todos los errores se registran en logs del servidor
- **Controladores más ligeros**: Solo orquestan, no gestionan detalles de errores

**Reglas de sanitización implementadas**:
- **Lista blanca** (se permiten): dni, empleado, nómina, categoría, salario, años
- **Lista negra** (se bloquean): sql, connection, database, table, column, jdbc, exception, nullpointer, class, stack

**Logging de errores**:
- `handleErrorSimple()` registra automáticamente en `ServletContext.log()` (archivo `localhost.YYYY-MM-DD.log`)
- Información logueada: timestamp, URI, método HTTP, mensaje original y stack trace completo
- Usuario final solo ve mensaje sanitizado, desarrollador tiene detalles completos en logs

---

### Salida segura en vistas (XSS)

Para prevenir Cross-Site Scripting, todas las variables renderizadas en JSP usan **`<c:out>`** (JSTL), que aplica `escapeXml=true` por defecto. Esto evita que contenido inyectado se interprete como HTML/JS.

Recomendaciones:
- No imprimir `${variable}` directamente en el HTML. Usa `<c:out value="${variable}"/>`.
- En atributos HTML (`value=`, `title=`, etc.), también usar `<c:out>`.
- En campos ocultos (`<input type="hidden">`), escapar el valor con `<c:out>`.
- Si necesitas permitir HTML controlado, valida/whitelistea previamente en backend y documenta el motivo.

Checklist para contribuciones:
- [ ] Salidas en `<td>`, `<h1..h6>`, `<p>`: `<c:out>`
- [ ] Atributos de inputs (`value`): `<c:out>`
- [ ] Campos `hidden`: `<c:out>`
- [ ] Mensajes de error al usuario: pasan por `ErrorHandler.sanitizeErrorMessage()` + `<c:out>`

---

### Buenas prácticas adicionales recomendadas

Para entornos de producción críticos:
- Cifrar contraseñas en `application.properties` (Jasypt u otro)
- Usar variables de entorno en lugar de properties para credenciales
- Implementar logger profesional (SLF4J + Logback) en lugar de `log()`
- Configurar HTTPS en Tomcat
- Limitar intentos de login si se añade autenticación

---

## Estructura del Proyecto

```
empresa/
├── pom.xml                           # Maven build configuration
├── README.md                         # Este archivo
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       ├── builder/
│       │       │   └── EmpleadoBuilder.java  # Builder pattern
│       │       ├── conexion/
│       │       │   ├── Conexion.java         # Singleton pool conexiones
│       │       │   └── gestion_de_nominas.sql # Script BD
│       │       ├── controller/
│       │       │   ├── FrontController.java  # Front Controller pattern
│       │       │   ├── EmpleadosController.java
│       │       │   └── NominasController.java
│       │       ├── dao/
│       │       │   ├── GenericDAO.java       # Interfaz genérica base
│       │       │   ├── IEmpleadoDAO.java     # Interfaz DAO empleados
│       │       │   ├── INominaDAO.java       # Interfaz DAO nóminas
│       │       │   ├── EmpleadosDAO.java     # Implementación
│       │       │   └── NominasDAO.java       # Implementación
│       │       ├── exceptions/
│       │       │   └── DatosNoCorrectosException.java
│       │       ├── factory/
│       │       │   └── EmpleadoFactory.java  # Factory pattern
│       │       ├── model/
│       │       │   ├── Persona.java          # Clase padre
│       │       │   ├── Empleado.java         # Modelo empleado
│       │       │   └── Nomina.java           # Lógica cálculo salario
│       │       ├── service/
│       │       │   ├── IEmpleadoService.java # Interfaz servicio empleados
│       │       │   ├── INominaService.java   # Interfaz servicio nóminas
│       │       │   ├── EmpleadoService.java  # Implementación
│       │       │   └── NominaService.java    # Implementación
│       │       └── util/
│       │           └── ErrorHandler.java     # Utilidad manejo errores centralizado
│       ├── resources/
│       │   └── application.properties        # Config externalizada (BD)
│       └── webapp/
│           ├── index.jsp             # Página principal
│           ├── empleados.jsp         # Listado empleados
│           ├── nominas.jsp           # Listado nóminas
│           ├── salarioForm.jsp       # Form consulta salario
│           ├── salarioResultado.jsp  # Resultado salario
│           ├── frontControllerDemo.jsp # Demo Front Controller
│           ├── detalleEmpleado.jsp   # Detalle empleado
│           ├── styles/
│           │   └── global.css        # Estilos
│           └── WEB-INF/
│               ├── web.xml           # Configuración webapp
│               ├── buscarEmpleado.jsp
│               ├── editarEmpleado.jsp
│               ├── resultadoBusqueda.jsp
│               └── error.jsp         # Página error centralizada
│   └── test/
│       └── java/
│           └── com/
│               └── service/
│                   └── EmpleadoServiceTest.java # Tests JUnit + Mockito
└── target/
    └── empresa.war                   # WAR generado
```

---

## Configuración y Despliegue

### Requisitos Previos
- JDK 8 o superior
- Apache Maven 3.x
- MariaDB 10.x (o MySQL 5.7+) instalado y corriendo en localhost:3306
- Apache Tomcat 9.x

### Configurar Base de Datos
1. Crear base de datos:
```sql
CREATE DATABASE gestion_de_nominas;
USE gestion_de_nominas;
```

2. Crear tablas y datos iniciales (usa tu script SQL o cliente MariaDB/MySQL)

3. Configurar credenciales en `src/main/resources/application.properties` (no en el código):
```properties
db.url=jdbc:mariadb://localhost:3306/gestion_de_nominas
db.username=root
db.password=123456
db.driver=org.mariadb.jdbc.Driver
```
Si usas MySQL en lugar de MariaDB:
```properties
db.url=jdbc:mysql://localhost:3306/gestion_de_nominas
db.driver=com.mysql.cj.jdbc.Driver
```

### Build y Despliegue

1. **Compilar proyecto**:
```bash
mvn clean package
```

2. **Desplegar en Tomcat**:
   - Copiar `target/empresa.war` a `$TOMCAT_HOME/webapps/`
   - O usar Community Server Connectors en VS Code

3. **Acceder a la aplicación**:
```
http://localhost:8080/empresa/
```

### Debugging y Logs

**Ver errores en logs de Tomcat** (Windows):
```powershell
# Logs de aplicación (aquí van los errores de ErrorHandler)
Get-Content "C:\Program Files\Apache Software Foundation\Tomcat 9.0\logs\localhost.YYYY-MM-DD.log" -Tail 50

# Monitoreo en tiempo real
Get-Content "C:\Program Files\Apache Software Foundation\Tomcat 9.0\logs\localhost.YYYY-MM-DD.log" -Tail 50 -Wait

# Buscar errores específicos
Select-String -Path "C:\Program Files\Apache Software Foundation\Tomcat 9.0\logs\localhost.*.log" -Pattern "ERROR EN APLICACIÓN" -Context 0,10
```

**Ver errores en logs de Tomcat** (Linux/Mac):
```bash
# Logs de aplicación
tail -f $CATALINA_HOME/logs/localhost.YYYY-MM-DD.log

# Buscar errores específicos
grep -A 10 "ERROR EN APLICACIÓN" $CATALINA_HOME/logs/localhost.*.log
```

**Información logueada por ErrorHandler**:
- Timestamp del error
- URI de la petición
- Método HTTP (GET/POST)
- Mensaje original de la excepción
- Stack trace completo

---

## Principios SOLID Aplicados

| Principio | Implementación |
|-----------|----------------|
| **Single Responsibility** | Cada clase tiene una única responsabilidad: Conexion (pool), Factory (creación), Controllers (lógica), DAOs (persistencia) |
| **Open/Closed** | Fácil agregar nuevas implementaciones de DAOs sin modificar código existente |
| **Liskov Substitution** | Cualquier implementación de IEmpleadoDAO puede sustituirse por otra |
| **Interface Segregation** | Interfaces específicas y cohesivas (IEmpleadoDAO, INominaDAO) |
| **Dependency Inversion** | Controllers dependen de abstracciones (interfaces), no de implementaciones concretas |

---

## Buenas Prácticas Implementadas

- **Pool de conexiones**: Uso de DBCP2 para eficiencia
- **Try-with-resources**: Liberación automática de recursos JDBC
- **JSTL `<c:url>`**: URLs portables independientes del contexto
- **Separación de capas (4 capas)**: Presentation → Controller → Service → DAO → BD
- **Forward a WEB-INF**: JSPs protegidos, solo accesibles via forward
- **Validaciones centralizadas**: En capa de servicio (Service Layer)
- **Manejo centralizado de errores**: Página error común con sanitización
- **Interfaces para DAOs y Services**: Preparado para inyección de dependencias
- **Testing con mocks**: JUnit + Mockito sin base de datos (ver `EmpleadoServiceTest`)
- **Configuración externalizada**: `application.properties` para credenciales BD
- **Seguridad**: Sanitización de errores, sesiones solo por cookies, POST para datos sensibles

---

## Manejo de Errores

Todos los errores se centralizan y sanitizan según el entorno configurado.

**Flujo de error**:
1. Excepción capturada en controlador
2. `manejarError()` invoca `sanitizeErrorMessage()`
3. Si modo `development`: añade `errorType` y `errorStackTrace` al request
4. Forward a `/WEB-INF/error.jsp`
5. JSP muestra mensaje sanitizado (+ detalles técnicos si development)
6. Error completo siempre se loggea en servidor

**Validaciones clave**:
- **En Service Layer**: DNI vacío, empleado no encontrado
- DNI no nulo en consultas
- Empleado existente antes de editar
- Campos requeridos en formularios
- Conexiones cerradas con try-with-resources

**Mensajes sanitizados**:
- Permitidos: "DNI no proporcionado", "Empleado no encontrado"
- Bloqueados: "SQLException: Table 'nominas' doesn't exist", "NullPointerException at line 45"

---

## Testing

El proyecto incluye tests unitarios con **JUnit 4** y **Mockito** para testing sin base de datos.

**Ejemplo: `EmpleadoServiceTest.java`**
```java
@Test
public void testListarEmpleados() {
    IEmpleadoDAO mockDAO = mock(IEmpleadoDAO.class);
    List<Empleado> empleados = Arrays.asList(
        new Empleado("Juan", "12345678A", 'M', 5, 10)
    );
    when(mockDAO.listar()).thenReturn(empleados);
    
    EmpleadoService service = new EmpleadoService(mockDAO);
    List<Empleado> resultado = service.listarEmpleados();
    
    assertEquals(1, resultado.size());
    verify(mockDAO).listar(); // Verifica interacción
}
```

**Ejecutar tests**:
```bash
mvn test
```

---

## Configuración Externalizada

Las credenciales de base de datos están en **`src/main/resources/application.properties`** (no se tocan clases Java):

Ejemplo (MariaDB):
```properties
db.url=jdbc:mariadb://localhost:3306/gestion_de_nominas
db.username=root
db.password=123456
db.driver=org.mariadb.jdbc.Driver
db.pool.initialSize=5
db.pool.maxTotal=20
```

Ejemplo alternativo (MySQL):
```properties
db.url=jdbc:mysql://localhost:3306/gestion_de_nominas
db.username=root
db.password=TU_PASSWORD
db.driver=com.mysql.cj.jdbc.Driver
```

**Beneficios**:
- Sin credenciales hardcodeadas en clases
- Fácil cambiar entre entornos (dev, test, prod)
- No recompilar para cambiar configuración
- Seguridad mejorada (properties puede estar en .gitignore / usar variables)
- Permite cambiar de MySQL a MariaDB sólo editando el properties

**Uso**: `Conexion.java` lee automáticamente este archivo vía ClassLoader.

---

## Explicación Resumida (Elevator Pitch)

> "Aplicación web Java EE que implementa **arquitectura de 4 capas** con Front Controller centralizando todas las peticiones HTTP. Los controladores delegan en **servicios que encapsulan lógica de negocio**, validaciones y coordinación de múltiples DAOs. Los DAOs están abstraídos mediante interfaces (IEmpleadoDAO, INominaDAO), reduciendo acoplamiento y facilitando testing con mocks (JUnit + Mockito). La creación de objetos Empleado está centralizada con Factory y facilitada por Builder. La conexión a base de datos usa Singleton con pool DBCP2 para eficiencia, leyendo configuración desde properties externos. Las vistas JSP únicamente presentan datos usando JSTL. El sistema implementa **6 patrones de diseño** profesionales y cuenta con tests sin base de datos (EmpleadoServiceTest)."

---

## Autor

**Gerónimo Molero Rodríguez**  
Proyecto educativo - Entorno Servidor  
Noviembre 2025

**Patrones implementados**: Singleton, Factory, Builder, DAO + Interfaces, Front Controller, Service Layer  
**Testing**: JUnit 4 + Mockito 3.12.4  
**Build**: SUCCESS con 19 source files + 1 test

---

## Troubleshooting BD

- `Unable to load authentication plugin 'auth_gssapi_client'`
    - Usa el driver de MariaDB y URL `jdbc:mariadb://...` (ver Configuración Externalizada).
- `Access denied for user 'root'@'localhost' (using password: NO)`
    - Añade `db.password` en `application.properties` o crea un usuario dedicado con contraseña.
- Cambiar entre MySQL/MariaDB
    - Edita `db.url` y `db.driver` como en los ejemplos; no hace falta recompilar.

---

## Licencia

Proyecto educativo para propósitos académicos.
