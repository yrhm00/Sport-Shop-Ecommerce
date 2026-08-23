<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/importTags.jsp" %>

<div class="container my-5">
    <div class="row">
        <div class="col-lg-12">
            <h1 class="display-4 mb-4"><spring:message code="about.title"/></h1>
            
            <!-- Section Mission -->
            <div class="card mb-4 shadow-sm">
                <div class="card-body">
                    <h2 class="card-title h3 text-primary"><spring:message code="about.mission.title"/></h2>
                    <p class="card-text lead"><spring:message code="about.mission.text"/></p>
                </div>
            </div>
            
            <!-- Section Valeurs -->
            <div class="card mb-4 shadow-sm">
                <div class="card-body">
                    <h2 class="card-title h3 text-primary"><spring:message code="about.values.title"/></h2>
                    <ul class="list-unstyled">
                        <li class="mb-3">
                            <strong><spring:message code="about.values.quality"/></strong>
                        </li>
                        <li class="mb-3">
                            <strong><spring:message code="about.values.innovation"/></strong>
                        </li>
                        <li class="mb-3">
                            <strong><spring:message code="about.values.service"/></strong>
                        </li>
                    </ul>
                </div>
            </div>
            
            <!-- Section Histoire -->
            <div class="card mb-4 shadow-sm">
                <div class="card-body">
                    <h2 class="card-title h3 text-primary"><spring:message code="about.history.title"/></h2>
                    <p class="card-text"><spring:message code="about.history.text"/></p>
                </div>
            </div>
            
            <!-- Section Engagement -->
            <div class="card mb-4 shadow-sm">
                <div class="card-body">
                    <h2 class="card-title h3 text-primary"><spring:message code="about.commitment.title"/></h2>
                    <p class="card-text"><spring:message code="about.commitment.text"/></p>
                </div>
            </div>
            
            <!-- Appel à l'action -->
            <div class="text-center mt-5">
                <a href="<spring:url value='/produits'/>" class="btn btn-primary btn-lg">
                    <spring:message code="catalog.btn.view"/>
                </a>
            </div>
        </div>
    </div>
</div>
