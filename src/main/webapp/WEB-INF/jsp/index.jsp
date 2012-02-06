<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<link href="compsoc.css" rel="stylesheet" type="text/css" />
<title>UWCS Music Server</title>
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
