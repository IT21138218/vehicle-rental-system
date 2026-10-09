<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Vehicles"/>
<c:set var="activeNav" value="vehicles"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Vehicle fleet</h1>
        <p class="subtitle">
            <c:forEach var="entry" items="${typeCounts}" varStatus="s">
                <c:out value="${entry.value}"/> <c:out value="${fn:toLowerCase(entry.key)}"/>s<c:if test="${not s.last}"> &middot; </c:if>
            </c:forEach>
        </p>
    </div>
    <a href="${ctx}/admin/vehicles/add" class="btn btn-primary"><i class="bi bi-plus-lg me-1"></i>Add vehicle</a>
</div>

<div class="card mb-4">
    <div class="card-body">
        <form method="get" action="${ctx}/admin/vehicles" class="row g-2 align-items-end">
            <div class="col-md-5">
                <label for="q" class="form-label small text-muted">Search</label>
                <input type="search" class="form-control" id="q" name="q" placeholder="ID, brand or model"
                       value="${fn:escapeXml(param.q)}">
            </div>
            <div class="col-md-2">
                <label for="type" class="form-label small text-muted">Type</label>
                <select class="form-select" id="type" name="type">
                    <option value="">All types</option>
                    <option value="CAR" ${param.type == 'CAR' ? 'selected' : ''}>Car</option>
                    <option value="BIKE" ${param.type == 'BIKE' ? 'selected' : ''}>Bike</option>
                    <option value="VAN" ${param.type == 'VAN' ? 'selected' : ''}>Van</option>
                </select>
            </div>
            <div class="col-md-2">
                <label for="availability" class="form-label small text-muted">Availability</label>
                <select class="form-select" id="availability" name="availability">
                    <option value="">Any</option>
                    <option value="available" ${param.availability == 'available' ? 'selected' : ''}>Available</option>
                    <option value="unavailable" ${param.availability == 'unavailable' ? 'selected' : ''}>Unavailable</option>
                </select>
            </div>
            <div class="col-md-3 d-flex gap-2">
                <button type="submit" class="btn btn-primary flex-grow-1"><i class="bi bi-funnel me-1"></i>Filter</button>
                <a href="${ctx}/admin/vehicles" class="btn btn-outline-secondary">Reset</a>
            </div>
        </form>
    </div>
</div>

<div class="card">
    <div class="card-header py-3 d-flex justify-content-between">
        <span>Vehicles</span>
        <span class="text-muted fw-normal small">${fn:length(vehicles)} found</span>
    </div>
    <c:choose>
        <c:when test="${empty vehicles}">
            <div class="empty-state"><i class="bi bi-car-front"></i>No vehicles match your filters.</div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-hover table-app">
                    <thead>
                    <tr>
                        <th>ID</th><th>Vehicle</th><th>Type</th><th>Spec</th>
                        <th class="text-end">Base rate</th><th class="text-end">Daily rate</th>
                        <th>Status</th><th class="text-end">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="v" items="${vehicles}">
                        <c:set var="iconType" value="${v.type}"/>
                        <tr>
                            <td class="fw-semibold"><c:out value="${v.id}"/></td>
                            <td>
                                <div class="fw-semibold"><c:out value="${v.brand} ${v.model}"/></div>
                                <div class="small text-muted"><c:out value="${v.year}"/></div>
                            </td>
                            <td><span class="text-brand me-1"><%@ include file="/WEB-INF/views/includes/vehicle-icon.jspf" %></span><c:out value="${v.type}"/></td>
                            <td class="text-nowrap"><c:out value="${v.specLabel}"/>: <c:out value="${v.specValue}"/></td>
                            <td class="text-end text-muted">Rs. <fmt:formatNumber value="${v.baseDailyRate}" pattern="#,##0.00"/></td>
                            <%-- calculateDailyRate() is polymorphic: each type applies its own rule --%>
                            <td class="text-end fw-semibold">Rs. <fmt:formatNumber value="${v.calculateDailyRate()}" pattern="#,##0.00"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${v.available}"><span class="badge text-bg-success">Available</span></c:when>
                                    <c:otherwise><span class="badge text-bg-secondary">Unavailable</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-end text-nowrap">
                                <a href="${ctx}/vehicles/view?id=${v.id}" class="btn btn-sm btn-outline-secondary" title="View"><i class="bi bi-eye"></i></a>
                                <a href="${ctx}/admin/vehicles/edit?id=${v.id}" class="btn btn-sm btn-outline-primary"><i class="bi bi-pencil"></i> Edit</a>
                                <a href="${ctx}/admin/vehicles/delete?id=${v.id}" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i> Delete</a>
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
