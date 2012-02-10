<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
	<head>
		<link href="compsoc.css" rel="stylesheet" type="text/css" />
		<title>UWCS Music Server</title>
	</head>
	<body>
		Hello,World!
		<c:if test="${not empty username}">
			<font color="blue"><br />Currently logged in s <c:out value="${username}"/><br />
			</font>
		</c:if>	
		<c:forEach items="${queue}" var="qi">
			${qi.userName} ${qi.bucket} ${qi.position} <br />
		</c:forEach>
		<h1>Please upload a file</h1>
        <form method="post" commandName="FORM" action="/upload/" enctype="multipart/form-data">
            <input type="file" name="file"/>
            <input type="submit"/>
        </form>
	</body>
</html>