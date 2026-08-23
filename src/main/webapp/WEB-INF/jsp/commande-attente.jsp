<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/importTags.jsp" %>
<div class="container py-5">

    <%-- La commande a bien ete enregistree avant le paiement : elle n'est pas
         supprimee si le client abandonne. On lui laisse explicitement le choix
         entre payer maintenant et annuler. --%>

    <c:if test="${erreurPaiement == 'payment_cancelled'}">
        <div class="alert alert-warning"><spring:message code="error.payment.cancelled"/></div>
    </c:if>
    <c:if test="${erreurPaiement == 'payment_failed'}">
        <div class="alert alert-danger"><spring:message code="error.payment.failed"/></div>
    </c:if>
    <c:if test="${erreurPaiement == 'payment_init'}">
        <div class="alert alert-danger"><spring:message code="error.payment.init"/></div>
    </c:if>
    <c:if test="${erreurPaiement == 'stock_unavailable'}">
        <div class="alert alert-danger"><spring:message code="error.payment.stock"/></div>
    </c:if>

    <div class="card shadow-sm">
        <div class="card-header bg-secondary text-white">
            <h4 class="mb-0"><spring:message code="order.pending.title" arguments="${order.id}"/></h4>
        </div>
        <div class="card-body">
            <p class="lead"><spring:message code="order.pending.explanation"/></p>

            <dl class="row">
                <dt class="col-sm-4"><spring:message code="order.date"/></dt>
                <dd class="col-sm-8"><c:out value="${order.dateCommande}"/></dd>

                <dt class="col-sm-4"><spring:message code="order.amount"/></dt>
                <dd class="col-sm-8"><fmt:formatNumber value="${order.montantTotal}" type="currency" currencySymbol="€"/></dd>

                <dt class="col-sm-4"><spring:message code="order.status"/></dt>
                <dd class="col-sm-8"><span class="badge bg-warning text-dark"><spring:message code="order.status.EN_ATTENTE"/></span></dd>
            </dl>

            <div class="d-flex gap-2 mt-4">
                <form method="post" action="<spring:url value='/commandes/${order.id}/payer'/>">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="btn btn-success btn-lg"><spring:message code="order.btn.payNow"/></button>
                </form>

                <form method="post" action="<spring:url value='/commandes/${order.id}/annuler'/>"
                      data-confirm="<spring:message code='order.confirm.cancel'/>">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="btn btn-outline-danger btn-lg"><spring:message code="order.btn.cancel"/></button>
                </form>

                <a href="<spring:url value='/produits'/>" class="btn btn-outline-secondary btn-lg ms-auto">
                    <spring:message code="order.btn.payLater"/></a>
            </div>
        </div>
    </div>
</div>
