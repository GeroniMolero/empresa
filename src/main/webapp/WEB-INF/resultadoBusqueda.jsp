<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Resultado de Búsqueda</title>
    <link rel="stylesheet" href="<c:url value='/styles/global.css'/>">
</head>
<body>
<header>
    <h1>Resultado de Búsqueda de Empleados</h1>
</header>

<main>
    <section class="tabla-container">

        <c:choose>
            <c:when test="${empty listaEmpleados}">
                <p class="mensaje">No se encontraron empleados con los criterios de búsqueda.</p>
            </c:when>

            <c:otherwise>
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>DNI</th>
                            <th>Sexo</th>
                            <th>Categoría</th>
                            <th>Años trabajados</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="empleado" items="${listaEmpleados}">
                            <tr>
                                <td><c:out value="${empleado.nombre}"/></td>
                                <td><c:out value="${empleado.dni}"/></td>
                                <td><c:out value="${empleado.sexo}"/></td>
                                <td><c:out value="${empleado.categoria}"/></td>
                                <td><c:out value="${empleado.anyos}"/></td>
                                <td>
                                    <form action="<c:url value='/app/empleados'/>" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="editar">
                                        <input type="hidden" name="dni" value="<c:out value='${empleado.dni}'/>">
                                        <button type="submit" class="btn-small">Editar</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <div class="acciones">
            <a href="<c:url value='/app/empleados?action=buscarForm'/>" class="btn-secundario">Nueva búsqueda</a>
            <a href="<c:url value='/app/empleados?action=listar'/>" class="btn-secundario">Volver al listado</a>
        </div>

    </section>
</main>

<footer>
    <p>© 2025 Gestión de Nóminas</p>
</footer>
</body>
</html>
