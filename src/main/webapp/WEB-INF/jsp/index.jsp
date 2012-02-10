<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
	<head>
		<link href="compsoc.css" rel="stylesheet" type="text/css" />
		<title>UWCS Music Server</title>
	</head>
	<body>
		Hello,World!
	<c:if test="${not empty username}">
		<font color="blue">Currently logged in s <c:out value="${username}"/><br />
		</font>
	</c:if>	
		<c:forEach items="${queue}" var="qi">
			${qi.userName} ${qi.bucket} ${qi.position} <br />
		</c:forEach>
	</body>
</html>