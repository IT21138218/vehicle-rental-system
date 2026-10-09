<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My dashboard"/>
<c:set var="activeNav" value="dashboard"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Hello, <c:out value="${me.name}"/></h1>
        <p class="subtitle">Find a vehicle, manage your bookings and check your bills.</p>
    </div>
</div>

<div class="row g-3 mb-4">
    <div class="col-sm-6 col-xl-3">
        <a href="${ctx}/rentals?status=ACTIVE" class="card stat-card h-100">
            <div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-calendar-check"></i></span>
                <div><div class="stat-value">${activeRentalCount}</div><div class="stat-label">Active bookings</div></div>
            </div>
        </a>
    </div>
    <div class="col-sm-6 col-xl-3">
        <a href="${ctx}/rentals?status=RETURNED" class="card stat-card h-100">
            <div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-check2-all"></i></span>
                <div><div class="stat-value">${returnedRentalCount}</div><div class="stat-label">Completed rentals</div></div>
            </div>
        </a>
    </div>
</div>

<div class="card mb-4">
    <div class="card-header py-3">Upcoming and current bookings</div>
    <c:choose>
        <c:when test="${empty activeRentals}">
            <div class="empty-state"><i class="bi bi-calendar"></i>No active bookings. <a href="${ctx}/vehicles">Find a vehicle</a></div>
        </c:when>
        <c:otherwise>
            <ul class="list-group list-group-flush">
                <c:forEach var="r" items="${activeRentals}">
                    <c:set var="v" value="${vehiclesById[r.vehicleId]}"/>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <span><strong><c:out value="${r.id}"/></strong> &middot;
                            <c:out value="${empty v ? r.vehicleId : v.brand}"/> <c:out value="${empty v ? '' : v.model}"/> &middot;
                            <span class="text-muted"><c:out value="${r.startDate}"/> &rarr; <c:out value="${r.endDate}"/></span></span>
                        <a href="${ctx}/rentals/view?id=${r.id}" class="btn btn-sm btn-outline-secondary">View</a>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
</div>

<div class="card">
    <div class="card-header py-3">Quick actions</div>
    <div class="card-body d-flex flex-wrap gap-2">
        <a href="${ctx}/vehicles" class="btn btn-primary"><i class="bi bi-search me-1"></i>Browse vehicles</a>
        <a href="${ctx}/rentals" class="btn btn-outline-primary"><i class="bi bi-calendar-check me-1"></i>My rentals</a>
        <a href="${ctx}/payments" class="btn btn-outline-primary"><i class="bi bi-receipt me-1"></i>My bills</a>
        <a href="${ctx}/profile" class="btn btn-outline-primary"><i class="bi bi-person-gear me-1"></i>Edit my profile</a>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
