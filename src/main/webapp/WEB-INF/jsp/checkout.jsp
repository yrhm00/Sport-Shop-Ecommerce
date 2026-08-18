<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/importTags.jsp" %>
<div class="container py-5">
    <h2><spring:message code="checkout.title"/></h2>
    <p class="text-muted"><spring:message code="checkout.confirmationRequired"/></p>

    <c:choose>
        <c:when test="${empty cart.items}">
            <div class="alert alert-warning"><spring:message code="cart.empty"/></div>
            <a href="<spring:url value='/produits'/>" class="btn btn-primary"><spring:message code="cart.continue"/></a>
        </c:when>
        <c:otherwise>
            <div class="table-responsive mb-4">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th><spring:message code="cart.table.product"/></th>
                            <th><spring:message code="cart.table.unitPrice"/></th>
                            <th><spring:message code="cart.table.quantity"/></th>
                            <th><spring:message code="cart.table.subtotal"/></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${cart.items}" var="item">
                            <tr>
                                <td>
                                    <c:out value="${item.product.nom}"/>
                                    <c:if test="${not empty item.taille}">
                                        <span class="badge bg-secondary ms-1"><c:out value="${item.taille}"/></span>
                                    </c:if>
                                </td>
                                <td><fmt:formatNumber value="${item.product.prix}" type="currency" currencySymbol="€"/></td>
                                <td><c:out value="${item.quantite}"/></td>
                                <td><fmt:formatNumber value="${item.sousTotal}" type="currency" currencySymbol="€"/></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${cart.discountAmount > 0}">
                            <tr>
                                <td colspan="3" class="text-end text-success fw-bold"><spring:message code="cart.promo.label"/></td>
                                <td class="text-success fw-bold">- <fmt:formatNumber value="${cart.discountAmount}" type="currency" currencySymbol="€"/></td>
                            </tr>
                        </c:if>
                        <tr>
                            <td colspan="3" class="text-end fw-bold"><spring:message code="cart.total.pay"/></td>
                            <td class="fw-bold"><fmt:formatNumber value="${cart.totalWithDiscount}" type="currency" currencySymbol="€"/></td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <c:if test="${not empty paymentError}">
                <div class="alert alert-danger"><spring:message code="${paymentError}"/></div>
            </c:if>

            <sec:authorize access="isAuthenticated()">
                <form action="<spring:url value='/commandes/confirmer'/>" method="post" class="mt-4">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="card mb-4">
                        <div class="card-header bg-primary text-white">
                            <spring:message code="payment.paypal.header"/>
                        </div>
                        <div class="card-body text-center">
                            <p class="mb-3"><spring:message code="payment.paypal.redirect"/></p>
                            <button type="submit" class="btn btn-warning btn-lg">
                                <spring:message code="payment.paypal.button"/>
                            </button>
                        </div>
                    </div>
                </form>
            </sec:authorize>
        </c:otherwise>
    </c:choose>
</div>
