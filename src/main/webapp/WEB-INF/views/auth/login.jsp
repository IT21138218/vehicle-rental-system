<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Log in"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="auth-wrapper">
    <div class="text-center mb-4">
        <div class="stat-card d-inline-block mb-2"><span class="stat-icon mx-auto"><i class="bi bi-key"></i></span></div>
        <h1 class="h3 auth-title">Welcome back</h1>
        <p class="text-muted mb-0">Log in to rent, manage and review vehicles.</p>
    </div>

    <div class="card">
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/login" method="post" class="needs-validation" novalidate>
                <div class="mb-3">
                    <label for="username" class="form-label">Username</label>
                    <input type="text" class="form-control" id="username" name="username" required autofocus
                           autocomplete="username" value="${fn:escapeXml(username)}">
                    <div class="invalid-feedback">Please enter your username.</div>
                </div>
                <div class="mb-4">
                    <label for="password" class="form-label">Password</label>
                    <input type="password" class="form-control" id="password" name="password" required
                           autocomplete="current-password">
                    <div class="invalid-feedback">Please enter your password.</div>
                </div>
                <button type="submit" class="btn btn-primary w-100">
                    <i class="bi bi-box-arrow-in-right me-1"></i>Log in
                </button>
            </form>
        </div>
    </div>

    <p class="text-center mt-3 mb-3">
        New customer? <a href="${pageContext.request.contextPath}/register">Create an account</a>
    </p>
    <div class="demo-hint text-muted">
        <i class="bi bi-info-circle me-1"></i>Demo accounts:
        admin <code>admin</code> / <code>admin123</code> &middot; customer <code>nimal</code> / <code>pass123</code>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
