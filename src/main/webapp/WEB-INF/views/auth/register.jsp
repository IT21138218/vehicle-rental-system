<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Create account"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="auth-wrapper" style="max-width: 520px;">
    <div class="text-center mb-4">
        <h1 class="h3 auth-title">Create your account</h1>
        <p class="text-muted mb-0">Register as a customer to start renting.</p>
    </div>

    <div class="card">
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/register" method="post" class="needs-validation" novalidate>
                <div class="mb-3">
                    <label for="name" class="form-label">Full name</label>
                    <input type="text" class="form-control" id="name" name="name" required maxlength="60"
                           pattern="[^,]+" value="${fn:escapeXml(form.name)}">
                    <div class="invalid-feedback">Enter your name (no commas).</div>
                </div>
                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" name="email" required maxlength="100"
                           value="${fn:escapeXml(form.email)}">
                    <div class="invalid-feedback">Enter a valid email address.</div>
                </div>
                <div class="mb-3">
                    <label for="username" class="form-label">Username</label>
                    <input type="text" class="form-control" id="username" name="username" required
                           pattern="[A-Za-z0-9_.]{4,20}" autocomplete="username" value="${fn:escapeXml(form.username)}">
                    <div class="form-text">4-20 letters, digits, dots or underscores.</div>
                    <div class="invalid-feedback">Username must be 4-20 letters, digits, dots or underscores.</div>
                </div>
                <div class="row g-3 mb-4">
                    <div class="col-sm-6">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" required
                               minlength="6" maxlength="30" pattern="[^,]+" autocomplete="new-password">
                        <div class="invalid-feedback">At least 6 characters, no commas.</div>
                    </div>
                    <div class="col-sm-6">
                        <label for="confirmPassword" class="form-label">Confirm password</label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                               required data-match="password" autocomplete="new-password">
                        <div class="invalid-feedback">Passwords do not match.</div>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary w-100">
                    <i class="bi bi-person-plus me-1"></i>Create account
                </button>
            </form>
        </div>
    </div>

    <p class="text-center mt-3">
        Already registered? <a href="${pageContext.request.contextPath}/login">Log in</a>
    </p>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
