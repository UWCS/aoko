<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<body>
<html>
<body>
	<c:forEach items="${queue}" var="qi">
		${qi.userName} ${qi.bucket} ${qi.position} <br />
	</c:forEach>
</body>
</html>
</body>
</html>
