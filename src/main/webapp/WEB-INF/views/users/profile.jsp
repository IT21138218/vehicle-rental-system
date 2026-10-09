<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My profile"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>My profile</h1>
        <p class="subtitle">Update your contact details or change your password.</p>
    </div>
</div>

<div class="row g-4">
    <div class="col-lg-4">
        <div class="card h-100">
            <div class="card-body">
                <div class="d-flex align-items-center gap-3 mb-3">
                    <span class="stat-card"><span class="stat-icon"><i class="bi bi-person"></i></span></span>
                    <div>
                        <div class="fw-semibold"><c:out value="${me.name}"/></div>
                        <div class="text-muted small">@<c:out value="${me.username}"/></div>
                    </div>
                </div>
                <dl class="row mb-0 small">
                    <dt class="col-5 text-muted fw-normal">User ID</dt><dd class="col-7"><c:out value="${me.id}"/></dd>
                    <dt class="col-5 text-muted fw-normal">Role</dt><dd class="col-7"><c:out value="${me.role}"/></dd>
                    <dt class="col-5 text-muted fw-normal">Email</dt><dd class="col-7 text-break"><c:out value="${me.email}"/></dd>
                </dl>
            </div>
        </div>
    </div>

    <div class="col-lg-8">
        <div class="card">
            <div class="card-header py-3">Edit details</div>
            <div class="card-body p-4">
                <form action="${pageContext.request.contextPath}/profile" method="post" class="needs-validation" novalidate>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="name" class="form-label">Full name</label>
                            <input type="text" class="form-control" id="name" name="name" required maxlength="60"
                                   pattern="[^,]+" value="${fn:escapeXml(form.name)}">
                            <div class="invalid-feedback">Enter your name (no commas).</div>
                        </div>
                        <div class="col-md-6">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email" required maxlength="100"
                                   value="${fn:escapeXml(form.email)}">
                            <div class="invalid-feedback">Enter a valid email address.</div>
                        </div>
                    </div>

                    <hr class="my-4">
                    <h2 class="h6">Change password <span class="text-muted fw-normal">(optional)</span></h2>
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label for="currentPassword" class="form-label">Current password</label>
                            <input type="password" class="form-control" id="currentPassword" name="currentPassword"
                                   autocomplete="current-password">
                        </div>
                        <div class="col-md-4">
                            <label for="newPassword" class="form-label">New password</label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword"
                                   minlength="6" maxlength="30" pattern="[^,]+" autocomplete="new-password">
                            <div class="invalid-feedback">At least 6 characters, no commas.</div>
                        </div>
                        <div class="col-md-4">
                            <label for="confirmPassword" class="form-label">Confirm new password</label>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                                   data-match="newPassword" autocomplete="new-password">
                            <div class="invalid-feedback">Passwords do not match.</div>
                        </div>
                    </div>

                    <div class="mt-4">
                        <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Save changes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
