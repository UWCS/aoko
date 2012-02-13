<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
	<head>
		<link href="compsoc.css" rel="stylesheet" type="text/css" />
		<title>UWCS Music Server</title>
	</head>
	<body>
		Hello,World!<br />
		<c:choose>
		  <c:when test="${not empty username}">
		    <font color="blue"><br />Currently logged in as - <strong><c:out value="${username}"/></strong>
			</font>
			</c:when>
		   <c:otherwise>
		    You aren't logged in! <a href="<c:url value="/login/"/>">Login</a> or <a href="<c:url value="/login/register"/>">register</a>
		  </c:otherwise>
		</c:choose>
		<br />
		<c:forEach items="${queue}" var="qi">
			${qi.toString()} <br />
		</c:forEach>
		<sec:authorize access="isAuthenticated()">
		<h1>Upload a file</h1>
        <form method="post" commandName="FORM" action="/submit/upload" enctype="multipart/form-data">
            <input type="file" name="file"/>
            <input type="submit"/>
        </form><br />
    <h1>YouTubeDl</h1>
        <form method="post" action="/submit/youtube">
            <input type="text" name="url"/>
            <input type="submit"/>
        </form>
		</sec:authorize>
	</body>
</html>