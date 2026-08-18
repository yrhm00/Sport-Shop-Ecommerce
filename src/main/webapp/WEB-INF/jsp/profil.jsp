<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/importTags.jsp" %>
<div class="container py-5">
  <div class="row justify-content-center">
    <div class="col-lg-8">

      <div class="text-center mb-5">
        <h1 class="display-5 fw-bold"><spring:message code="profile.title"/></h1>
        <p class="lead text-muted"><spring:message code="profile.subtitle"/></p>
      </div>

      <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" role="alert"><spring:message code="${errorMessage}"/></div>
      </c:if>
      <c:if test="${not empty successMessage}">
        <div class="alert alert-success" role="alert"><spring:message code="${successMessage}"/></div>
      </c:if>

      <div class="card shadow-sm border-0 rounded-3">
        <div class="card-header bg-primary text-white p-4 rounded-top-3">
          <h4 class="mb-0"><spring:message code="menu.profile"/></h4>
        </div>
        <div class="card-body p-4 p-md-5 bg-light">
          <%-- <form:form> ajoute automatiquement le jeton CSRF --%>
          <form:form method="post" action="${pageContext.request.contextPath}/profil" modelAttribute="profileForm">

            <div class="mb-4">
              <label class="form-label text-muted small text-uppercase fw-bold"><spring:message code="profile.username.fixed"/></label>
              <%-- Champ en lecture seule : le username n'est jamais modifiable --%>
              <form:input path="username" type="text" class="form-control bg-white" readonly="true" />
            </div>

            <hr class="my-4 text-muted" />

            <div class="row g-3 mb-3">
              <div class="col-md-6">
                <form:label path="nom" class="form-label fw-bold"><spring:message code="user.lastname"/></form:label>
                <form:input path="nom" type="text" class="form-control" />
                <form:errors path="nom" cssClass="text-danger small mt-1" />
              </div>
              <div class="col-md-6">
                <form:label path="prenom" class="form-label fw-bold"><spring:message code="user.firstname"/></form:label>
                <form:input path="prenom" type="text" class="form-control" />
                <form:errors path="prenom" cssClass="text-danger small mt-1" />
              </div>
            </div>

            <div class="mb-3">
              <form:label path="email" class="form-label fw-bold"><spring:message code="user.email"/></form:label>
              <form:input path="email" type="email" class="form-control" />
              <form:errors path="email" cssClass="text-danger small mt-1" />
            </div>

            <div class="mb-3">
              <form:label path="telephone" class="form-label fw-bold"><spring:message code="user.phone"/></form:label>
              <form:input path="telephone" type="text" class="form-control" />
              <form:errors path="telephone" cssClass="text-danger small mt-1" />
            </div>

            <div class="mb-3">
              <form:label path="adresse" class="form-label fw-bold"><spring:message code="user.address"/></form:label>
              <form:input path="adresse" type="text" class="form-control" />
              <form:errors path="adresse" cssClass="text-danger small mt-1" />
            </div>

            <div class="row g-3 mb-4">
              <div class="col-md-4">
                <form:label path="codePostal" class="form-label fw-bold"><spring:message code="user.postalCode"/></form:label>
                <form:input path="codePostal" type="text" class="form-control" />
                <form:errors path="codePostal" cssClass="text-danger small mt-1" />
              </div>
              <div class="col-md-8">
                <form:label path="localite" class="form-label fw-bold"><spring:message code="user.city"/></form:label>
                <form:input path="localite" type="text" class="form-control" />
                <form:errors path="localite" cssClass="text-danger small mt-1" />
              </div>
            </div>

            <div class="d-grid">
              <button type="submit" class="btn btn-primary btn-lg"><spring:message code="profile.btn.save"/></button>
            </div>
          </form:form>
        </div>
      </div>
    </div>
  </div>
</div>
