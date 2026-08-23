<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/importTags.jsp" %>
<div class="container-fluid h-100">
  <div class="row justify-content-center align-items-center h-100">
    <!-- Colonne Image -->
    <div class="col-md-6 d-none d-md-block p-0 h-100 joggin-colonne-illustration">
      <img src="<spring:url value='/images/auth_sidebar.png'/>" alt="Login Visual" class="w-100 h-100 joggin-illustration">
    </div>
    
    <!-- Colonne Formulaire -->
    <div class="col-md-6 col-lg-5 col-xl-4 py-5">
      <div class="px-4 px-md-5">
        <div class="text-center mb-4">
            <h1 class="fw-bold"><spring:message code="login.title"/></h1>
            <p class="text-muted"><spring:message code="login.welcome"/></p>
        </div>

        <!-- Messages -->
        <c:if test="${param.inscriptionSuccess != null}">
            <div class="alert alert-success d-flex align-items-center" role="alert">
                <spring:message code="login.success.register"/>
            </div>
        </c:if>

        <c:if test="${param.logout != null}">
            <div class="alert alert-info d-flex align-items-center" role="alert">
                 <spring:message code="login.success.logout"/>
            </div>
        </c:if>

        <c:if test="${param.error != null}">
            <div class="alert alert-danger d-flex align-items-center" role="alert">
                <spring:message code="login.error.badCredentials"/>
            </div>
        </c:if>

        <!-- Formulaire -->
        <form method="post" action="<spring:url value='/connexion'/>" class="mt-4">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="form-floating mb-3">
                <input type="text" class="form-control" id="username" name="username" placeholder="name" required />
                <label for="username"><spring:message code="user.username"/></label>
            </div>
            
            <div class="form-floating mb-4">
                <input type="password" class="form-control" id="password" name="password" placeholder="Password" required />
                <label for="password"><spring:message code="user.password"/></label>
            </div>

            <div class="d-grid gap-2 mb-4">
                <button type="submit" class="btn btn-primary btn-lg shadow-sm"><spring:message code="login.btn"/></button>
            </div>
            
            <div class="text-center">
                <p class="mb-0"><spring:message code="login.newHere"/> <a href="<spring:url value='/inscription'/>" class="text-primary fw-bold text-decoration-none"><spring:message code="login.createAccount"/></a></p>
            </div>
        </form>
      </div>
    </div>
  </div>
</div>
