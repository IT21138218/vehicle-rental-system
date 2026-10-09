<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Users"/>
<c:set var="activeNav" value="users"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Users</h1>
        <p class="subtitle">Search, edit and remove customer and admin accounts.</p>
    </div>
</div>

<div class="card mb-4">
    <div class="card-body">
        <form method="get" action="${ctx}/admin/users" class="row g-2 align-items-end">
            <div class="col-md-6">
                <label for="q" class="form-label small text-muted">Search</label>
                <input type="search" class="form-control" id="q" name="q" placeholder="ID, username, name or email"
                       value="${fn:escapeXml(param.q)}">
            </div>
            <div class="col-md-3">
                <label for="role" class="form-label small text-muted">Role</label>
                <select class="form-select" id="role" name="role">
                    <option value="">All roles</option>
                    <option value="ADMIN" ${param.role == 'ADMIN' ? 'selected' : ''}>Admin</option>
                    <option value="CUSTOMER" ${param.role == 'CUSTOMER' ? 'selected' : ''}>Customer</option>
                </select>
            </div>
            <div class="col-md-3 d-flex gap-2">
                <button type="submit" class="btn btn-primary flex-grow-1"><i class="bi bi-search me-1"></i>Filter</button>
                <a href="${ctx}/admin/users" class="btn btn-outline-secondary">Reset</a>
            </div>
        </form>
    </div>
</div>

<div class="card">
    <div class="card-header py-3 d-flex justify-content-between">
        <span>All users</span>
        <span class="text-muted fw-normal small">${fn:length(users)} found</span>
    </div>
    <c:choose>
        <c:when test="${empty users}">
            <div class="empty-state"><i class="bi bi-people"></i>No users match your search.</div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-hover table-app">
                    <thead>
                    <tr><th>ID</th><th>Name</th><th>Username</th><th>Email</th><th>Role</th><th class="text-end">Actions</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="u" items="${users}">
                        <tr>
                            <td class="fw-semibold"><c:out value="${u.id}"/></td>
                            <td><c:out value="${u.name}"/></td>
                            <td>@<c:out value="${u.username}"/></td>
                            <td><c:out value="${u.email}"/></td>
                            <td>
                                <span class="badge ${u.canAccessAdminPages() ? 'text-bg-warning' : 'text-bg-light border'}">
                                    <c:out value="${u.role}"/></span>
                            </td>
                            <td class="text-end text-nowrap">
                                <a href="${ctx}/admin/users/edit?id=${u.id}" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-pencil"></i> Edit</a>
                                <a href="${ctx}/admin/users/delete?id=${u.id}" class="btn btn-sm btn-outline-danger">
                                    <i class="bi bi-trash"></i> Delete</a>
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
