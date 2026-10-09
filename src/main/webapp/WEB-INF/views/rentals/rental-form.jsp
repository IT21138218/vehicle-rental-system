<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="isEdit" value="${mode == 'edit'}"/>
<c:set var="pageTitle" value="${isEdit ? 'Change booking dates' : 'Book a vehicle'}"/>
<c:set var="activeNav" value="${isEdit ? 'rentals' : 'browse'}"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>
            <c:choose>
                <c:when test="${isEdit}">Change dates for <c:out value="${rental.id}"/></c:when>
                <c:otherwise>Book <c:out value="${vehicle.brand} ${vehicle.model}"/></c:otherwise>
            </c:choose>
        </h1>
        <p class="subtitle">Pick-up on the start date, return by the end date. Up to 30 days.</p>
    </div>
    <a href="${ctx}${isEdit ? '/rentals/view?id='.concat(rental.id) : '/vehicles/view?id='.concat(vehicle.id)}"
       class="btn btn-outline-secondary"><i class="bi bi-arrow-left me-1"></i>Back</a>
</div>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="card">
            <div class="card-body p-4">
                <form action="${ctx}/rentals/${isEdit ? 'edit' : 'new'}" method="post" class="needs-validation" novalidate
                      data-cost-estimate data-daily-rate="${vehicle.calculateDailyRate()}">
                    <c:choose>
                        <c:when test="${isEdit}"><input type="hidden" name="id" value="${fn:escapeXml(rental.id)}"></c:when>
                        <c:otherwise><input type="hidden" name="vehicleId" value="${fn:escapeXml(vehicle.id)}"></c:otherwise>
                    </c:choose>
                    <div class="row g-3">
                        <div class="col-sm-6">
                            <label for="startDate" class="form-label">Start date</label>
                            <input type="date" class="form-control" id="startDate" name="startDate" required
                                   min="${isEdit ? '' : today}" value="${fn:escapeXml(form.startDate)}">
                            <div class="invalid-feedback">Choose a start date (not in the past).</div>
                        </div>
                        <div class="col-sm-6">
                            <label for="endDate" class="form-label">End date</label>
                            <input type="date" class="form-control" id="endDate" name="endDate" required
                                   min="${today}" value="${fn:escapeXml(form.endDate)}">
                            <div class="invalid-feedback">End date must be after the start date.</div>
                        </div>
                    </div>

                    <div class="bg-brand-soft rounded-3 p-3 mt-4 d-flex justify-content-between align-items-center">
                        <div>
                            <div class="small text-muted">Estimated cost</div>
                            <div class="fw-semibold"><span id="estimateDays">0</span> day(s) &times;
                                Rs. <fmt:formatNumber value="${vehicle.calculateDailyRate()}" pattern="#,##0.00"/></div>
                        </div>
                        <div class="fs-4 fw-bold text-brand">Rs. <span id="estimateTotal">0.00</span></div>
                    </div>

                    <div class="mt-4 d-flex gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-calendar-check me-1"></i>${isEdit ? 'Save new dates' : 'Confirm booking'}
                        </button>
                        <a href="${ctx}/vehicles" class="btn btn-outline-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="col-lg-5">
        <div class="card mb-4">
            <div class="card-body">
                <c:set var="iconType" value="${vehicle.type}"/>
                <div class="d-flex align-items-center gap-3">
                    <span class="stat-card"><span class="stat-icon"><%@ include file="/WEB-INF/views/includes/vehicle-icon.jspf" %></span></span>
                    <div>
                        <div class="fw-semibold"><c:out value="${vehicle.brand} ${vehicle.model}"/></div>
                        <div class="small text-muted"><c:out value="${vehicle.displayDetails()}"/></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="card">
            <div class="card-header py-3"><i class="bi bi-calendar3 me-1"></i>Already booked</div>
            <c:choose>
                <c:when test="${empty upcoming}">
                    <div class="card-body text-muted small">No upcoming bookings - any dates are free.</div>
                </c:when>
                <c:otherwise>
                    <ul class="list-group list-group-flush small">
                        <c:forEach var="u" items="${upcoming}">
                            <li class="list-group-item d-flex justify-content-between">
                                <span><c:out value="${u.startDate}"/> &rarr; <c:out value="${u.endDate}"/></span>
                                <c:if test="${isEdit and u.id == rental.id}"><span class="badge text-bg-light border">this booking</span></c:if>
                            </li>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
