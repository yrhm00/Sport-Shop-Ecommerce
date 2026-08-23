<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../include/importTags.jsp" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
    <tiles:importAttribute name="titre" />
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="${titre}" /></title>
    <%-- Bootstrap et la feuille de style du site sont servis par l'application
         elle-meme : la CSP (style-src 'self') interdit les CDN et les styles inline. --%>
    <link href="<spring:url value='/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<spring:url value='/css/app.css'/>" rel="stylesheet">
</head>
<body>

    <div class="container">
        <header class="mb-4">
            <tiles:insertAttribute name="entete" />
            <tiles:insertAttribute name="menu" />
        </header>

        <main>
            <tiles:insertAttribute name="contenu" />
        </main>

        <footer class="footer text-center">
            <tiles:insertAttribute name="piedpage" />
        </footer>
    </div>

    <script src="<spring:url value='/js/app.js'/>"></script>
</body>
</html>
