<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nóminas de Empleados</title>
    <link rel="stylesheet" href="<c:url value='/styles/global.css'/>">
</head>
<body>
    <header>
        <h1>Nóminas de Empleados</h1>
    </header>

    <main>
        <section class="tabla-container">
            <c:choose>
                <c:when test="${empty listaNominas}">
                    <p class="mensaje">No se encontraron nóminas.</p>
                </c:when>

                <c:otherwise>
                    <table class="tabla">
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>DNI</th>
                                <th>Categoría</th>
                                <th>Años trabajados</th>
                                <th>Salario</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="n" items="${listaNominas}">
                                <tr>
                                    <td>${n.empleado.nombre}</td>
                                    <td>${n.empleado.dni}</td>
                                    <td>${n.empleado.categoria}</td>
                                    <td>${n.empleado.anyos}</td>
                                    <td>
                                        <fmt:formatNumber value="${n.salario}" type="currency" currencySymbol="€" />
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
