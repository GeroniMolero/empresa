<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle de Empleado</title>
    <link rel="stylesheet" href="<c:url value='/styles/global.css'/>">
</head>
<body>
    <header>
        <h1>Detalle del Empleado</h1>
    </header>

    <main>
        <section class="detalle-container">
            <c:choose>
                <c:when test="${not empty error}">
                    <div class="error">${error}</div>
                </c:when>

                <c:when test="${not empty empleado}">
                    <div class="detalle">
                        <h2>${empleado.nombre}</h2>
                        <p><strong>DNI:</strong> ${empleado.dni}</p>
                        <p><strong>Sexo:</strong> ${empleado.sexo}</p>
                        <p><strong>Categoría:</strong> ${empleado.categoria}</p>
                        <p><strong>Años trabajados:</strong> ${empleado.anyos}</p>
                    </div>

                    <div class="acciones">
                        <a href="<c:url value='/app/empleados'>
                                    <c:param name='action' value='editar'/>
                                    <c:param name='dni' value='${empleado.dni}'/>
                                 </c:url>" class="btn-small">Editar</a>

                        <a href="<c:url value='/app/nominas'>
                                    <c:param name='action' value='consultarSalario'/>
                                    <c:param name='dni' value='${empleado.dni}'/>
                                 </c:url>" class="btn-small">Ver salario</a>

                        <a href="<c:url value='/index.jsp'/>" class="btn">Volver</a>
                    </div>
                </c:when>

                <c:otherwise>
                    <p class="mensaje">No se encontró el empleado solicitado.</p>
                    <div class="acciones">
                        <a href="<c:url value='/index.jsp'/>" class="btn-secundario">Volver</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>

    <footer>
        <p>© 2025 Gestión de Nóminas</p>
    </footer>
</body>
</html>
