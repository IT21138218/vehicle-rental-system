<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Delete vehicle"/>
<c:set var="activeNav" value="vehicles"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="card confirm-card">
    <div class="card-body p-4">
        <h1 class="h4 mb-3"><i class="bi bi-exclamation-octagon text-danger me-2"></i>Delete this vehicle?</h1>
        <p class="text-muted">This permanently removes the vehicle from vehicles.txt. It cannot be undone.</p>
        <dl class="row mb-4">
            <dt class="col-4">ID</dt><dd class="col-8"><c:out value="${vehicle.id}"/></dd>
            <dt class="col-4">Vehicle</dt><dd class="col-8"><c:out value="${vehicle.displayDetails()}"/></dd>
            <dt class="col-4">Daily rate</dt>
            <dd class="col-8">Rs. <fmt:formatNumber value="${vehicle.calculateDailyRate()}" pattern="#,##0.00"/></dd>
            <dt class="col-4">Status</dt><dd class="col-8">${vehicle.available ? 'Available' : 'Unavailable'}</dd>
        </dl>
        <form action="${ctx}/admin/vehicles/delete" method="post" class="d-flex gap-2">
            <input type="hidden" name="id" value="${fn:escapeXml(vehicle.id)}">
            <button type="submit" class="btn btn-danger"><i class="bi bi-trash me-1"></i>Yes, delete</button>
            <a href="${ctx}/admin/vehicles" class="btn btn-outline-secondary">Cancel</a>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
