<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
	<%@ include file="header.html" %>
		<div id="menu">
				<h2/> Songs queued by ${user} <h2/>
				<c:if test="${not empty error}" >
					Error: ${error}
				</c:if>
				
				<c:forEach items="${queued}" var="qi">
						<div class="package">
							<span class="title">
								${qi}
							</span>
						</div>
						<br />
				</c:forEach>
		</div>
<%@ include file="footer.html" %>