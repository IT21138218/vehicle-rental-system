<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin dashboard"/>
<c:set var="activeNav" value="dashboard"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Admin dashboard</h1>
        <p class="subtitle">Overview of the rental platform.</p>
    </div>
</div>

<div class="row g-3 mb-4">
    <div class="col-sm-6 col-xl-3">
        <a href="${ctx}/admin/users?role=CUSTOMER" class="card stat-card h-100">
            <div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-people"></i></span>
                <div><div class="stat-value">${customerCount}</div><div class="stat-label">Customers</div></div>
            </div>
        </a>
    </div>
    <div class="col-sm-6 col-xl-3">
        <a href="${ctx}/admin/users?role=ADMIN" class="card stat-card h-100">
            <div class="card-body d-flex align-items-center gap-3">
                <span class="stat-icon"><i class="bi bi-shield-lock"></i></span>
                <div><div class="stat-value">${adminCount}</div><div class="stat-label">Administrators</div></div>
            </div>
        </a>
    </div>
</div>

<div class="card">
    <div class="card-header py-3">Quick actions</div>
    <div class="card-body d-flex flex-wrap gap-2">
        <a href="${ctx}/admin/users" class="btn btn-outline-primary"><i class="bi bi-people me-1"></i>Manage users</a>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
