<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="forVehicle" value="${not empty vehicle}"/>
<c:set var="isAdmin" value="${sessionScope.currentUser.canAccessAdminPages()}"/>
<c:set var="pageTitle" value="${forVehicle ? 'Reviews' : (isAdmin ? 'All reviews' : 'My reviews')}"/>
<c:set var="activeNav" value="${forVehicle ? 'browse' : 'reviews'}"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <c:choose>
            <c:when test="${forVehicle}">
                <h1>Reviews for <c:out value="${vehicle.brand} ${vehicle.model}"/></h1>
                <p class="subtitle">
                    <c:choose>
                        <c:when test="${empty average}">No reviews yet.</c:when>
                        <c:otherwise>
                            <c:set var="starsValue" value="${average}"/>
                            <%@ include file="/WEB-INF/views/includes/stars.jspf" %>
                            <strong class="ms-1">${average}</strong> / 5 from ${fn:length(reviews)} review(s)
                        </c:otherwise>
                    </c:choose>
                </p>
            </c:when>
            <c:otherwise>
                <h1>${isAdmin ? 'All reviews' : 'My reviews'}</h1>
                <p class="subtitle">${isAdmin ? 'Moderate customer feedback.' : 'Reviews you have written.'}</p>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="d-flex gap-2">
        <c:if test="${forVehicle and not isAdmin and empty myReview}">
            <a href="${ctx}/reviews/new?vehicleId=${vehicle.id}" class="btn btn-primary"><i class="bi bi-pencil-square me-1"></i>Write a review</a>
        </c:if>
        <c:if test="${forVehicle}">
            <a href="${ctx}/vehicles/view?id=${vehicle.id}" class="btn btn-outline-secondary"><i class="bi bi-arrow-left me-1"></i>Vehicle</a>
        </c:if>
    </div>
</div>

<c:choose>
    <c:when test="${empty reviews}">
        <div class="card"><div class="empty-state"><i class="bi bi-chat-square-text"></i>No reviews to show.</div></div>
    </c:when>
    <c:otherwise>
        <div class="row g-3">
            <c:forEach var="r" items="${reviews}">
                <c:set var="author" value="${usersById[r.customerId]}"/>
                <c:set var="rv" value="${vehiclesById[r.vehicleId]}"/>
                <c:set var="starsValue" value="${r.rating}"/>
                <div class="col-lg-6">
                    <div class="card h-100">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <div>
                                    <%@ include file="/WEB-INF/views/includes/stars.jspf" %>
                                    <%-- displayLabel() and isVerified() are polymorphic --%>
                                    <span class="badge ms-2 ${r.verified ? 'text-bg-success' : 'text-bg-light border'}">
                                        <c:if test="${r.verified}"><i class="bi bi-patch-check-fill me-1"></i></c:if><c:out value="${r.displayLabel()}"/>
                                    </span>
                                </div>
                                <span class="small text-muted"><c:out value="${r.date}"/></span>
                            </div>
                            <p class="mb-2"><c:out value="${r.comment}"/></p>
                            <div class="small text-muted d-flex flex-wrap justify-content-between gap-2 align-items-center">
                                <span>
                                    &mdash; <c:out value="${empty author ? r.customerId : author.name}"/>
                                    <c:if test="${not forVehicle}">
                                        on <a href="${ctx}/reviews?vehicleId=${r.vehicleId}"><c:out value="${empty rv ? r.vehicleId : rv.brand.concat(' ').concat(rv.model)}"/></a>
                                    </c:if>
                                    &middot; <c:out value="${r.id}"/>
                                </span>
                                <span class="d-flex gap-1">
                                    <c:if test="${r.customerId == me.id}">
                                        <a href="${ctx}/reviews/edit?id=${r.id}" class="btn btn-sm btn-outline-primary"><i class="bi bi-pencil"></i> Edit</a>
                                    </c:if>
                                    <c:if test="${isAdmin or r.customerId == me.id}">
                                        <a href="${ctx}/reviews/delete?id=${r.id}" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></a>
                                    </c:if>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
