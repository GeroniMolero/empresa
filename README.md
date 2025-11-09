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

3. GET /empresa/app/nominas?action=consultarSalario&dni=12345678A

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
| **Buscar empleados (resultado)** | GET | `/app/empleados` | `action=buscarResultado`, `campo`, `valor` |
| **Editar empleado (form)** | GET | `/app/empleados` | `action=editar`, `dni` |
| **Actualizar empleado** | POST | `/app/empleados` | `action=actualizar` + campos formulario |
| **Form consultar salario** | GET | `/app/nominas` | `action=formularioSalario` |
| **Consultar salario** | GET | `/app/nominas` | `action=consultarSalario`, `dni` |
| **Listar nóminas** | GET | `/app/nominas` | `action=listarNominas` |

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
│       │       └── service/
│       │           ├── IEmpleadoService.java # Interfaz servicio empleados
│       │           ├── INominaService.java   # Interfaz servicio nóminas
│       │           ├── EmpleadoService.java  # Implementación
│       │           └── NominaService.java    # Implementación
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
- **Manejo centralizado de errores**: Página error común
- **Interfaces para DAOs y Services**: Preparado para inyección de dependencias
- **Testing con mocks**: JUnit + Mockito sin base de datos (ver `EmpleadoServiceTest`)
- **Configuración externalizada**: `application.properties` para credenciales BD

---

## Manejo de Errores

Todos los errores se centralizan en `FrontController.manejarError()` y se redirigen a `/WEB-INF/error.jsp`.

Validaciones clave:
- **En Service Layer**: DNI vacío, empleado no encontrado
- DNI no nulo en consultas
- Empleado existente antes de editar
- Campos requeridos en formularios
- Conexiones cerradas con try-with-resources

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

## Posibles Mejoras Futuras

1. **Más tests**: Añadir tests para `NominaService` y controladores
2. **Spring Framework**: Inyección de dependencias automática (`@Autowired`)
3. **Seguridad**: Autenticación, autorización por roles (Spring Security)
4. **Paginación**: Listados grandes con paginación
5. **Validaciones**: JSR-303 Bean Validation en modelos
6. **Internacionalización**: Soporte multi-idioma (i18n)
7. **REST API**: Endpoints REST para integración con frontend moderno
8. **Cache**: Redis/Ehcache para consultas frecuentes
9. **Logging**: SLF4J + Logback en lugar de printStackTrace
10. **Profiles Maven**: dev, test, prod con application-{profile}.properties
11. **CI/CD**: Pipeline automatizado (GitHub Actions, Jenkins)
12. **DTOs**: Separar modelos de dominio de objetos de transferencia

---

## Explicación Resumida (Elevator Pitch)

> "Aplicación web Java EE que implementa **arquitectura de 4 capas** con Front Controller centralizando todas las peticiones HTTP. Los controladores delegan en **servicios que encapsulan lógica de negocio**, validaciones y coordinación de múltiples DAOs. Los DAOs están abstraídos mediante interfaces (IEmpleadoDAO, INominaDAO), reduciendo acoplamiento y facilitando testing con mocks (JUnit + Mockito). La creación de objetos Empleado está centralizada con Factory y facilitada por Builder. La conexión a base de datos usa Singleton con pool DBCP2 para eficiencia, leyendo configuración desde properties externos. Las vistas JSP únicamente presentan datos usando JSTL. El sistema implementa **6 patrones de diseño** profesionales y cuenta con tests sin base de datos (EmpleadoServiceTest)."

---

## Documentación Adicional

- **`PATRONES_RESUMEN.md`**: Documentación completa de los 6 patrones implementados con ejemplos
- **`INTERFACES_GUIDE.md`**: Guía sobre uso de interfaces DAO y Service
- **`FRONT_CONTROLLER_GUIDE.md`**: Guía detallada del Front Controller
- **`ARQUITECTURA_Y_FUNCIONAMIENTO.md`**: Análisis profundo de la arquitectura
- **`MEJORAS_IMPLEMENTADAS.md`**: Detalles de las mejoras críticas (Maven, Service Layer, Testing)

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
