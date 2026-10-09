<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Bill ${payment.id}"/>
<c:set var="activeNav" value="payments"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>
<c:set var="payStatus" value="${payment.status}"/>

<div class="page-header no-print">
    <div>
        <h1>Bill <c:out value="${payment.id}"/> <%@ include file="/WEB-INF/views/includes/payment-status.jspf" %></h1>
        <p class="subtitle">For rental <c:out value="${payment.rentalId}"/> &middot; issued <c:out value="${payment.issueDate}"/></p>
    </div>
    <div class="d-flex flex-wrap gap-2">
        <button type="button" class="btn btn-outline-secondary" onclick="window.print()"><i class="bi bi-printer me-1"></i>Print</button>
        <c:if test="${me.canAccessAdminPages()}">
            <a href="${ctx}/admin/payments/edit?id=${payment.id}" class="btn btn-outline-primary"><i class="bi bi-pencil me-1"></i>Update</a>
        </c:if>
        <a href="${ctx}/payments" class="btn btn-outline-secondary"><i class="bi bi-arrow-left me-1"></i>All bills</a>
    </div>
</div>

<div class="card" style="max-width: 760px; margin: 0 auto;">
    <div class="card-body p-4 p-md-5">
        <div class="d-flex justify-content-between flex-wrap gap-3 mb-4">
            <div>
                <div class="fs-4 fw-bold"><i class="bi bi-car-front-fill text-brand me-1"></i>RentRide</div>
                <div class="text-muted small">Vehicle Rental Service Platform</div>
            </div>
            <div class="text-md-end">
                <div class="fw-semibold">INVOICE <c:out value="${payment.id}"/></div>
                <div class="small text-muted">Issued <c:out value="${payment.issueDate}"/></div>
                <div class="mt-1"><%@ include file="/WEB-INF/views/includes/payment-status.jspf" %></div>
            </div>
        </div>

        <div class="row g-3 mb-4 small">
            <div class="col-sm-6">
                <div class="text-muted text-uppercase fw-semibold mb-1" style="font-size:.7rem;">Billed to</div>
                <div class="fw-semibold"><c:out value="${empty customer ? payment.customerId : customer.name}"/></div>
                <c:if test="${not empty customer}"><div><c:out value="${customer.email}"/></div></c:if>
            </div>
            <div class="col-sm-6">
                <div class="text-muted text-uppercase fw-semibold mb-1" style="font-size:.7rem;">Rental</div>
                <div class="fw-semibold"><c:out value="${empty vehicle ? payment.rentalId : vehicle.displayDetails()}"/></div>
                <c:if test="${not empty rental}">
                    <div><c:out value="${rental.startDate}"/> &rarr; <c:out value="${rental.endDate}"/> (${rental.days} days)</div>
                </c:if>
            </div>
        </div>

        <table class="table bill-table">
            <thead><tr><th>Description</th><th>Amount (Rs.)</th></tr></thead>
            <tbody>
            <tr>
                <td>Rental charge
                    <c:if test="${not empty rental}"><span class="text-muted small">(${rental.days} days &times; daily rate)</span></c:if></td>
                <td><fmt:formatNumber value="${payment.baseAmount}" pattern="#,##0.00"/></td>
            </tr>
            <%-- getLateFee(), getSurcharge() and calculateTotal() are polymorphic --%>
            <tr>
                <td>Late fee <span class="text-muted small">(${payment.lateDays} late day(s))</span></td>
                <td><fmt:formatNumber value="${payment.lateFee}" pattern="#,##0.00"/></td>
            </tr>
            <c:if test="${payment.surcharge > 0}">
                <tr>
                    <td>Card processing surcharge <span class="text-muted small">(3%)</span></td>
                    <td><fmt:formatNumber value="${payment.surcharge}" pattern="#,##0.00"/></td>
                </tr>
            </c:if>
            </tbody>
            <tfoot>
            <tr>
                <th class="bill-total">Total</th>
                <th class="bill-total text-brand">Rs. <fmt:formatNumber value="${payment.calculateTotal()}" pattern="#,##0.00"/></th>
            </tr>
            </tfoot>
        </table>

        <div class="small text-muted">
            Payment method: <strong><c:out value="${payment.displayMethod()}"/></strong>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
