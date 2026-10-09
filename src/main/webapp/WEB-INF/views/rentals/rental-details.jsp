<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Booking ${rental.id}"/>
<c:set var="activeNav" value="rentals"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>
<c:set var="badgeStatus" value="${rental.status}"/>

<div class="page-header">
    <div>
        <h1>Booking <c:out value="${rental.id}"/> <%@ include file="/WEB-INF/views/includes/rental-status.jspf" %></h1>
        <p class="subtitle"><c:out value="${rental.startDate}"/> &rarr; <c:out value="${rental.endDate}"/> &middot; ${rental.days} day(s)</p>
    </div>
    <div class="d-flex flex-wrap gap-2">
        <c:if test="${rental.active}">
            <a href="${ctx}/rentals/edit?id=${rental.id}" class="btn btn-outline-primary"><i class="bi bi-calendar-event me-1"></i>Change dates</a>
            <a href="${ctx}/rentals/cancel?id=${rental.id}" class="btn btn-outline-danger"><i class="bi bi-x-circle me-1"></i>Cancel</a>
            <c:if test="${me.canAccessAdminPages()}">
                <form action="${ctx}/admin/rentals/return" method="post" class="m-0">
                    <input type="hidden" name="id" value="${fn:escapeXml(rental.id)}">
                    <button type="submit" class="btn btn-success"><i class="bi bi-box-arrow-in-down me-1"></i>Mark returned</button>
                </form>
            </c:if>
        </c:if>
        <a href="${ctx}/rentals" class="btn btn-outline-secondary"><i class="bi bi-arrow-left me-1"></i>All bookings</a>
    </div>
</div>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="card h-100">
            <div class="card-header py-3">Booking details</div>
            <div class="card-body">
                <dl class="row mb-0">
                    <dt class="col-sm-4 text-muted fw-normal">Customer</dt>
                    <dd class="col-sm-8"><c:out value="${empty customer ? rental.customerId : customer.name}"/>
                        <span class="text-muted small">(<c:out value="${rental.customerId}"/>)</span></dd>
                    <dt class="col-sm-4 text-muted fw-normal">Vehicle</dt>
                    <dd class="col-sm-8">
                        <c:choose>
                            <c:when test="${empty vehicle}"><c:out value="${rental.vehicleId}"/> (removed from fleet)</c:when>
                            <c:otherwise>
                                <a href="${ctx}/vehicles/view?id=${vehicle.id}"><c:out value="${vehicle.displayDetails()}"/></a>
                            </c:otherwise>
                        </c:choose>
                    </dd>
                    <dt class="col-sm-4 text-muted fw-normal">Pick-up</dt><dd class="col-sm-8"><c:out value="${rental.startDate}"/></dd>
                    <dt class="col-sm-4 text-muted fw-normal">Return by</dt><dd class="col-sm-8"><c:out value="${rental.endDate}"/></dd>
                    <dt class="col-sm-4 text-muted fw-normal">Status</dt><dd class="col-sm-8"><c:out value="${rental.status}"/></dd>
                </dl>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="card">
            <div class="card-header py-3"><i class="bi bi-cash-coin me-1"></i>Cost estimate</div>
            <div class="card-body">
                <c:if test="${not empty vehicle}">
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Daily rate</span>
                        <span>Rs. <fmt:formatNumber value="${vehicle.calculateDailyRate()}" pattern="#,##0.00"/></span>
                    </div>
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Days</span><span>&times; ${rental.days}</span>
                    </div>
                </c:if>
                <div class="d-flex justify-content-between border-top pt-2">
                    <span class="fw-semibold">Estimated total</span>
                    <span class="fs-5 fw-bold text-brand">Rs. <fmt:formatNumber value="${cost}" pattern="#,##0.00"/></span>
                </div>
                <p class="small text-muted mt-3 mb-0">The final bill (with any late fees) is created by the admin.</p>
            </div>
        </div>
        <div class="card mt-4">
            <div class="card-header py-3"><i class="bi bi-receipt me-1"></i>Bill</div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty payment}">
                        <c:set var="payStatus" value="${payment.status}"/>
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <div class="fw-semibold"><c:out value="${payment.id}"/> <%@ include file="/WEB-INF/views/includes/payment-status.jspf" %></div>
                                <div class="small text-muted">Total Rs. <fmt:formatNumber value="${payment.calculateTotal()}" pattern="#,##0.00"/></div>
                            </div>
                            <a href="${ctx}/payments/view?id=${payment.id}" class="btn btn-sm btn-outline-primary">View bill</a>
                        </div>
                    </c:when>
                    <c:when test="${me.canAccessAdminPages() and rental.status != 'CANCELLED'}">
                        <a href="${ctx}/admin/payments/generate?rentalId=${rental.id}" class="btn btn-primary w-100">
                            <i class="bi bi-receipt me-1"></i>Generate bill</a>
                    </c:when>
                    <c:otherwise><span class="small text-muted">No bill has been generated yet.</span></c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
