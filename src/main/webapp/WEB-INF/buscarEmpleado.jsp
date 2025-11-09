<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Buscar Empleado</title>
    <link rel="stylesheet" href="<c:url value='/styles/global.css'/>">
</head>
<body>
<header>
    <h1>Buscar y Modificar Empleado</h1>
</header>

<main>
    <section class="form-container">
        <form action="<c:url value='/app/empleados'/>" method="post" class="form-dark">
            <input type="hidden" name="action" value="buscarResultado"/>

            <label for="campo">Buscar por:</label>
            <select id="campo" name="campo" required>
                <option value="dni">DNI</option>
                <option value="nombre">Nombre</option>
                <option value="sexo">Sexo</option>
                <option value="categoria">Categoría</option>
                <option value="anyos">Años</option>
            </select>

            <label for="valor">Valor:</label>
            <input type="text" id="valor" name="valor" required placeholder="Introduce el valor a buscar"/>

            <input type="submit" value="Buscar" class="btn">
        </form>

        <div class="acciones">
            <a href="<c:url value='/index.jsp'/>" class="btn-secundario">Volver</a>
        </div>
    </section>
</main>

<footer>
    <p>© 2025 Gestión de Nóminas</p>
</footer>
</body>
</html>
