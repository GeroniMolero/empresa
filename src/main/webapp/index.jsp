<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Nóminas - Inicio</title>
    <link rel="stylesheet" href="<c:url value='/styles/global.css'/>">
</head>
<body>
    <header>
        <h1>Gestión de Nóminas</h1>
        <p>Aplicación Web de gestión de empleados y nóminas</p>
    </header>

    <main>
        <section class="menu-container">
            <div class="menu-card">
                <h2>Opciones principales</h2>

                <nav class="menu-links">
                    <a href="<c:url value='/app/empleados?action=listar'/>" class="btn-menu">Ver todos los empleados</a>
                    <a href="<c:url value='/app/nominas?action=formularioSalario'/>" class="btn-menu">Consultar salario</a>
                    <a href="<c:url value='/app/empleados?action=buscarForm'/>" class="btn-menu">Modificar empleado</a>
                </nav>
            </div>
        </section>
    </main>

    <footer>
        <p>© 2025 Gestión de Nóminas — Aplicación Web desarrollada por Gerónimo Molero Rodríguez | Java EE • MVC • MySQL</p>
    </footer>
</body>
</html>
