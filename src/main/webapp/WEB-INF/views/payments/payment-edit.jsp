<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Update bill"/>
<c:set var="activeNav" value="payments"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>
<c:set var="isCard" value="${payment.method == 'CARD'}"/>

<div class="page-header">
    <div>
        <h1>Update bill <span class="text-muted"><c:out value="${payment.id}"/></span></h1>
        <p class="subtitle">Rental <c:out value="${payment.rentalId}"/> &middot; <c:out value="${payment.displayMethod()}"/>
            &middot; base Rs. <fmt:formatNumber value="${payment.baseAmount}" pattern="#,##0.00"/></p>
    </div>
    <a href="${ctx}/payments/view?id=${payment.id}" class="btn btn-outline-secondary"><i class="bi bi-arrow-left me-1"></i>Back to bill</a>
</div>

<div class="card" style="max-width: 720px;">
    <div class="card-body p-4">
        <form action="${ctx}/admin/payments/edit" method="post" class="needs-validation" novalidate>
            <input type="hidden" name="id" value="${fn:escapeXml(payment.id)}">
            <div class="row g-3">
                <div class="col-md-4">
                    <label for="status" class="form-label">Status</label>
                    <select class="form-select" id="status" name="status">
                        <option value="PENDING" ${form.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="PAID" ${form.status == 'PAID' ? 'selected' : ''}>Paid</option>
                        <option value="OVERDUE" ${form.status == 'OVERDUE' ? 'selected' : ''}>Overdue</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label for="lateDays" class="form-label">Late days</label>
                    <input type="number" class="form-control" id="lateDays" name="lateDays" required min="0" max="60" step="1"
                           value="${fn:escapeXml(form.lateDays)}">
                    <div class="invalid-feedback">0 to 60 days.</div>
                </div>
                <div class="col-md-4">
                    <%-- getExtraLabel() is polymorphic: "Received by" or "Card last 4 digits" --%>
                    <label for="extra" class="form-label"><c:out value="${payment.extraLabel}"/></label>
                    <input type="text" class="form-control" id="extra" name="extra" required
                           pattern="${isCard ? '\\d{4}' : '[^,]{1,40}'}" value="${fn:escapeXml(form.extra)}">
                    <div class="invalid-feedback">${isCard ? 'Enter exactly 4 digits.' : 'Required (no commas).'}</div>
                </div>
            </div>
            <div class="mt-4 d-flex gap-2">
                <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Save changes</button>
                <a href="${ctx}/payments/view?id=${payment.id}" class="btn btn-outline-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
