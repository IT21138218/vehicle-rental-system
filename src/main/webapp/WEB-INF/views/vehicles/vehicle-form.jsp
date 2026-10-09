<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="isEdit" value="${mode == 'edit'}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit vehicle' : 'Add vehicle'}"/>
<c:set var="activeNav" value="vehicles"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>
            <c:choose>
                <c:when test="${isEdit}">Edit vehicle <span class="text-muted"><c:out value="${vehicle.id}"/></span></c:when>
                <c:otherwise>Add a vehicle</c:otherwise>
            </c:choose>
        </h1>
        <p class="subtitle">The daily rate customers pay is calculated from the base rate by each vehicle type's own rule.</p>
    </div>
    <a href="${ctx}/admin/vehicles" class="btn btn-outline-secondary"><i class="bi bi-arrow-left me-1"></i>Back to fleet</a>
</div>

<div class="row g-4">
    <div class="col-lg-8">
        <div class="card">
            <div class="card-body p-4">
                <form action="${ctx}/admin/vehicles/${isEdit ? 'edit' : 'add'}" method="post" class="needs-validation" novalidate>
                    <c:if test="${isEdit}"><input type="hidden" name="id" value="${fn:escapeXml(vehicle.id)}"></c:if>

                    <div class="row g-3">
                        <div class="col-md-4">
                            <label for="type" class="form-label">Type</label>
                            <%-- Each option says how to label and limit the type-specific field --%>
                            <select class="form-select" id="type" name="type" data-spec-input="spec" ${isEdit ? 'disabled' : ''} required>
                                <option value="CAR" data-label="Seats" data-min="2" data-max="9"
                                        ${form.type == 'CAR' ? 'selected' : ''}>Car</option>
                                <option value="BIKE" data-label="Engine (cc)" data-min="50" data-max="2000"
                                        ${form.type == 'BIKE' ? 'selected' : ''}>Bike</option>
                                <option value="VAN" data-label="Cargo (kg)" data-min="100" data-max="5000"
                                        ${form.type == 'VAN' ? 'selected' : ''}>Van</option>
                            </select>
                            <c:if test="${isEdit}"><div class="form-text">The type cannot be changed.</div></c:if>
                        </div>
                        <div class="col-md-4">
                            <label for="brand" class="form-label">Brand</label>
                            <input type="text" class="form-control" id="brand" name="brand" required maxlength="30"
                                   pattern="[^,]+" placeholder="e.g. Toyota" value="${fn:escapeXml(form.brand)}">
                            <div class="invalid-feedback">Brand is required (no commas).</div>
                        </div>
                        <div class="col-md-4">
                            <label for="model" class="form-label">Model</label>
                            <input type="text" class="form-control" id="model" name="model" required maxlength="30"
                                   pattern="[^,]+" placeholder="e.g. Corolla" value="${fn:escapeXml(form.model)}">
                            <div class="invalid-feedback">Model is required (no commas).</div>
                        </div>
                        <div class="col-md-4">
                            <label for="year" class="form-label">Year</label>
                            <input type="number" class="form-control" id="year" name="year" required min="1990" step="1"
                                   value="${fn:escapeXml(form.year)}">
                            <div class="invalid-feedback">Enter a year from 1990.</div>
                        </div>
                        <div class="col-md-4">
                            <label for="baseDailyRate" class="form-label">Base daily rate (Rs.)</label>
                            <input type="number" class="form-control" id="baseDailyRate" name="baseDailyRate" required
                                   min="1" max="1000000" step="0.01" value="${fn:escapeXml(form.baseDailyRate)}">
                            <div class="invalid-feedback">Rate must be greater than 0.</div>
                        </div>
                        <div class="col-md-4">
                            <label for="spec" class="form-label" id="specLabel">Seats</label>
                            <input type="number" class="form-control" id="spec" name="spec" required step="1"
                                   value="${fn:escapeXml(form.spec)}">
                            <div class="invalid-feedback" id="specFeedback">Value is out of range.</div>
                        </div>
                        <div class="col-12">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" role="switch" id="available" name="available"
                                       ${not empty form.available ? 'checked' : ''}>
                                <label class="form-check-label" for="available">Available for booking (in service)</label>
                            </div>
                        </div>
                    </div>

                    <div class="mt-4 d-flex gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-save me-1"></i>${isEdit ? 'Save changes' : 'Add vehicle'}
                        </button>
                        <a href="${ctx}/admin/vehicles" class="btn btn-outline-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="col-lg-4">
        <div class="card">
            <div class="card-header py-3"><i class="bi bi-calculator me-1"></i>Pricing rules</div>
            <ul class="list-group list-group-flush small">
                <li class="list-group-item"><strong>Car:</strong> base rate, +10% if more than 5 seats</li>
                <li class="list-group-item"><strong>Bike:</strong> base rate, &times;1.2 if engine above 500cc</li>
                <li class="list-group-item"><strong>Van:</strong> base rate + Rs. 2 per kg of cargo capacity</li>
            </ul>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
