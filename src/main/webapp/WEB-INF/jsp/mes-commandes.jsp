<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/importTags.jsp" %>
<div class="container py-5">
    <h1><spring:message code="orders.title"/></h1>

    <c:if test="${not empty infoMessage}">
        <div class="alert alert-info"><spring:message code="${infoMessage}"/></div>
    </c:if>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="alert alert-info"><spring:message code="orders.empty"/></div>
            <a href="<spring:url value='/produits'/>" class="btn btn-primary"><spring:message code="cart.btn.catalog"/></a>
        </c:when>
        <c:otherwise>
            <table class="table table-hover align-middle">
                <thead>
                    <tr>
                        <th><spring:message code="order.number"/></th>
                        <th><spring:message code="order.date"/></th>
                        <th><spring:message code="order.amount"/></th>
                        <th><spring:message code="order.status"/></th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${orders}" var="order">
                        <tr>
                            <td>#<c:out value="${order.id}"/></td>
                            <td><c:out value="${order.dateCommande}"/></td>
                            <td><fmt:formatNumber value="${order.montantTotal}" type="currency" currencySymbol="€"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${order.statut == 'PAYEE'}">
                                        <span class="badge bg-success"><spring:message code="order.status.PAYEE"/></span>
                                    </c:when>
                                    <c:when test="${order.statut == 'ANNULEE'}">
                                        <span class="badge bg-secondary"><spring:message code="order.status.ANNULEE"/></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-warning text-dark"><spring:message code="order.status.EN_ATTENTE"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-end">
                                <c:if test="${order.enAttente}">
                                    <a href="<spring:url value='/commandes/${order.id}/attente'/>" class="btn btn-sm btn-outline-primary">
                                        <spring:message code="order.btn.resume"/></a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
