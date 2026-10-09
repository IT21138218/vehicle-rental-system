<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Generate bill"/>
<c:set var="activeNav" value="payments"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Generate a bill</h1>
        <p class="subtitle">Base amount = rental days &times; the vehicle's daily rate. Fees depend on the payment method.</p>
    </div>
    <a href="${ctx}/payments" class="btn btn-outline-secondary"><i class="bi bi-arrow-left me-1"></i>All bills</a>
</div>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="card">
            <div class="card-body p-4">
                <c:choose>
                    <c:when test="${empty billableRentals}">
                        <div class="empty-state"><i class="bi bi-check2-all"></i>Every rental already has a bill.</div>
                    </c:when>
                    <c:otherwise>
                        <form action="${ctx}/admin/payments/generate" method="post" class="needs-validation" novalidate>
                            <div class="mb-3">
                                <label for="rentalId" class="form-label">Rental</label>
                                <select class="form-select" id="rentalId" name="rentalId" required>
                                    <option value="">Choose a rental...</option>
                                    <c:forEach var="r" items="${billableRentals}">
                                        <c:set var="v" value="${vehiclesById[r.vehicleId]}"/>
                                        <option value="${r.id}" ${form.rentalId == r.id ? 'selected' : ''}>
                                            <c:out value="${r.id} - ${empty usersById[r.customerId] ? r.customerId : usersById[r.customerId].name} - ${empty v ? r.vehicleId : v.brand} ${empty v ? '' : v.model} (${r.days} days, ${r.status})"/>
                                        </option>
                                    </c:forEach>
                                </select>
                                <div class="invalid-feedback">Choose the rental to bill.</div>
                            </div>
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="method" class="form-label">Payment method</label>
                                    <select class="form-select" id="method" name="method" data-extra-input="extra" required>
                                        <option value="CASH" data-label="Received by" data-pattern="[^,]{1,40}" data-placeholder="e.g. Front Desk"
                                                data-feedback="Enter who received the cash (no commas)." ${form.method == 'CASH' ? 'selected' : ''}>Cash</option>
                                        <option value="CARD" data-label="Card last 4 digits" data-pattern="\d{4}" data-placeholder="e.g. 4242"
                                                data-feedback="Enter exactly 4 digits." ${form.method == 'CARD' ? 'selected' : ''}>Card</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="extra" class="form-label">Received by</label>
                                    <input type="text" class="form-control" id="extra" name="extra" required value="${fn:escapeXml(form.extra)}">
                                    <div class="invalid-feedback" id="extraFeedback">Required.</div>
                                </div>
                                <div class="col-md-6">
                                    <label for="lateDays" class="form-label">Late days</label>
                                    <input type="number" class="form-control" id="lateDays" name="lateDays" required min="0" max="60" step="1"
                                           value="${fn:escapeXml(form.lateDays)}">
                                    <div class="invalid-feedback">0 to 60 days.</div>
                                </div>
                            </div>
                            <div class="mt-4 d-flex gap-2">
                                <button type="submit" class="btn btn-primary"><i class="bi bi-receipt me-1"></i>Generate bill</button>
                                <a href="${ctx}/payments" class="btn btn-outline-secondary">Cancel</a>
                            </div>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="card">
            <div class="card-header py-3"><i class="bi bi-calculator me-1"></i>Billing rules</div>
            <ul class="list-group list-group-flush small">
                <li class="list-group-item"><strong>Cash:</strong> base + Rs. 1,000 per late day</li>
                <li class="list-group-item"><strong>Card:</strong> (base + Rs. 1,500 per late day) + 3% surcharge</li>
                <li class="list-group-item text-muted">Cancelled rentals cannot be billed. Each rental has one bill.</li>
            </ul>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
