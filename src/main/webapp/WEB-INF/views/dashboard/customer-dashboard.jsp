<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My dashboard"/>
<c:set var="activeNav" value="dashboard"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Hello, <c:out value="${me.name}"/></h1>
        <p class="subtitle">Find a vehicle, manage your bookings and check your bills.</p>
    </div>
</div>

<div class="card">
    <div class="card-header py-3">Quick actions</div>
    <div class="card-body d-flex flex-wrap gap-2">
        <a href="${ctx}/profile" class="btn btn-outline-primary"><i class="bi bi-person-gear me-1"></i>Edit my profile</a>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
