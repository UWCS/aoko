<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
	<%@ include file="header.html" %>
		Some kind of music server! 
		<sec:authorize access="isAuthenticated()">
			<a href="<c:url value="/logout"/>">Logout</a>
		</sec:authorize>
		<sec:authorize access="isAuthenticated()">
			<div id="sidebar">
				<ul>
					<li>File Upload</li>
			        <li>
				        <form method="post" commandName="FORM" action="/submit/upload" enctype="multipart/form-data">
				            <input type="file" name="file"/><br/>
				            <input type="submit"/>
				        </form>
				   	</li>
				<br /><br />
		    	<li>YouTubeDl</li>
		        <li><form method="post" action="/submit/youtube">
		            <input type="text" name="url"/>
		            <input type="submit"/>
		        </form></li>
		        </ul>
	        </div>
		</sec:authorize>
		<br />
		<c:choose>
		  	<c:when test="${not empty username}">
		    Currently logged in as - <strong><c:out value="${username}"/></strong>
			</c:when>
		   <c:otherwise>
		    You aren't logged in! <a href="<c:url value="/login/"/>">Login</a> or <a href="<c:url value="/login/register"/>">register</a>
		  </c:otherwise>
		</c:choose>
		<p>
			<div id="menu">
				<c:forEach items="${queue}" var="qi">
						<div class="package">
							<span class="title">
								<c:if test="${not empty qi.file.artLocation}">
								<img src="<c:url value="/resources/${qi.file.artLocation}"/>" height="100" width="100"/>
								</c:if>
								${qi} queued by ${qi.userName}
							</span>
							<span class="info">			
								<c:if test="${qi.userName == username}">
									<a href="<c:url value="/a/move/up/${qi.bucket}"/>">Up</a>
									<a href="<c:url value="/a/move/down/${qi.bucket}"/>">Down</a>
									<a href="<c:url value="/a/delete/${qi.bucket}"/>">Del</a>
								</c:if>
							</span>
						</div>
						<br />
				</c:forEach>
			</div>
<%@ include file="footer.html" %>