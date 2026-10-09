<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${vehicle.brand} ${vehicle.model}"/>
<c:set var="activeNav" value="browse"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>
<c:set var="iconType" value="${vehicle.type}"/>

<div class="page-header">
    <div>
        <h1><c:out value="${vehicle.brand} ${vehicle.model}"/></h1>
        <p class="subtitle"><c:out value="${vehicle.displayDetails()}"/></p>
    </div>
    <div class="d-flex gap-2">
        <c:if test="${me.canModifyVehicles()}">
            <a href="${ctx}/admin/vehicles/edit?id=${vehicle.id}" class="btn btn-outline-primary"><i class="bi bi-pencil me-1"></i>Edit</a>
        </c:if>
        <a href="${ctx}/vehicles" class="btn btn-outline-secondary"><i class="bi bi-arrow-left me-1"></i>All vehicles</a>
    </div>
</div>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="card h-100">
            <div class="card-body p-4">
                <div class="d-flex align-items-center gap-3 mb-4">
                    <span class="stat-card"><span class="stat-icon" style="width:64px;height:64px;font-size:2rem;">
                        <%@ include file="/WEB-INF/views/includes/vehicle-icon.jspf" %></span></span>
                    <div>
                        <span class="badge bg-brand-soft text-brand mb-1"><c:out value="${vehicle.type}"/></span>
                        <div class="text-muted small">Vehicle ID <c:out value="${vehicle.id}"/></div>
                    </div>
                </div>
                <dl class="row mb-0">
                    <dt class="col-sm-4 text-muted fw-normal">Brand</dt><dd class="col-sm-8"><c:out value="${vehicle.brand}"/></dd>
                    <dt class="col-sm-4 text-muted fw-normal">Model</dt><dd class="col-sm-8"><c:out value="${vehicle.model}"/></dd>
                    <dt class="col-sm-4 text-muted fw-normal">Year</dt><dd class="col-sm-8"><c:out value="${vehicle.year}"/></dd>
                    <dt class="col-sm-4 text-muted fw-normal"><c:out value="${vehicle.specLabel}"/></dt>
                    <dd class="col-sm-8"><c:out value="${vehicle.specValue}"/></dd>
                    <dt class="col-sm-4 text-muted fw-normal">Status</dt>
                    <dd class="col-sm-8">
                        <c:choose>
                            <c:when test="${vehicle.available}"><span class="badge text-bg-success">Available</span></c:when>
                            <c:otherwise><span class="badge text-bg-secondary">Unavailable</span></c:otherwise>
                        </c:choose>
                    </dd>
                </dl>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="card h-100">
            <div class="card-header py-3"><i class="bi bi-cash-coin me-1"></i>Pricing</div>
            <div class="card-body p-4">
                <div class="d-flex justify-content-between mb-2">
                    <span class="text-muted">Base daily rate</span>
                    <span>Rs. <fmt:formatNumber value="${vehicle.baseDailyRate}" pattern="#,##0.00"/></span>
                </div>
                <div class="d-flex justify-content-between border-top pt-2">
                    <span class="fw-semibold">You pay per day</span>
                    <span class="price fs-4 fw-bold text-brand">Rs. <fmt:formatNumber value="${vehicle.calculateDailyRate()}" pattern="#,##0.00"/></span>
                </div>
                <p class="small text-muted mt-3 mb-0">The daily rate includes the <c:out value="${fn:toLowerCase(vehicle.type)}"/> pricing rule.</p>
                <c:if test="${vehicle.available}">
                    <a href="${ctx}/rentals/new?vehicleId=${vehicle.id}" class="btn btn-primary w-100 mt-3">
                        <i class="bi bi-calendar-plus me-1"></i>Book this vehicle</a>
                </c:if>
            </div>
        </div>
        <div class="card mt-4">
            <div class="card-header py-3"><i class="bi bi-calendar3 me-1"></i>Booked dates</div>
            <c:choose>
                <c:when test="${empty upcoming}">
                    <div class="card-body small text-muted">No upcoming bookings.</div>
                </c:when>
                <c:otherwise>
                    <ul class="list-group list-group-flush small">
                        <c:forEach var="u" items="${upcoming}">
                            <li class="list-group-item"><c:out value="${u.startDate}"/> &rarr; <c:out value="${u.endDate}"/></li>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<div class="card mt-4">
    <div class="card-header py-3 d-flex flex-wrap justify-content-between align-items-center gap-2">
        <span><i class="bi bi-chat-square-text me-1"></i>Reviews
            <c:if test="${not empty average}">
                <c:set var="starsValue" value="${average}"/>
                <span class="ms-2"><%@ include file="/WEB-INF/views/includes/stars.jspf" %></span>
                <span class="fw-normal text-muted small">${average} / 5 (${reviewCount})</span>
            </c:if>
        </span>
        <span class="d-flex gap-2">
            <c:if test="${not me.canAccessAdminPages()}">
                <a href="${ctx}/reviews/new?vehicleId=${vehicle.id}" class="btn btn-sm btn-primary"><i class="bi bi-pencil-square me-1"></i>Write a review</a>
            </c:if>
            <a href="${ctx}/reviews?vehicleId=${vehicle.id}" class="btn btn-sm btn-outline-secondary">All reviews</a>
        </span>
    </div>
    <c:choose>
        <c:when test="${empty latestReviews}">
            <div class="card-body text-muted small">No reviews yet - be the first after your rental.</div>
        </c:when>
        <c:otherwise>
            <ul class="list-group list-group-flush">
                <c:forEach var="r" items="${latestReviews}">
                    <c:set var="starsValue" value="${r.rating}"/>
                    <li class="list-group-item">
                        <div class="d-flex justify-content-between">
                            <span><%@ include file="/WEB-INF/views/includes/stars.jspf" %>
                                <span class="badge ms-1 ${r.verified ? 'text-bg-success' : 'text-bg-light border'}"><c:out value="${r.displayLabel()}"/></span></span>
                            <span class="small text-muted"><c:out value="${r.date}"/></span>
                        </div>
                        <div class="mt-1"><c:out value="${r.comment}"/></div>
                        <div class="small text-muted">&mdash; <c:out value="${empty usersById[r.customerId] ? r.customerId : usersById[r.customerId].name}"/></div>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
