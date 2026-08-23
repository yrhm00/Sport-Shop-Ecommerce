<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/importTags.jsp" %>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-8 col-xl-7">
            <div class="card shadow border-0 rounded-3">
                <div class="card-body p-4 p-md-5">
                    <div class="text-center mb-4">
                        <h2 class="fw-bold text-primary"><spring:message code="register.title"/></h2>
                        <p class="text-muted"><spring:message code="register.subtitle"/></p>
                    </div>

                    <!-- Erreur Globale -->
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger d-flex align-items-center mb-4 text-center" role="alert">
                            <spring:message code="${errorMessage}"/>
                        </div>
                    </c:if>

                    <form:form method="post" action="${pageContext.request.contextPath}/inscription" modelAttribute="inscriptionForm" class="row g-3">
                        
                        <div class="col-12">
                            <h5 class="text-secondary border-bottom pb-2 mb-3"><spring:message code="register.accountDetails"/></h5>
                        </div>

                        <div class="col-12">
                            <div class="form-floating">
                                <form:input path="username" type="text" class="form-control" id="reg-username" placeholder="Username" />
                                <form:label path="username" for="reg-username"><spring:message code="user.username"/></form:label>
                            </div>
                            <form:errors path="username" cssClass="text-danger small ms-1" />
                        </div>

                        <div class="col-md-6">
                            <div class="form-floating">
                                <form:input path="password" type="password" class="form-control" id="reg-pass" placeholder="Password" />
                                <form:label path="password" for="reg-pass"><spring:message code="user.password"/></form:label>
                            </div>
                            <form:errors path="password" cssClass="text-danger small ms-1" />
                        </div>

                        <div class="col-md-6">
                            <div class="form-floating">
                                <form:input path="confirmPassword" type="password" class="form-control" id="reg-confirm" placeholder="Confirm" />
                                <form:label path="confirmPassword" for="reg-confirm"><spring:message code="user.confirmPassword"/></form:label>
                            </div>
                            <form:errors path="confirmPassword" cssClass="text-danger small ms-1" />
                        </div>

                        <div class="col-12 mt-4">
                            <h5 class="text-secondary border-bottom pb-2 mb-3"><spring:message code="register.personalInfo"/></h5>
                        </div>

                        <div class="col-md-6">
                            <div class="form-floating">
                                <form:input path="nom" type="text" class="form-control" id="reg-nom" placeholder="Nom" />
                                <form:label path="nom" for="reg-nom"><spring:message code="user.lastname"/></form:label>
                            </div>
                            <form:errors path="nom" cssClass="text-danger small ms-1" />
                        </div>

                        <div class="col-md-6">
                            <div class="form-floating">
                                <form:input path="prenom" type="text" class="form-control" id="reg-prenom" placeholder="Prenom" />
                                <form:label path="prenom" for="reg-prenom"><spring:message code="user.firstname"/></form:label>
                            </div>
                            <form:errors path="prenom" cssClass="text-danger small ms-1" />
                        </div>

                        <div class="col-md-6">
                            <div class="form-floating">
                                <form:input path="email" type="email" class="form-control" id="reg-email" placeholder="Email" />
                                <form:label path="email" for="reg-email"><spring:message code="user.email"/></form:label>
                            </div>
                            <form:errors path="email" cssClass="text-danger small ms-1" />
                        </div>

                        <div class="col-md-6">
                            <div class="form-floating">
                                <form:input path="telephone" type="tel" class="form-control" id="reg-tel" placeholder="Tel" />
                                <form:label path="telephone" for="reg-tel"><spring:message code="user.phone"/></form:label>
                            </div>
                            <form:errors path="telephone" cssClass="text-danger small ms-1" />
                        </div>

                        <div class="col-12">
                            <div class="form-floating">
                                <form:input path="adresse" class="form-control" id="reg-addr" placeholder="Address" />
                                <form:label path="adresse" for="reg-addr"><spring:message code="user.address"/></form:label>
                            </div>
                            <form:errors path="adresse" cssClass="text-danger small ms-1" />
                        </div>

                        <div class="col-md-4">
                            <div class="form-floating">
                                <form:input path="codePostal" type="text" class="form-control" id="reg-zip" placeholder="Zip" />
                                <form:label path="codePostal" for="reg-zip"><spring:message code="user.zip"/></form:label>
                            </div>
                            <form:errors path="codePostal" cssClass="text-danger small ms-1" />
                        </div>

                        <div class="col-md-8">
                            <div class="form-floating">
                                <form:input path="localite" type="text" class="form-control" id="reg-city" placeholder="City" />
                                <form:label path="localite" for="reg-city"><spring:message code="user.city"/></form:label>
                            </div>
                            <form:errors path="localite" cssClass="text-danger small ms-1" />
                        </div>

                        <div class="col-12 mt-4 d-flex justify-content-between align-items-center">
                            <a href="<spring:url value='/'/>" class="text-decoration-none text-secondary"><spring:message code="btn.cancel"/></a>
                            <button type="submit" class="btn btn-primary btn-lg px-5"><spring:message code="btn.register"/></button>
                        </div>
                    </form:form>
                </div>
                <div class="card-footer py-3 border-0 text-center bg-light rounded-bottom-3">
                    <span class="text-muted"><spring:message code="register.alreadyAccount"/></span> <a href="<spring:url value='/connexion'/>" class="fw-bold text-decoration-none ms-1"><spring:message code="register.login"/></a>
                </div>
            </div>
        </div>
    </div>
</div>
