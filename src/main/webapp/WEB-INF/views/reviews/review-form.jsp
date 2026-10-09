<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="isEdit" value="${mode == 'edit'}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit review' : 'Write a review'}"/>
<c:set var="activeNav" value="reviews"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>${isEdit ? 'Edit your review' : 'Write a review'}</h1>
        <p class="subtitle"><c:out value="${empty vehicle ? '' : vehicle.displayDetails()}"/></p>
    </div>
    <a href="${ctx}/reviews?vehicleId=${isEdit ? review.vehicleId : vehicle.id}" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-1"></i>Back to reviews</a>
</div>

<div class="card" style="max-width: 720px;">
    <div class="card-body p-4">
        <div class="alert ${willBeVerified ? 'alert-success' : 'alert-light border'} small">
            <c:choose>
                <c:when test="${willBeVerified}"><i class="bi bi-patch-check-fill me-1"></i>You have returned a rental of this vehicle, so this will be a <strong>Verified renter</strong> review.</c:when>
                <c:otherwise><i class="bi bi-info-circle me-1"></i>This will be a <strong>Public review</strong>. It becomes verified after you complete a rental of this vehicle.</c:otherwise>
            </c:choose>
        </div>
        <form action="${ctx}/reviews/${isEdit ? 'edit' : 'new'}" method="post" class="needs-validation" novalidate>
            <c:choose>
                <c:when test="${isEdit}"><input type="hidden" name="id" value="${fn:escapeXml(review.id)}"></c:when>
                <c:otherwise><input type="hidden" name="vehicleId" value="${fn:escapeXml(vehicle.id)}"></c:otherwise>
            </c:choose>
            <div class="mb-3">
                <label for="rating" class="form-label">Rating</label>
                <select class="form-select" id="rating" name="rating" required style="max-width: 240px;">
                    <c:forEach begin="1" end="5" var="n">
                        <c:set var="stars" value="${6 - n}"/>
                        <option value="${stars}" ${form.rating == stars ? 'selected' : ''}>${stars} star${stars > 1 ? 's' : ''}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="mb-3">
                <label for="comment" class="form-label">Comment</label>
                <textarea class="form-control" id="comment" name="comment" rows="4" required minlength="3" maxlength="500"
                          placeholder="How was the vehicle? Commas are fine."><c:out value="${form.comment}"/></textarea>
                <div class="form-text">3 to 500 characters.</div>
                <div class="invalid-feedback">Write at least 3 characters.</div>
            </div>
            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary"><i class="bi bi-send me-1"></i>${isEdit ? 'Save review' : 'Post review'}</button>
                <a href="${ctx}/reviews?vehicleId=${isEdit ? review.vehicleId : vehicle.id}" class="btn btn-outline-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
