<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Cancel booking"/>
<c:set var="activeNav" value="rentals"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="card confirm-card">
    <div class="card-body p-4">
        <h1 class="h4 mb-3"><i class="bi bi-calendar-x text-danger me-2"></i>Cancel booking <c:out value="${rental.id}"/>?</h1>
        <p class="text-muted">The booking stays in your history with status CANCELLED and the dates become free for others.</p>
        <dl class="row mb-4">
            <dt class="col-4">Vehicle</dt>
            <dd class="col-8"><c:out value="${empty vehicle ? rental.vehicleId : vehicle.displayDetails()}"/></dd>
            <dt class="col-4">Dates</dt><dd class="col-8"><c:out value="${rental.startDate}"/> &rarr; <c:out value="${rental.endDate}"/> (${rental.days} days)</dd>
            <dt class="col-4">Estimated cost</dt><dd class="col-8">Rs. <fmt:formatNumber value="${cost}" pattern="#,##0.00"/></dd>
        </dl>
        <form action="${ctx}/rentals/cancel" method="post" class="d-flex gap-2">
            <input type="hidden" name="id" value="${fn:escapeXml(rental.id)}">
            <button type="submit" class="btn btn-danger"><i class="bi bi-x-circle me-1"></i>Yes, cancel booking</button>
            <a href="${ctx}/rentals/view?id=${rental.id}" class="btn btn-outline-secondary">Keep booking</a>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
