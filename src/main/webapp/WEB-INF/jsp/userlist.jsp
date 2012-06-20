<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
	<%@ include file="header.html" %>
		<div class="menu">
				<h2> Songs queued by ${user}
				<sec:authorize access="hasRole('ROLE_ADMIN')">
					<a href="<c:url value="/admin/make/${user}"/>">Make Admin</a>
					<a href="<c:url value="/admin/remove/${user}"/>">Delete</a>
				</sec:authorize>
				
				</h2>
				<c:if test="${not empty error}" >
					Error: ${error}
				</c:if>
				
						
				<div class="package">
				<c:forEach items="${queued}" var="qi">
							<div class="info">
								<a href="<c:url value="/user/${qi.file.location}"/>">${qi}</a>
							</div>
						<br />
				</c:forEach>
				</div>
		</div>
<%@ include file="footer.html" %>