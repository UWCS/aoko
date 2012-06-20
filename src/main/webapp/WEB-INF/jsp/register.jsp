<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="header.html" %>
	<div class="package">
	<h2>Register</h2>
	<c:if test="${not empty param.error}">
		<font color="red"> Error: <br />
		<br /> Reason: <c:out value="${param.error}" />.
		</font>
	</c:if>
	<form name="register" action="<c:url value='/login/register/'/>" method="POST">
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
				<td colspan='2'><input name="Register" type="submit"></td>
			</tr>
		</table>
	</form>
	</div>
<%@ include file="footer.html" %>