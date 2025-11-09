<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Listado de Empleados</title>
    <link rel="stylesheet" href="<c:url value='/styles/global.css'/>">
</head>
<body>
    <header>
        <h1>Listado de Empleados</h1>
    </header>

    <main>
        <section class="tabla-container">
            <c:choose>
                <c:when test="${empty listaEmpleados}">
                    <p class="mensaje">No hay empleados registrados.</p>
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
                                <td>${empleado.nombre}</td>
                                <td>${empleado.dni}</td>
                                <td>${empleado.sexo}</td>
                                <td>${empleado.categoria}</td>
                                <td>${empleado.anyos}</td>
                                <td>
                                    <a class="btn-small" href="<c:url value='/app/empleados?action=editar&dni=${empleado.dni}'/>">Editar</a>
                                    <a class="btn-small" href="<c:url value='/app/nominas?dni=${empleado.dni}&action=consultarSalario'/>">Ver salario</a>
                                </td>
                                </tr>
                            </c:forEach>
                        </tbody>

                    </table>
                </c:otherwise>
            </c:choose>

            <div class="acciones">
                <a href="<c:url value='/index.jsp'/>" class="btn-secundario">Volver al inicio</a>
            </div>
        </section>
    </main>

    <footer>
        <p>© 2025 Gestión de Nóminas</p>
    </footer>
</body>
</html>
