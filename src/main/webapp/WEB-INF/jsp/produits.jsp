<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/importTags.jsp" %>
<div class="container mt-4">
  <h1><spring:message code="catalog.title"/></h1>
  <p class="lead"><spring:message code="catalog.subtitle"/></p>

  <c:if test="${param.erreur == 'produit'}">
    <div class="alert alert-danger"><spring:message code="error.product.notFound"/></div>
  </c:if>

  <div class="row">
    <c:forEach items="${categories}" var="category">
      <div class="col-md-4 mb-3">
        <div class="card h-100 shadow-sm border-0 transition-hover">
          <img src="<spring:url value='${category.imageUrl}'/>" class="card-img-top joggin-image-categorie"
               alt="<c:out value='${category.nom}'/>">
          <div class="card-body text-center">
            <%-- Nom de categorie traduit : il vient de la table unique de traduction --%>
            <h5 class="card-title fw-bold my-3"><c:out value="${category.nom}"/></h5>
            <p class="card-text"><spring:message code="catalog.category.desc"/></p>
            <a href="<spring:url value='/produits/categorie/${category.id}'/>" class="btn btn-primary">
              <spring:message code="catalog.btn.view"/></a>
          </div>
        </div>
      </div>
    </c:forEach>
  </div>

  <c:if test="${empty categories}">
    <div class="alert alert-info"><spring:message code="catalog.empty"/></div>
  </c:if>
</div>
