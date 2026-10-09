<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Log in"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="auth-layout">
    <section class="auth-intro">
        <h1>Rent a car, bike or van in minutes.</h1>
        <p>Pick a vehicle, choose your dates and see the price before you book.</p>
        <ul class="auth-points">
            <li><i class="bi bi-calendar-check"></i><span>Dates that are already taken are shown before you book, so there are no clashes.</span></li>
            <li><i class="bi bi-receipt"></i><span>Every rental gets a clear bill with late fees and card charges itemised.</span></li>
            <li><i class="bi bi-patch-check"></i><span>Reviews marked <strong>Verified renter</strong> come from people who returned the vehicle.</span></li>
        </ul>
    </section>

    <section class="auth-card">
        <div class="card">
            <div class="card-body">
                <h2 class="h4 mb-1">Log in</h2>
                <p class="text-muted mb-4">Use your RentRide account.</p>
                <form action="${pageContext.request.contextPath}/login" method="post" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" class="form-control" id="username" name="username" required autofocus
                               autocomplete="username" value="${fn:escapeXml(username)}">
                        <div class="invalid-feedback">Enter your username.</div>
                    </div>
                    <div class="mb-4">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" required
                               autocomplete="current-password">
                        <div class="invalid-feedback">Enter your password.</div>
                    </div>
                    <button type="submit" class="btn btn-primary w-100 py-2">Log in</button>
                </form>
                <p class="text-center mt-3 mb-0">
                    New here? <a href="${pageContext.request.contextPath}/register">Create an account</a>
                </p>
            </div>
        </div>
        <div class="demo-hint mt-3">
            Demo accounts: <code>admin</code> / <code>admin123</code> (admin),
            <code>nimal</code> / <code>pass123</code> (customer)
        </div>
    </section>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
