<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Ejemplo - Front Controller</title>
    <link rel="stylesheet" href="<c:url value='/styles/global.css'/>">
    <style>
        .demo-section {
            background: #f5f5f5;
            padding: 20px;
            margin: 20px 0;
            border-radius: 8px;
            border-left: 4px solid #007bff;
        }
        .demo-section h3 {
            margin-top: 0;
            color: #007bff;
        }
        .code-example {
            background: #282c34;
            color: #61dafb;
            padding: 15px;
            border-radius: 4px;
            font-family: 'Courier New', monospace;
            overflow-x: auto;
        }
        .url-example {
            background: #fff;
            border: 1px solid #ddd;
            padding: 10px;
            margin: 10px 0;
            border-radius: 4px;
            font-family: monospace;
            color: black;
        }
    </style>
</head>
<body>
    <header>
        <h1>🎯 Demostración del Front Controller Pattern</h1>
        <p>Ejemplos prácticos de uso del patrón Front Controller</p>
    </header>

    <main>
        <!-- Sección 1: URLs Antiguas vs Nuevas -->
        <div class="demo-section">
            <h3>1. Migración de URLs</h3>
            <p><strong>❌ Antes (acceso directo):</strong></p>
            <div class="url-example">
                /empresa/EmpleadosController?action=listar<br>
                /empresa/NominasController?action=consultarSalario&dni=12345678A
            </div>
            
            <p><strong>✅ Ahora (a través del Front Controller):</strong></p>
            <div class="url-example">
                /empresa/app/empleados?action=listar<br>
                /empresa/app/nominas?action=consultarSalario&dni=12345678A
            </div>
        </div>

        <!-- Sección 2: Ejemplos de Enlaces -->
        <div class="demo-section">
            <h3>2. Ejemplos de Enlaces Funcionales</h3>
            
            <h4>📋 Gestión de Empleados:</h4>
            <nav class="menu-links">
                <a href="<c:url value='/app/empleados?action=listar'/>" class="btn-menu">
                    Ver todos los empleados
                </a>
                <a href="<c:url value='/app/empleados?action=buscarForm'/>" class="btn-menu">
                    Buscar empleados
                </a>
            </nav>

            <h4>💰 Gestión de Nóminas:</h4>
            <nav class="menu-links">
                <a href="<c:url value='/app/nominas?action=listarNominas'/>" class="btn-menu">
                    Ver todas las nóminas
                </a>
                <a href="<c:url value='/salarioForm.jsp'/>" class="btn-menu">
                    Consultar salario individual
                </a>
            </nav>
        </div>

        <!-- Sección 3: Código JSP -->
        <div class="demo-section">
            <h3>3. Código JSP Correcto</h3>
            
            <p><strong>Enlace simple:</strong></p>
            <div class="code-example">
&lt;a href="&lt;c:url value='/app/empleados?action=listar'/&gt;"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;Ver Empleados<br>
&lt;/a&gt;
            </div>

            <p><strong>Formulario GET:</strong></p>
            <div class="code-example">
&lt;form action="&lt;c:url value='/app/nominas'/&gt;" method="get"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&lt;input type="hidden" name="action" value="consultarSalario"/&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&lt;input type="text" name="dni" placeholder="DNI"/&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&lt;button type="submit"&gt;Consultar&lt;/button&gt;<br>
&lt;/form&gt;
            </div>

            <p><strong>Formulario POST:</strong></p>
            <div class="code-example">
&lt;form action="&lt;c:url value='/app/empleados?action=actualizar'/&gt;" method="post"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&lt;input type="text" name="nombre" value="${empleado.nombre}"/&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&lt;input type="text" name="dni" value="${empleado.dni}" readonly/&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&lt;button type="submit"&gt;Guardar&lt;/button&gt;<br>
&lt;/form&gt;
            </div>

            <p><strong>Enlace con parámetros múltiples:</strong></p>
            <div class="code-example">
&lt;a href="&lt;c:url value='/app/empleados'&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;c:param name='action' value='editar'/&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;c:param name='dni' value='${empleado.dni}'/&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/c:url&gt;"&gt;<br>
&nbsp;&nbsp;&nbsp;&nbsp;Editar<br>
&lt;/a&gt;
            </div>
        </div>

        <!-- Sección 4: Flujo de Peticiones -->
        <div class="demo-section">
            <h3>4. Flujo de una Petición</h3>
            <ol style="line-height: 2; color:black">
                <li>📥 Cliente envía: <code>GET /app/empleados?action=listar</code></li>
                <li>🎯 FrontController recibe en <code>service()</code></li>
                <li>🔍 Analiza pathInfo: <code>/empleados</code></li>
                <li>➡️ Redirige a <code>EmpleadosController.service()</code></li>
                <li>⚙️ EmpleadosController procesa el action=listar</li>
                <li>📊 Consulta datos con EmpleadosDAO</li>
                <li>📄 Forward a empleados.jsp</li>
                <li>📤 Respuesta HTML al cliente</li>
            </ol>
        </div>

        <!-- Sección 5: Ventajas -->
        <div class="demo-section">
            <h3>5. Ventajas del Front Controller</h3>
            <ul style="line-height: 2; color:black">
                <li>✅ <strong>Centralización:</strong> Un solo punto de entrada para todas las peticiones</li>
                <li>✅ <strong>Mantenibilidad:</strong> Cambios de enrutamiento en un solo lugar</li>
                <li>✅ <strong>Logging:</strong> Registro automático de todas las peticiones</li>
                <li>✅ <strong>Seguridad:</strong> Control de acceso centralizado</li>
                <li>✅ <strong>Manejo de errores:</strong> Gestión uniforme de excepciones</li>
                <li>✅ <strong>Escalabilidad:</strong> Fácil agregar nuevos controladores</li>
            </ul>
        </div>

        <!-- Botón de retorno -->
        <div class="acciones">
            <a href="<c:url value='/index.jsp'/>" class="btn">Volver al inicio</a>
        </div>
    </main>

    <footer>
        <p>© 2025 Gestión de Nóminas - Ejemplo Front Controller Pattern</p>
    </footer>
</body>
</html>
