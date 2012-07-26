<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
	<%@ include file="header.html" %>
		<div class="menu">
			<div class="package">
			<h2>Music server</h2>
			Submit bug reports and browse the source <a href="http://github.com/mrwilson/aoko">here</a>!</br>
			Please report any problems to an admin. The admins are:<br/>
			<c:forEach items="${admins}" var="admin">
				${admin}<br/>
			</c:forEach>	
		</div>
	</div>
<%@ include file="footer.html" %>
