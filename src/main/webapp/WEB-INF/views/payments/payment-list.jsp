<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Payments"/>
<c:set var="activeNav" value="payments"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>
<c:set var="isAdmin" value="${me.canAccessAdminPages()}"/>

<div class="page-header">
    <div>
        <h1>${isAdmin ? 'Payments and billing' : 'My bills'}</h1>
        <p class="subtitle">${isAdmin ? 'Bills generated from rentals.' : 'Bills for your rentals.'}</p>
    </div>
    <c:if test="${isAdmin}">
        <a href="${ctx}/admin/payments/generate" class="btn btn-primary"><i class="bi bi-receipt me-1"></i>Generate bill</a>
    </c:if>
</div>

<c:if test="${isAdmin}">
    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card stat-card h-100"><div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-cash-stack"></i></span>
                <div><div class="stat-value fs-4">Rs. <fmt:formatNumber value="${paidTotal}" pattern="#,##0"/></div><div class="stat-label">Collected (paid)</div></div>
            </div></div>
        </div>
        <div class="col-md-4">
            <div class="card stat-card h-100"><div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-hourglass-split"></i></span>
                <div><div class="stat-value fs-4">Rs. <fmt:formatNumber value="${pendingTotal}" pattern="#,##0"/></div><div class="stat-label">Pending</div></div>
            </div></div>
        </div>
        <div class="col-md-4">
            <div class="card stat-card h-100"><div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-exclamation-triangle"></i></span>
                <div><div class="stat-value fs-4">Rs. <fmt:formatNumber value="${overdueTotal}" pattern="#,##0"/></div><div class="stat-label">Overdue</div></div>
            </div></div>
        </div>
    </div>
</c:if>

<ul class="nav nav-pills mb-3 gap-1">
    <li class="nav-item"><a class="nav-link ${empty param.status ? 'active' : ''}" href="${ctx}/payments">All</a></li>
    <li class="nav-item"><a class="nav-link ${param.status == 'PENDING' ? 'active' : ''}" href="${ctx}/payments?status=PENDING">Pending</a></li>
    <li class="nav-item"><a class="nav-link ${param.status == 'PAID' ? 'active' : ''}" href="${ctx}/payments?status=PAID">Paid</a></li>
    <li class="nav-item"><a class="nav-link ${param.status == 'OVERDUE' ? 'active' : ''}" href="${ctx}/payments?status=OVERDUE">Overdue</a></li>
</ul>

<div class="card">
    <c:choose>
        <c:when test="${empty payments}">
            <div class="empty-state"><i class="bi bi-receipt"></i>No bills here.</div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-hover table-app">
                    <thead>
                    <tr>
                        <th>Bill</th><th>Rental</th>
                        <c:if test="${isAdmin}"><th>Customer</th></c:if>
                        <th>Issued</th><th>Method</th><th class="text-end">Total</th><th>Status</th><th class="text-end">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="p" items="${payments}">
                        <c:set var="payStatus" value="${p.status}"/>
                        <tr>
                            <td class="fw-semibold"><c:out value="${p.id}"/></td>
                            <td><a href="${ctx}/rentals/view?id=${p.rentalId}"><c:out value="${p.rentalId}"/></a></td>
                            <c:if test="${isAdmin}">
                                <td><c:out value="${empty usersById[p.customerId] ? p.customerId : usersById[p.customerId].name}"/></td>
                            </c:if>
                            <td class="text-nowrap"><c:out value="${p.issueDate}"/></td>
                            <%-- displayMethod() and calculateTotal() are polymorphic (cash vs card) --%>
                            <td><c:out value="${p.displayMethod()}"/></td>
                            <td class="text-end fw-semibold text-nowrap">Rs. <fmt:formatNumber value="${p.calculateTotal()}" pattern="#,##0.00"/></td>
                            <td><%@ include file="/WEB-INF/views/includes/payment-status.jspf" %></td>
                            <td class="text-end text-nowrap">
                                <a href="${ctx}/payments/view?id=${p.id}" class="btn btn-sm btn-outline-secondary"><i class="bi bi-receipt"></i> Bill</a>
                                <c:if test="${isAdmin}">
                                    <a href="${ctx}/admin/payments/edit?id=${p.id}" class="btn btn-sm btn-outline-primary"><i class="bi bi-pencil"></i> Update</a>
                                    <a href="${ctx}/admin/payments/delete?id=${p.id}" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></a>
                                </c:if>
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
