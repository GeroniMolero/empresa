<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Error en la aplicación</title>
    <link rel="stylesheet" href="<c:url value='/styles/global.css'/>">
</head>
<body>
    <header>
        <h1>Ha ocurrido un error</h1>
    </header>

    <main>
        <section class="error-container">
            <div class="error-box">
                <c:choose>
                    <c:when test="${not empty error}">
                        <p class="error-msg"><strong>${error}</strong></p>
                    </c:when>
                    <c:otherwise>
                        <p class="error-msg"><strong>Se ha producido un error inesperado.</strong></p>
                    </c:otherwise>
                </c:choose>

                <p>Por favor, inténtalo de nuevo o contacta con el administrador del sistema.</p>

                <div class="acciones">
                    <a href="<c:url value='/index.jsp'/>" class="btn">Volver al inicio</a>
                </div>
            </div>
        </section>
    </main>

    <footer>
        <p>© 2025 Gestión de Nóminas</p>
    </footer>
</body>
</html>
