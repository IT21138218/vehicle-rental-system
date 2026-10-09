<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Staff"/>
<c:set var="activeNav" value="staff"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Drivers and staff</h1>
        <p class="subtitle">
            <c:forEach var="entry" items="${typeCounts}" varStatus="s">
                <c:out value="${entry.value}"/> <c:out value="${fn:toLowerCase(entry.key)}"/>s<c:if test="${not s.last}"> &middot; </c:if>
            </c:forEach>
        </p>
    </div>
    <a href="${ctx}/admin/staff/add" class="btn btn-primary"><i class="bi bi-person-plus me-1"></i>Add staff member</a>
</div>

<div class="card mb-4">
    <div class="card-body">
        <form method="get" action="${ctx}/admin/staff" class="row g-2 align-items-end">
            <div class="col-md-6">
                <label for="q" class="form-label small text-muted">Search</label>
                <input type="search" class="form-control" id="q" name="q" placeholder="ID, name, phone, licence or specialization"
                       value="${fn:escapeXml(param.q)}">
            </div>
            <div class="col-md-3">
                <label for="type" class="form-label small text-muted">Role</label>
                <select class="form-select" id="type" name="type">
                    <option value="">All roles</option>
                    <option value="DRIVER" ${param.type == 'DRIVER' ? 'selected' : ''}>Drivers</option>
                    <option value="MECHANIC" ${param.type == 'MECHANIC' ? 'selected' : ''}>Mechanics</option>
                </select>
            </div>
            <div class="col-md-3 d-flex gap-2">
                <button type="submit" class="btn btn-primary flex-grow-1"><i class="bi bi-search me-1"></i>Filter</button>
                <a href="${ctx}/admin/staff" class="btn btn-outline-secondary">Reset</a>
            </div>
        </form>
    </div>
</div>

<div class="card">
    <div class="card-header py-3 d-flex justify-content-between">
        <span>Staff members</span>
        <span class="text-muted fw-normal small">${fn:length(staffList)} found</span>
    </div>
    <c:choose>
        <c:when test="${empty staffList}">
            <div class="empty-state"><i class="bi bi-person-badge"></i>No staff match your search.</div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-hover table-app">
                    <thead>
                    <tr>
                        <th>ID</th><th>Name</th><th>Role</th><th>Phone</th>
                        <th class="text-end">Daily wage</th><th class="text-end">Monthly pay (${workingDays} days)</th>
                        <th class="text-end">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="s" items="${staffList}">
                        <tr>
                            <td class="fw-semibold"><c:out value="${s.id}"/></td>
                            <td><c:out value="${s.name}"/></td>
                            <%-- displayRole() and calculateMonthlyPay() are polymorphic --%>
                            <td>
                                <i class="bi ${s.type == 'DRIVER' ? 'bi-steering-wheel' : 'bi-wrench-adjustable'} text-brand me-1"></i>
                                <c:out value="${s.displayRole()}"/>
                            </td>
                            <td class="text-nowrap"><c:out value="${s.phone}"/></td>
                            <td class="text-end text-nowrap">Rs. <fmt:formatNumber value="${s.dailyWage}" pattern="#,##0.00"/></td>
                            <td class="text-end text-nowrap fw-semibold">Rs. <fmt:formatNumber value="${s.calculateMonthlyPay(workingDays)}" pattern="#,##0.00"/></td>
                            <td class="text-end text-nowrap">
                                <a href="${ctx}/admin/staff/edit?id=${s.id}" class="btn btn-sm btn-outline-primary"><i class="bi bi-pencil"></i> Edit</a>
                                <a href="${ctx}/admin/staff/delete?id=${s.id}" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i> Remove</a>
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
