<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="header.html" %>
	<div class="package">
	<h2>Login Page</h2>
	<c:if test="${not empty param.error}">
		<font color="red"> Your login attempt was not successful, try again.<br />
		<br /> Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />.
		</font>
	</c:if>
	<form name="login" action="<c:url value='/j_spring_security_check'/>" method="POST">
		<table>
			<tr>
				<td>User:</td>
				<td><input type='text' name='j_username' /></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type='password' name='j_password' /></td>
			</tr>
			<tr>
				<td colspan='2'><input name="Login" type="submit"></td>
			</tr>
		</table>
	</form>
	</div>
<%@ include file="footer.html" %>