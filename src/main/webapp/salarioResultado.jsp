<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Resultado del Salario</title>
    <link rel="stylesheet" href="<c:url value='/styles/global.css'/>">
</head>
<body>
    <header>
        <h1>Resultado del Cálculo de Salario</h1>
    </header>

    <main>
        <section class="resultado-container" 
        style="width: 80%;
                max-width: 700px;
                margin: 40px auto;
                background-color: #2c2c2c;
                padding: 25px;
                border-radius: 10px;
                box-shadow: 0 0 10px #00000055;">
            <c:choose>
                <c:when test="${not empty error}">
                    <div class="error"><c:out value="${error}"/></div>
                </c:when>
                <c:otherwise>
                    <h2>Empleado: <c:out value="${empleado.nombre}"/></h2>
                    <p><strong>DNI:</strong> <c:out value="${empleado.dni}"/></p>
                    <p><strong>Categoría:</strong> <c:out value="${empleado.categoria}"/></p>
                    <p><strong>Años trabajados:</strong> <c:out value="${empleado.anyos}"/></p>
                    <p><strong>Salario calculado:</strong> 
                        <span style="color:#5cff8a; font-size:1.3em;">
                            <c:out value="${salario}"/> €
                        </span>
                    </p>
                </c:otherwise>
            </c:choose>

            <div class="acciones">
                <a href="<c:url value='/index.jsp'/>" class="btn-secundario">Volver al inicio</a>
                <a href="<c:url value='/app/empleados?action=listar'/>" class="btn-secundario">Ver empleados</a>
            </div>
        </section>
    </main>

    <footer>
        <p>© 2025 Gestión de Nóminas</p>
    </footer>
</body>
</html>
