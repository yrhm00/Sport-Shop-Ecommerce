<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/importTags.jsp" %>
<div class="container py-5 text-center">
    <div class="alert alert-warning p-5">
        <h1 class="display-4"><c:out value="${codeErreur}"/></h1>
        <p class="lead mt-3"><spring:message code="${cleErreur}"/></p>
        <hr>
        <a href="<spring:url value='/'/>" class="btn btn-primary mt-3"><spring:message code="error.backHome"/></a>
    </div>
</div>
