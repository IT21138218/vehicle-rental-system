<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Delete review"/>
<c:set var="activeNav" value="reviews"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>
<c:set var="starsValue" value="${review.rating}"/>

<div class="card confirm-card">
    <div class="card-body p-4">
        <h1 class="h4 mb-3"><i class="bi bi-exclamation-octagon text-danger me-2"></i>Delete review <c:out value="${review.id}"/>?</h1>
        <p class="text-muted">This removes the review from reviews.txt. It cannot be undone.</p>
        <dl class="row mb-4">
            <dt class="col-4">Vehicle</dt><dd class="col-8"><c:out value="${empty vehicle ? review.vehicleId : vehicle.displayDetails()}"/></dd>
            <dt class="col-4">Rating</dt><dd class="col-8"><%@ include file="/WEB-INF/views/includes/stars.jspf" %></dd>
            <dt class="col-4">Type</dt><dd class="col-8"><c:out value="${review.displayLabel()}"/></dd>
            <dt class="col-4">Comment</dt><dd class="col-8"><c:out value="${review.comment}"/></dd>
        </dl>
        <form action="${ctx}/reviews/delete" method="post" class="d-flex gap-2">
            <input type="hidden" name="id" value="${fn:escapeXml(review.id)}">
            <button type="submit" class="btn btn-danger"><i class="bi bi-trash me-1"></i>Yes, delete</button>
            <a href="${ctx}/reviews?vehicleId=${review.vehicleId}" class="btn btn-outline-secondary">Cancel</a>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
