<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin dashboard"/>
<c:set var="activeNav" value="dashboard"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Admin dashboard</h1>
        <p class="subtitle">Bookings, money, fleet and people at a glance. Select any tile to open the full list.</p>
    </div>
    <div class="d-flex flex-wrap gap-2">
        <a href="${ctx}/admin/vehicles/add" class="btn btn-primary"><i class="bi bi-plus-lg me-1"></i>Add vehicle</a>
        <a href="${ctx}/admin/payments/generate" class="btn btn-outline-primary"><i class="bi bi-receipt me-1"></i>Generate bill</a>
    </div>
</div>

<h2 class="section-title">Rentals and money</h2>
<div class="row g-3 mb-4">
    <div class="col-sm-6 col-xl-3">
        <a href="${ctx}/payments?status=PAID" class="card stat-card stat-card-lead h-100">
            <div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-cash-stack"></i></span>
                <div>
                    <div class="stat-value">Rs. <fmt:formatNumber value="${paidTotal}" pattern="#,##0"/></div>
                    <div class="stat-label">Revenue collected</div>
                </div>
            </div>
        </a>
    </div>
    <div class="col-sm-6 col-xl-3">
        <a href="${ctx}/payments?status=PENDING" class="card stat-card h-100">
            <div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-hourglass-split"></i></span>
                <div>
                    <div class="stat-value">Rs. <fmt:formatNumber value="${unpaidTotal}" pattern="#,##0"/></div>
                    <div class="stat-label">Awaiting payment</div>
                </div>
            </div>
        </a>
    </div>
    <div class="col-sm-6 col-xl-3">
        <a href="${ctx}/rentals?status=ACTIVE" class="card stat-card h-100">
            <div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-calendar-check"></i></span>
                <div><div class="stat-value">${activeRentalCount}</div><div class="stat-label">Active rentals</div></div>
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

<h2 class="section-title">Fleet</h2>
<div class="row g-3 mb-4 row-cols-1 row-cols-sm-2 row-cols-xl-5">
    <div class="col">
        <a href="${ctx}/admin/vehicles" class="card stat-card h-100">
            <div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-car-front"></i></span>
                <div>
                    <div class="stat-value">${vehicleCount}</div>
                    <div class="stat-label">All vehicles</div>
                </div>
            </div>
        </a>
    </div>
    <div class="col">
        <a href="${ctx}/admin/vehicles?availability=available" class="card stat-card h-100">
            <div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-check2-circle"></i></span>
                <div><div class="stat-value">${availableVehicleCount}</div><div class="stat-label">In service</div></div>
            </div>
        </a>
    </div>
    <c:forEach var="e" items="${vehicleTypeCounts}">
        <div class="col">
            <a href="${ctx}/admin/vehicles?type=${e.key}" class="card stat-card h-100">
                <div class="card-body d-flex align-items-center gap-3">
                    <c:set var="iconType" value="${e.key}"/>
                    <span class="stat-icon"><%@ include file="/WEB-INF/views/includes/vehicle-icon.jspf" %></span>
                    <div><div class="stat-value">${e.value}</div>
                        <div class="stat-label">${e.key == 'CAR' ? 'Cars' : (e.key == 'BIKE' ? 'Bikes' : 'Vans')}</div></div>
                </div>
            </a>
        </div>
    </c:forEach>
</div>

<h2 class="section-title">People</h2>
<div class="row g-3">
    <div class="col-sm-6 col-xl-3">
        <a href="${ctx}/admin/users?role=CUSTOMER" class="card stat-card h-100">
            <div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-people"></i></span>
                <div><div class="stat-value">${customerCount}</div><div class="stat-label">Customers</div></div>
            </div>
        </a>
    </div>
    <div class="col-sm-6 col-xl-3">
        <a href="${ctx}/admin/users?role=ADMIN" class="card stat-card h-100">
            <div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-shield-lock"></i></span>
                <div><div class="stat-value">${adminCount}</div><div class="stat-label">Administrators</div></div>
            </div>
        </a>
    </div>
    <c:forEach var="e" items="${staffTypeCounts}">
        <div class="col-sm-6 col-xl-3">
            <a href="${ctx}/admin/staff?type=${e.key}" class="card stat-card h-100">
                <div class="card-body d-flex align-items-center gap-3">
                    <span class="stat-icon"><i class="bi ${e.key == 'DRIVER' ? 'bi-person-vcard' : 'bi-wrench-adjustable'}"></i></span>
                    <div><div class="stat-value">${e.value}</div><div class="stat-label">${e.key == 'DRIVER' ? 'Drivers' : 'Mechanics'}</div></div>
                </div>
            </a>
        </div>
    </c:forEach>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
