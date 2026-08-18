<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/importTags.jsp" %>
<div class="row align-items-center rounded-3 overflow-hidden shadow-lg mb-4 bg-white">
    <div class="col-lg-7 p-0 overflow-hidden joggin-hero-image">
        <img src="<spring:url value='/images/home_hero.png'/>" class="w-100 joggin-image-couvrante"
             alt="<spring:message code='home.hero.alt'/>">
    </div>
    <div class="col-lg-5 p-4 p-lg-5">
        <h1 class="display-5 fw-bold lh-1 mb-3"><spring:message code="home.welcome.title"/></h1>
        <p class="lead"><spring:message code="home.welcome.text"/></p>
        <div class="d-grid gap-2 d-md-flex justify-content-md-start mb-4 mb-lg-3">
            <a href="<spring:url value='/produits'/>" class="btn btn-primary btn-lg px-4 me-md-2 fw-bold"><spring:message code="home.btn.catalog"/></a>
        </div>
    </div>
</div>

<div class="row align-items-md-stretch">
    <div class="col-md-6 mb-4">
        <div class="h-100 p-5 text-white bg-dark rounded-3 d-flex flex-column justify-content-center">
            <h2><spring:message code="home.news.title"/></h2>
            <p><spring:message code="home.news.text"/></p>
            <div class="mt-auto">
                <a href="<spring:url value='/produits/nouveautes'/>" class="btn btn-outline-light"><spring:message code="home.news.btn"/></a>
            </div>
        </div>
    </div>
    <div class="col-md-6 mb-4">
        <div class="h-100 border rounded-3 overflow-hidden position-relative text-white">
            <img src="<spring:url value='/images/promo.png'/>"
                 class="w-100 h-100 position-absolute top-0 start-0 joggin-image-promo"
                 alt="<spring:message code='home.promo.alt'/>">
            <div class="position-relative p-5 h-100 d-flex flex-column justify-content-center">
                <h2 class="display-6 fw-bold"><spring:message code="home.promo.title"/></h2>
                <p class="fs-5"><spring:message code="home.promo.text"/></p>
                <div class="mt-auto">
                    <a href="<spring:url value='/produits/promotions'/>" class="btn btn-light"><spring:message code="home.promo.btn"/></a>
                </div>
            </div>
        </div>
    </div>
</div>
