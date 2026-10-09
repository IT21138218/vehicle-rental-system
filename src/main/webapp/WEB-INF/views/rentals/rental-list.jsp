<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Rentals"/>
<c:set var="activeNav" value="rentals"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>
<c:set var="isAdmin" value="${me.canAccessAdminPages()}"/>

<div class="page-header">
    <div>
        <h1>${isAdmin ? 'All rentals' : 'My rentals'}</h1>
        <p class="subtitle">${isAdmin ? 'Every booking on the platform.' : 'Your bookings, newest first.'}</p>
    </div>
    <c:if test="${not isAdmin}">
        <a href="${ctx}/vehicles" class="btn btn-primary"><i class="bi bi-plus-lg me-1"></i>New booking</a>
    </c:if>
</div>

<ul class="nav nav-pills mb-3 gap-1">
    <li class="nav-item"><a class="nav-link ${empty param.status ? 'active' : ''}" href="${ctx}/rentals">All</a></li>
    <li class="nav-item"><a class="nav-link ${param.status == 'ACTIVE' ? 'active' : ''}" href="${ctx}/rentals?status=ACTIVE">Active</a></li>
    <li class="nav-item"><a class="nav-link ${param.status == 'RETURNED' ? 'active' : ''}" href="${ctx}/rentals?status=RETURNED">Returned</a></li>
    <li class="nav-item"><a class="nav-link ${param.status == 'CANCELLED' ? 'active' : ''}" href="${ctx}/rentals?status=CANCELLED">Cancelled</a></li>
</ul>

<div class="card">
    <c:choose>
        <c:when test="${empty rentals}">
            <div class="empty-state"><i class="bi bi-calendar-x"></i>No bookings here yet.</div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-hover table-app">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <c:if test="${isAdmin}"><th>Customer</th></c:if>
                        <th>Vehicle</th><th>From</th><th>To</th><th class="text-end">Days</th>
                        <th class="text-end">Est. cost</th><th>Status</th><th class="text-end">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="r" items="${rentals}">
                        <c:set var="v" value="${vehiclesById[r.vehicleId]}"/>
                        <c:set var="badgeStatus" value="${r.status}"/>
                        <tr>
                            <td class="fw-semibold"><c:out value="${r.id}"/></td>
                            <c:if test="${isAdmin}">
                                <td><c:out value="${empty usersById[r.customerId] ? r.customerId : usersById[r.customerId].name}"/></td>
                            </c:if>
                            <td>
                                <c:choose>
                                    <c:when test="${empty v}"><span class="text-muted"><c:out value="${r.vehicleId}"/> (removed)</span></c:when>
                                    <c:otherwise><c:out value="${v.brand} ${v.model}"/></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-nowrap"><c:out value="${r.startDate}"/></td>
                            <td class="text-nowrap"><c:out value="${r.endDate}"/></td>
                            <td class="text-end"><c:out value="${r.days}"/></td>
                            <td class="text-end text-nowrap">Rs. <fmt:formatNumber value="${costs[r.id]}" pattern="#,##0.00"/></td>
                            <td><%@ include file="/WEB-INF/views/includes/rental-status.jspf" %></td>
                            <td class="text-end text-nowrap">
                                <a href="${ctx}/rentals/view?id=${r.id}" class="btn btn-sm btn-outline-secondary"><i class="bi bi-eye"></i> View</a>
                                <c:if test="${r.active}">
                                    <a href="${ctx}/rentals/edit?id=${r.id}" class="btn btn-sm btn-outline-primary"><i class="bi bi-calendar-event"></i> Dates</a>
                                    <a href="${ctx}/rentals/cancel?id=${r.id}" class="btn btn-sm btn-outline-danger"><i class="bi bi-x-circle"></i> Cancel</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
