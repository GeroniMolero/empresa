<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Empleado</title>
    <link rel="stylesheet" href="<c:url value='/styles/global.css'/>">
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background-color: #f4f4f4;
        }
        header, footer {
            text-align: center;
            margin-bottom: 20px;
        }
        .form-container {
            background: white;
            padding: 20px 30px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            width: 420px;
            margin: auto;
        }
        label {
            display: block;
            margin-top: 10px;
            font-weight: bold;
        }
        input, select {
            width: 100%;
            padding: 6px;
            margin-top: 5px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        .acciones {
            margin-top: 20px;
            display: flex;
            justify-content: space-between;
        }
        .btn, .btn-secundario {
            text-decoration: none;
            padding: 8px 16px;
            border-radius: 4px;
            color: white;
            text-align: center;
        }
        .btn { background-color: #007bff; }
        .btn-secundario { background-color: #6c757d; }
        .error { color: red; font-weight: bold; }
    </style>
</head>
<body>

<header>
    <h1>Editar Empleado</h1>
</header>

<main>
    <section class="form-container">
        <!-- Mostrar error si no se cargó el empleado -->
        <c:if test="${empty empleado}">
            <p class="error">❌ No se pudo cargar el empleado. Verifique el DNI.</p>
            <a href="<c:url value='/app/empleados?action=listar'/>" class="btn-secundario">Volver</a>
        </c:if>

        <!-- Mostrar formulario si el empleado existe -->
        <c:if test="${not empty empleado}">
            <form action="<c:url value='/app/empleados?action=actualizar'/>" method="post">

                <!-- DNI -->
                <label for="dni">DNI:</label>
                <input type="text" id="dni" name="dni" value="${empleado.dni}" readonly>

                <!-- Nombre -->
                <label for="nombre">Nombre:</label>
                <input type="text" id="nombre" name="nombre" value="${empleado.nombre}" required>

                <!-- Sexo -->
                <label for="sexo">Sexo:</label>
                <c:set var="sexoVal" value="${empleado.sexo}" />
                <select id="sexo" name="sexo">
                    <option value="M" ${sexoVal eq 'M' || sexoVal eq 'm' ? 'selected' : ''}>Masculino</option>
                    <option value="F" ${sexoVal eq 'F' || sexoVal eq 'f' ? 'selected' : ''}>Femenino</option>
                </select>

                <!-- Categoría -->
                <label for="categoria">Categoría:</label>
                <input type="number" id="categoria" name="categoria" min="1" max="10"
                       value="${empleado.categoria}" required>

                <!-- Años trabajados -->
                <label for="anyos">Años trabajados:</label>
                <input type="number" id="anyos" name="anyos" min="0"
                       value="${empleado.anyos}" required>

                <!-- Botones -->
                <div class="acciones">
                    <input type="submit" value="Guardar cambios" class="btn">
                    <a href="<c:url value='/app/empleados?action=listar'/>" class="btn-secundario">Cancelar</a>
                </div>
            </form>
        </c:if>
    </section>
</main>

<footer>
    <p>© 2025 Gestión de Nóminas — Aplicación Web desarrollada en Java EE con MVC</p>
</footer>

</body>
</html>
