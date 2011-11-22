<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ include file="includes/commonVars.jsp" %>
<c:set var="title" value="logout.title" scope="request" />
<c:set var="helpJsp" value="help/logout.jsp" scope="request" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<%@ include file="includes/htmlHead.jsp" %>
<body class="page_logout">
	<%@ include file="includes/allHeadings.jsp" %>
	<%@ include file="includes/menu.jsp" %>
		
	<div class="content">

		<div class="content_header">
			<p><spring:message code="logoutsuccessful"/></p>
		</div>

		<div class="mainContent">
			<form id="command" class="minHeight" action="${loginUri}" method="get">
				<p><spring:message code="logout.message"/></p>
				<div class="submit">
					<input class="form_submit" type="submit" tabindex="1" value="<spring:message code="logout.button.submit"/>" />
				</div>
			</form>
		</div>
		
		<div class="content_footer"><img class="logo" src="<c:out value="${logoUri}"/>" alt="The Key Logo"/>
		
			<%@ include file="includes/securityNote.jsp" %>
		
			<div class="clear"></div>
		</div>

	</div>
	
	<%@ include file="includes/allFooters.jsp" %>

</body>
</html>
