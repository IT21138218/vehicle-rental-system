<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Remove staff member"/>
<c:set var="activeNav" value="staff"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="card confirm-card">
    <div class="card-body p-4">
        <h1 class="h4 mb-3"><i class="bi bi-exclamation-octagon text-danger me-2"></i>Remove this staff member?</h1>
        <p class="text-muted">This permanently removes the record from drivers.txt. It cannot be undone.</p>
        <dl class="row mb-4">
            <dt class="col-4">ID</dt><dd class="col-8"><c:out value="${staff.id}"/></dd>
            <dt class="col-4">Name</dt><dd class="col-8"><c:out value="${staff.name}"/></dd>
            <dt class="col-4">Role</dt><dd class="col-8"><c:out value="${staff.displayRole()}"/></dd>
            <dt class="col-4">Phone</dt><dd class="col-8"><c:out value="${staff.phone}"/></dd>
        </dl>
        <form action="${ctx}/admin/staff/delete" method="post" class="d-flex gap-2">
            <input type="hidden" name="id" value="${fn:escapeXml(staff.id)}">
            <button type="submit" class="btn btn-danger"><i class="bi bi-trash me-1"></i>Yes, remove</button>
            <a href="${ctx}/admin/staff" class="btn btn-outline-secondary">Cancel</a>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
