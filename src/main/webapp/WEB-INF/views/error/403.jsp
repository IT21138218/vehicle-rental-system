<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Access denied"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="text-center py-5" style="max-width: 560px; margin: 0 auto;">
    <div class="display-1 fw-bold text-brand">403</div>
    <h1 class="h3 mb-3">Access denied</h1>
    <p class="text-muted mb-4">You do not have permission to open this page. Administrator pages are only available to admin accounts.</p>
    <a href="${ctx}/" class="btn btn-primary"><i class="bi bi-house me-1"></i>Go to my dashboard</a>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
