<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/importTags.jsp" %>
<div class="container mt-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="<spring:url value='/'/>"><spring:message code="breadcrumbs.home"/></a></li>
            <li class="breadcrumb-item"><a href="<spring:url value='/produits'/>"><spring:message code="breadcrumbs.catalog"/></a></li>
            <li class="breadcrumb-item"><a href="<spring:url value='/produits/categorie/${product.categoryId}'/>"><c:out value="${product.categoryNom}"/></a></li>
            <li class="breadcrumb-item active"><c:out value="${product.nom}"/></li>
        </ol>
    </nav>

    <c:if test="${not empty cartError}">
        <div class="alert alert-danger"><spring:message code="${cartError}"/></div>
    </c:if>

    <div class="row">
        <div class="col-md-6">
            <div class="card border-0 shadow-sm position-relative">
                <c:if test="${product.newArrival}">
                    <div class="position-absolute top-0 start-0 m-3">
                        <span class="badge bg-success fs-5"><spring:message code="product.badge.new"/></span>
                    </div>
                </c:if>
                <c:if test="${product.enPromotion}">
                    <div class="position-absolute top-0 end-0 m-3">
                        <span class="badge bg-danger fs-5"><c:out value="${product.promotionLibelle}"/></span>
                    </div>
                </c:if>
                <div class="card-body text-center p-5">
                    <img src="<spring:url value='${product.imageUrl}'/>" class="img-fluid rounded joggin-image-detail"
                         alt="<c:out value='${product.nom}'/>">
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <h1><c:out value="${product.nom}"/></h1>
            <c:choose>
                <c:when test="${product.enPromotion}">
                    <p class="mb-1">
                        <span class="h2 text-danger"><fmt:formatNumber value="${product.prix}" type="currency" currencySymbol="€" minFractionDigits="2" /></span>
                        <span class="h4 text-muted text-decoration-line-through ms-3"><fmt:formatNumber value="${product.originalPrice}" type="currency" currencySymbol="€" minFractionDigits="2" /></span>
                    </p>
                </c:when>
                <c:otherwise>
                    <p class="h2 text-primary mb-3"><fmt:formatNumber value="${product.prix}" type="currency" currencySymbol="€" minFractionDigits="2" /></p>
                </c:otherwise>
            </c:choose>

            <div class="mb-4">
                <h5><spring:message code="product.description"/></h5>
                <p><c:out value="${product.description}"/></p>
            </div>

            <div class="mb-4">
                <p><strong><spring:message code="product.stock.available"/></strong> <c:out value="${product.stock}"/></p>
                <p><strong><spring:message code="product.category"/></strong> <c:out value="${product.categoryNom}"/></p>
                <p><strong><spring:message code="product.reference"/></strong> <c:out value="${product.code}"/></p>
            </div>

            <c:choose>
                <c:when test="${product.stock > 0}">
                    <%-- Ajout au panier en POST : une requete GET ne doit pas modifier le panier --%>
                    <form id="form-ajout-panier" method="post"
                          action="<spring:url value='/panier/ajouter/${product.id}'/>"
                          data-libelle-ajouter="<spring:message code='product.btn.addToCart'/>"
                          data-libelle-rupture="<spring:message code='product.outOfStock.short'/>">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                        <c:if test="${not empty product.sizesStock}">
                            <div class="mb-3 joggin-champ-court">
                                <label for="taille" class="form-label"><spring:message code="product.size"/></label>
                                <select id="taille" name="taille" class="form-select" required>
                                    <c:forEach items="${product.sizesStock}" var="entry">
                                        <option value="<c:out value='${entry.key}'/>" data-stock="${entry.value}" ${entry.value == 0 ? 'disabled' : ''}>
                                            <c:out value="${entry.key}"/>
                                            <c:choose>
                                                <c:when test="${entry.value > 0}">(<c:out value="${entry.value}"/>)</c:when>
                                                <c:otherwise>(<spring:message code="product.outOfStock.short"/>)</c:otherwise>
                                            </c:choose>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </c:if>

                        <div class="input-group mb-3 joggin-champ-court">
                            <label for="quantite" class="form-label me-2"><spring:message code="product.quantity"/></label>
                            <input type="number" class="form-control" id="quantite" name="quantite"
                                   value="1" min="1" max="${product.stock}" required>
                        </div>
                        <button type="submit" id="btn-add" class="btn btn-success btn-lg"><spring:message code="product.btn.addToCart"/></button>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-warning"><spring:message code="product.outOfStock"/></div>
                </c:otherwise>
            </c:choose>

            <a href="<spring:url value='/produits/categorie/${product.categoryId}'/>" class="btn btn-outline-secondary mt-3">
                <spring:message code="product.btn.backToCategory"/></a>
        </div>
    </div>
</div>
<%-- Script externe : la CSP (script-src 'self') interdit tout script inline --%>
<script src="<spring:url value='/js/produit-detail.js'/>"></script>
