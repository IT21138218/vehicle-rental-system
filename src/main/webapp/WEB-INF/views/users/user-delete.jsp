<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Delete user"/>
<c:set var="activeNav" value="users"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="card confirm-card">
    <div class="card-body p-4">
        <h1 class="h4 mb-3"><i class="bi bi-exclamation-octagon text-danger me-2"></i>Delete this user?</h1>
        <p class="text-muted">This permanently removes the account from users.txt. It cannot be undone.</p>
        <dl class="row mb-4">
            <dt class="col-4">ID</dt><dd class="col-8"><c:out value="${deleteUser.id}"/></dd>
            <dt class="col-4">Name</dt><dd class="col-8"><c:out value="${deleteUser.name}"/></dd>
            <dt class="col-4">Username</dt><dd class="col-8">@<c:out value="${deleteUser.username}"/></dd>
            <dt class="col-4">Email</dt><dd class="col-8"><c:out value="${deleteUser.email}"/></dd>
            <dt class="col-4">Role</dt><dd class="col-8"><c:out value="${deleteUser.role}"/></dd>
        </dl>
        <form action="${ctx}/admin/users/delete" method="post" class="d-flex gap-2">
            <input type="hidden" name="id" value="${fn:escapeXml(deleteUser.id)}">
            <button type="submit" class="btn btn-danger"><i class="bi bi-trash me-1"></i>Yes, delete</button>
            <a href="${ctx}/admin/users" class="btn btn-outline-secondary">Cancel</a>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
