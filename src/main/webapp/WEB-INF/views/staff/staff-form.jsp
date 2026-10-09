<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="isEdit" value="${mode == 'edit'}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit staff member' : 'Add staff member'}"/>
<c:set var="activeNav" value="staff"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>
            <c:choose>
                <c:when test="${isEdit}">Edit <c:out value="${staff.name}"/> <span class="text-muted"><c:out value="${staff.id}"/></span></c:when>
                <c:otherwise>Add a staff member</c:otherwise>
            </c:choose>
        </h1>
        <p class="subtitle">Drivers need a licence number; mechanics need a specialization.</p>
    </div>
    <a href="${ctx}/admin/staff" class="btn btn-outline-secondary"><i class="bi bi-arrow-left me-1"></i>Back to staff</a>
</div>

<div class="row g-4">
    <div class="col-lg-8">
        <div class="card">
            <div class="card-body p-4">
                <form action="${ctx}/admin/staff/${isEdit ? 'edit' : 'add'}" method="post" class="needs-validation" novalidate>
                    <c:if test="${isEdit}"><input type="hidden" name="id" value="${fn:escapeXml(staff.id)}"></c:if>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="type" class="form-label">Role</label>
                            <%-- Each option says how to label and check the role-specific field --%>
                            <select class="form-select" id="type" name="type" data-extra-input="extra" ${isEdit ? 'disabled' : ''}>
                                <option value="DRIVER" data-label="Licence number" data-pattern="[A-Za-z]\d{7}" data-placeholder="e.g. B1234567"
                                        data-feedback="One letter followed by 7 digits." ${form.type == 'DRIVER' ? 'selected' : ''}>Driver</option>
                                <option value="MECHANIC" data-label="Specialization" data-pattern="[^,]{1,30}" data-placeholder="e.g. Engine"
                                        data-feedback="Enter a specialization (no commas)." ${form.type == 'MECHANIC' ? 'selected' : ''}>Mechanic</option>
                            </select>
                            <c:if test="${isEdit}"><div class="form-text">The role cannot be changed.</div></c:if>
                        </div>
                        <div class="col-md-6">
                            <label for="extra" class="form-label">Licence number</label>
                            <input type="text" class="form-control" id="extra" name="extra" required value="${fn:escapeXml(form.extra)}">
                            <div class="invalid-feedback" id="extraFeedback">Required.</div>
                        </div>
                        <div class="col-md-6">
                            <label for="name" class="form-label">Full name</label>
                            <input type="text" class="form-control" id="name" name="name" required maxlength="60" pattern="[^,]+"
                                   value="${fn:escapeXml(form.name)}">
                            <div class="invalid-feedback">Name is required (no commas).</div>
                        </div>
                        <div class="col-md-6">
                            <label for="phone" class="form-label">Phone</label>
                            <input type="tel" class="form-control" id="phone" name="phone" required pattern="0\d{9}"
                                   placeholder="0771234567" value="${fn:escapeXml(form.phone)}">
                            <div class="invalid-feedback">10 digits starting with 0.</div>
                        </div>
                        <div class="col-md-6">
                            <label for="dailyWage" class="form-label">Daily wage (Rs.)</label>
                            <input type="number" class="form-control" id="dailyWage" name="dailyWage" required min="1" max="100000"
                                   step="0.01" value="${fn:escapeXml(form.dailyWage)}">
                            <div class="invalid-feedback">Wage must be greater than 0.</div>
                        </div>
                    </div>
                    <div class="mt-4 d-flex gap-2">
                        <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>${isEdit ? 'Save changes' : 'Add staff member'}</button>
                        <a href="${ctx}/admin/staff" class="btn btn-outline-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="col-lg-4">
        <div class="card">
            <div class="card-header py-3"><i class="bi bi-calculator me-1"></i>Pay rules</div>
            <ul class="list-group list-group-flush small">
                <li class="list-group-item"><strong>Driver:</strong> (daily wage + Rs. 500 meal allowance) &times; days worked</li>
                <li class="list-group-item"><strong>Mechanic:</strong> daily wage &times; days worked + Rs. 5,000 tool allowance</li>
            </ul>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
