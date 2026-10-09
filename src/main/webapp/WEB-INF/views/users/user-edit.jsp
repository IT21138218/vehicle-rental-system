<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Edit user"/>
<c:set var="activeNav" value="users"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Edit user <span class="text-muted"><c:out value="${editUser.id}"/></span></h1>
        <p class="subtitle">Role: <c:out value="${editUser.role}"/></p>
    </div>
    <a href="${ctx}/admin/users" class="btn btn-outline-secondary"><i class="bi bi-arrow-left me-1"></i>Back to users</a>
</div>

<div class="card" style="max-width: 720px;">
    <div class="card-body p-4">
        <form action="${ctx}/admin/users/edit" method="post" class="needs-validation" novalidate>
            <input type="hidden" name="id" value="${fn:escapeXml(editUser.id)}">
            <div class="row g-3">
                <div class="col-md-6">
                    <label for="name" class="form-label">Full name</label>
                    <input type="text" class="form-control" id="name" name="name" required maxlength="60"
                           pattern="[^,]+" value="${fn:escapeXml(form.name)}">
                    <div class="invalid-feedback">Enter a name (no commas).</div>
                </div>
                <div class="col-md-6">
                    <label for="username" class="form-label">Username</label>
                    <input type="text" class="form-control" id="username" name="username" required
                           pattern="[A-Za-z0-9_.]{4,20}" value="${fn:escapeXml(form.username)}">
                    <div class="invalid-feedback">4-20 letters, digits, dots or underscores.</div>
                </div>
                <div class="col-md-6">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" name="email" required maxlength="100"
                           value="${fn:escapeXml(form.email)}">
                    <div class="invalid-feedback">Enter a valid email address.</div>
                </div>
                <div class="col-md-6">
                    <label for="newPassword" class="form-label">Reset password <span class="text-muted">(optional)</span></label>
                    <input type="password" class="form-control" id="newPassword" name="newPassword"
                           minlength="6" maxlength="30" pattern="[^,]+" autocomplete="new-password">
                    <div class="form-text">Leave blank to keep the current password.</div>
                    <div class="invalid-feedback">At least 6 characters, no commas.</div>
                </div>
            </div>
            <div class="mt-4 d-flex gap-2">
                <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Save changes</button>
                <a href="${ctx}/admin/users" class="btn btn-outline-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
