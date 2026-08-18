<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/importTags.jsp" %>
<div class="container mt-4">
    <h1><spring:message code="cart.title"/></h1>

    <c:if test="${not empty cartError}">
        <div class="alert alert-danger"><spring:message code="${cartError}"/></div>
    </c:if>
    <c:if test="${not empty cartSuccess}">
        <div class="alert alert-success"><spring:message code="${cartSuccess}"/></div>
    </c:if>

    <c:choose>
        <c:when test="${not empty cart.items}">
            <div class="table-responsive">
                <table class="table table-bordered align-middle">
                    <thead>
                        <tr>
                            <th><spring:message code="cart.table.product"/></th>
                            <th><spring:message code="cart.table.unitPrice"/></th>
                            <th><spring:message code="cart.table.quantity"/></th>
                            <th><spring:message code="cart.table.subtotal"/></th>
                            <th><spring:message code="cart.table.actions"/></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${cart.items}" var="item">
                            <tr>
                                <td>
                                    <strong><c:out value="${item.product.nom}"/></strong>
                                    <c:if test="${not empty item.taille}">
                                        <span class="badge bg-secondary ms-1"><c:out value="${item.taille}"/></span>
                                    </c:if><br>
                                    <small class="text-muted"><c:out value="${item.product.description}"/></small>
                                </td>
                                <td><fmt:formatNumber value="${item.product.prix}" type="currency" currencySymbol="€" minFractionDigits="2" /></td>
                                <td>
                                    <form method="post" action="<spring:url value='/panier/modifier'/>" class="d-inline">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <input type="hidden" name="productId" value="${item.product.id}" />
                                        <c:if test="${not empty item.taille}">
                                            <input type="hidden" name="taille" value="<c:out value='${item.taille}'/>" />
                                        </c:if>
                                        <input type="number" name="quantite" value="${item.quantite}" min="1"
                                               max="${item.product.stock}" class="form-control form-control-sm joggin-champ-quantite" />
                                        <button type="submit" class="btn btn-sm btn-outline-primary"><spring:message code="cart.btn.modify"/></button>
                                    </form>
                                </td>
                                <td><strong><fmt:formatNumber value="${item.sousTotal}" type="currency" currencySymbol="€" minFractionDigits="2" /></strong></td>
                                <td>
                                    <%-- Suppression en POST, avec confirmation geree par app.js --%>
                                    <form method="post" action="<spring:url value='/panier/supprimer/${item.product.id}'/>"
                                          class="d-inline" data-confirm="<spring:message code='cart.confirm.delete'/>">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <c:if test="${not empty item.taille}">
                                            <input type="hidden" name="taille" value="<c:out value='${item.taille}'/>" />
                                        </c:if>
                                        <button type="submit" class="btn btn-sm btn-danger"><spring:message code="cart.btn.delete"/></button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>
                        <c:if test="${cart.discountAmount > 0}">
                            <tr>
                                <td colspan="3" class="text-end text-success"><strong><spring:message code="cart.promo.label"/></strong></td>
                                <td colspan="2"><strong class="text-success">- <fmt:formatNumber value="${cart.discountAmount}" type="currency" currencySymbol="€" minFractionDigits="2" /></strong></td>
                            </tr>
                            <tr>
                                <td colspan="3" class="text-end"><strong><spring:message code="cart.total.pay"/></strong></td>
                                <td colspan="2"><strong class="h4 text-primary"><fmt:formatNumber value="${cart.totalWithDiscount}" type="currency" currencySymbol="€" minFractionDigits="2" /></strong></td>
                            </tr>
                        </c:if>
                        <c:if test="${cart.discountAmount <= 0}">
                            <tr>
                                <td colspan="3" class="text-end"><strong><spring:message code="cart.total"/></strong></td>
                                <td colspan="2"><strong class="h4 text-primary"><fmt:formatNumber value="${cart.total}" type="currency" currencySymbol="€" minFractionDigits="2" /></strong></td>
                            </tr>
                        </c:if>
                    </tfoot>
                </table>
            </div>

            <div class="mt-4">
                <a href="<spring:url value='/produits'/>" class="btn btn-outline-secondary"><spring:message code="cart.continue"/></a>

                <form method="post" action="<spring:url value='/panier/vider'/>" class="d-inline"
                      data-confirm="<spring:message code='cart.confirm.empty'/>">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="btn btn-outline-danger"><spring:message code="cart.btn.empty"/></button>
                </form>

                <sec:authorize access="isAuthenticated()">
                    <a href="<spring:url value='/commandes/checkout'/>" class="btn btn-success btn-lg float-end">
                        <spring:message code="cart.btn.validate"/>
                    </a>
                </sec:authorize>
                <sec:authorize access="isAnonymous()">
                    <div class="float-end text-end">
                        <p class="text-muted mb-2 small"><spring:message code="cart.guest.message"/></p>
                        <a href="<spring:url value='/connexion'/>" class="btn btn-warning btn-lg">
                            <spring:message code="cart.guest.login"/>
                        </a>
                    </div>
                </sec:authorize>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info">
                <h4><spring:message code="cart.empty.title"/></h4>
                <p><spring:message code="cart.empty.text"/></p>
            </div>
            <a href="<spring:url value='/produits'/>" class="btn btn-primary"><spring:message code="cart.btn.catalog"/></a>
        </c:otherwise>
    </c:choose>
</div>
