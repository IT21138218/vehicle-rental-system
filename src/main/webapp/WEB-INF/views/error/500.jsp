<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Something went wrong"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="text-center py-5" style="max-width: 560px; margin: 0 auto;">
    <div class="display-1 fw-bold text-brand">500</div>
    <h1 class="h3 mb-3">Something went wrong</h1>
    <p class="text-muted mb-4">An unexpected error occurred while processing your request. Please try again. If the problem continues, contact the administrator.</p>
    <a href="${ctx}/" class="btn btn-primary"><i class="bi bi-house me-1"></i>Go to my dashboard</a>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
