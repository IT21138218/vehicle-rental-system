<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Delete bill"/>
<c:set var="activeNav" value="payments"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="card confirm-card">
    <div class="card-body p-4">
        <h1 class="h4 mb-3"><i class="bi bi-exclamation-octagon text-danger me-2"></i>Delete bill <c:out value="${payment.id}"/>?</h1>
        <p class="text-muted">This removes the bill from payments.txt. The rental can then be billed again.</p>
        <dl class="row mb-4">
            <dt class="col-4">Rental</dt><dd class="col-8"><c:out value="${payment.rentalId}"/></dd>
            <dt class="col-4">Method</dt><dd class="col-8"><c:out value="${payment.displayMethod()}"/></dd>
            <dt class="col-4">Total</dt><dd class="col-8">Rs. <fmt:formatNumber value="${payment.calculateTotal()}" pattern="#,##0.00"/></dd>
            <dt class="col-4">Status</dt><dd class="col-8"><c:out value="${payment.status}"/></dd>
        </dl>
        <form action="${ctx}/admin/payments/delete" method="post" class="d-flex gap-2">
            <input type="hidden" name="id" value="${fn:escapeXml(payment.id)}">
            <button type="submit" class="btn btn-danger"><i class="bi bi-trash me-1"></i>Yes, delete</button>
            <a href="${ctx}/payments/view?id=${payment.id}" class="btn btn-outline-secondary">Cancel</a>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
