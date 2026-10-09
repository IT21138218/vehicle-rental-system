<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Browse vehicles"/>
<c:set var="activeNav" value="browse"/>
<%@ include file="/WEB-INF/views/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Browse vehicles</h1>
        <p class="subtitle">Every vehicle below is in service and ready to book.</p>
    </div>
</div>

<div class="card mb-4">
    <div class="card-body">
        <form method="get" action="${ctx}/vehicles" class="row g-2 align-items-end">
            <div class="col-md-6">
                <label for="q" class="form-label small text-muted">Search</label>
                <input type="search" class="form-control" id="q" name="q" placeholder="Brand or model, e.g. Toyota"
                       value="${fn:escapeXml(param.q)}">
            </div>
            <div class="col-md-3">
                <label for="type" class="form-label small text-muted">Type</label>
                <select class="form-select" id="type" name="type">
                    <option value="">All types</option>
                    <option value="CAR" ${param.type == 'CAR' ? 'selected' : ''}>Cars</option>
                    <option value="BIKE" ${param.type == 'BIKE' ? 'selected' : ''}>Bikes</option>
                    <option value="VAN" ${param.type == 'VAN' ? 'selected' : ''}>Vans</option>
                </select>
            </div>
            <div class="col-md-3 d-flex gap-2">
                <button type="submit" class="btn btn-primary flex-grow-1"><i class="bi bi-search me-1"></i>Search</button>
                <a href="${ctx}/vehicles" class="btn btn-outline-secondary">Reset</a>
            </div>
        </form>
    </div>
</div>

<c:choose>
    <c:when test="${empty vehicles}">
        <div class="card"><div class="empty-state"><i class="bi bi-search"></i>No available vehicles match your search.</div></div>
    </c:when>
    <c:otherwise>
        <div class="row g-3">
            <c:forEach var="v" items="${vehicles}">
                <c:set var="iconType" value="${v.type}"/>
                <div class="col-sm-6 col-lg-4">
                    <div class="card vehicle-card h-100">
                        <div class="card-body d-flex flex-column">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <span class="vehicle-type-icon"><%@ include file="/WEB-INF/views/includes/vehicle-icon.jspf" %></span>
                                <span class="badge bg-brand-soft text-brand"><c:out value="${v.type}"/></span>
                            </div>
                            <h2 class="h5 mb-1"><c:out value="${v.brand} ${v.model}"/></h2>
                            <%-- displayDetails() is polymorphic: each type describes itself --%>
                            <p class="text-muted small mb-2"><c:out value="${v.displayDetails()}"/></p>
                            <c:set var="starsValue" value="${averages[v.id]}"/>
                            <div class="small mb-3">
                                <c:choose>
                                    <c:when test="${empty starsValue}"><span class="text-muted">No reviews yet</span></c:when>
                                    <c:otherwise><%@ include file="/WEB-INF/views/includes/stars.jspf" %> <span class="text-muted">${starsValue}</span></c:otherwise>
                                </c:choose>
                            </div>
                            <div class="mt-auto d-flex justify-content-between align-items-end">
                                <div>
                                    <div class="price">Rs. <fmt:formatNumber value="${v.calculateDailyRate()}" pattern="#,##0"/></div>
                                    <div class="small text-muted">per day</div>
                                </div>
                                <a href="${ctx}/vehicles/view?id=${v.id}" class="btn btn-outline-primary btn-sm">View details</a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
