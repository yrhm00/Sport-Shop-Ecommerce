<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../include/importTags.jsp" %>
<nav class="navbar navbar-expand-lg navbar-light bg-light rounded mb-3">
    <div class="container-fluid">
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="<spring:url value='/'/>"><spring:message code="menu.home"/></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<spring:url value='/produits'/>"><spring:message code="menu.catalog"/></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<spring:url value='/a-propos'/>"><spring:message code="menu.about"/></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<spring:url value='/panier'/>">
                        <spring:message code="menu.cart"/>
                        <c:if test="${sessionScope.cart != null && sessionScope.cart.totalItems > 0}">
                            <span class="badge bg-primary"><c:out value="${sessionScope.cart.totalItems}"/></span>
                        </c:if>
                    </a>
                </li>
                <sec:authorize access="isAuthenticated()">
                    <li class="nav-item">
                        <a class="nav-link" href="<spring:url value='/commandes/mes-commandes'/>"><spring:message code="menu.orders"/></a>
                    </li>
                </sec:authorize>
            </ul>

            <div class="d-flex">
                <%-- Utilisateur authentifie : message personnalise, profil, deconnexion --%>
                <sec:authorize access="isAuthenticated()">
                    <span class="navbar-text me-3">
                        <spring:message code="menu.welcome"/>,
                        <c:out value="${pageContext.request.userPrincipal.principal.prenom}"/>
                        (<c:out value="${pageContext.request.userPrincipal.name}"/>)
                    </span>
                    <a href="<spring:url value='/profil'/>" class="btn btn-outline-primary me-2"><spring:message code="menu.profile"/></a>
                    <%-- Deconnexion en POST avec jeton CSRF (une URL visitee ne doit pas deconnecter) --%>
                    <form method="post" action="<spring:url value='/deconnexion'/>" class="d-inline">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button type="submit" class="btn btn-outline-danger"><spring:message code="menu.logout"/></button>
                    </form>
                </sec:authorize>

                <%-- Visiteur non authentifie : connexion et inscription --%>
                <sec:authorize access="!isAuthenticated()">
                    <span class="navbar-text me-3">
                        <spring:message code="menu.welcome"/>, <spring:message code="menu.visitor"/>
                    </span>
                    <a href="<spring:url value='/connexion'/>" class="btn btn-outline-success me-2"><spring:message code="menu.login"/></a>
                    <a href="<spring:url value='/inscription'/>" class="btn btn-primary"><spring:message code="menu.register"/></a>
                </sec:authorize>
            </div>
        </div>
    </div>
</nav>
