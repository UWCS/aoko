<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
	<%@ include file="header.html" %>
	
<sec:authorize access="isAuthenticated()">
	<div class="package">
			
		<div class="submitform">
			File Upload
		        <form method="post" commandName="FORM" action="/submit/upload" enctype="multipart/form-data">
		            <input type="file" name="file"/><br/>
		            <input type="submit"/>
		        </form>
		</div>
			
    	<div class="submitform">
	    	YouTube
    	    <form method="post" action="/submit/youtube">
        	   <input type="text" name="url"/><br/>
		       <input type="submit"/>
		   	</form>
		</div>
		        
	</div>
</sec:authorize>
		
		<br />

		<div id="menu">
			<c:forEach items="${queued}" var="qi">
					<div class="package">
						<div class="info">
								<div class="track-data">
									<a href="${qi.url}">${qi.url}</a> - ${qi.queuedBy} - ${qi.state}
								</div>
							</div>
					</div>
			</c:forEach>
		</div>
<%@ include file="footer.html" %>
