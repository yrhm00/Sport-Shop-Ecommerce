<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../include/importTags.jsp" %>
<div class="d-flex align-items-center pb-3 mb-3 border-bottom">
    <a href="<spring:url value='/'/>" class="d-flex align-items-center text-dark text-decoration-none">
        <span class="fs-4"><spring:message code="general.title"/></span>
    </a>
    <div class="ms-auto">
        <%-- Le choix de la langue conserve la page courante. --%>
        <a href="?lang=fr" class="btn btn-sm btn-outline-primary" hreflang="fr">FR</a>
        <a href="?lang=en" class="btn btn-sm btn-outline-secondary" hreflang="en">EN</a>
    </div>
</div>
