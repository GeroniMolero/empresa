<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Consultar Salario</title>
    <link rel="stylesheet" href="<c:url value='/styles/global.css'/>">
</head>
<body>
    <header>
        <h1>Consultar Salario de un Empleado</h1>
    </header>

    <main>
        <section class="form-container">
            <form action="<c:url value='/app/nominas'/>" method="get" class="form-dark">
                <input type="hidden" name="action" value="consultarSalario" />

                <label for="dni">Introduce el DNI del empleado:</label>
                <input type="text" id="dni" name="dni" required placeholder="Ej: 11111111A" maxlength="9">

                <input type="submit" value="Consultar salario" class="btn">
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
