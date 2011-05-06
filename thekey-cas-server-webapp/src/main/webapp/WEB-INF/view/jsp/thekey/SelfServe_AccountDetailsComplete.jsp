<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isELIgnored="false"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><spring:message code="selfserve.complete.title"/></title>
<link href="themes/thekey/cas.css" type="text/css" rel="stylesheet"/>
<%@ include file="includes/customCss.jsp"  %>
<script src="themes/thekey/jquery.js" type="text/javascript"></script>
<script src="themes/thekey/helpPopup.js" type="text/javascript"></script>
</head>

<body class="page_SelfServe_Complete">
	<c:set var="menu_account" value="selected" scope="page" />
	<c:set var="help_file" value="Help_SelfServe_AccountDetailsComplete.jsp" scope="page" />

	<%@ include file="includes/allHeadings.jsp" %>
	<%@ include file="includes/menu.jsp" %>
	
	<div class="content">
	
		<div class="content_header">
			<p><spring:message code="selfserve.complete.notice"/></p>
		</div>
		
		<div class="mainContent">
			<form:form cssClass="minHeight">
				<div class="section">
					<p><spring:message code="selfserve.complete.message"/></p>
					<p><spring:message code="selfserve.complete.message2"/></p>
				</div>
				<div class="submit">
					<input class="form_submit" type="submit" tabindex="1" name="_eventId_submit" value="<spring:message code="selfserve.complete.button.continue"/>" />
				</div>
			</form:form>
		</div>
		
		<div class="content_footer"><img class="logo" src="themes/thekey/images/logo_thekey.png" alt="The Key Logo"/><div class="clear"></div></div>
		
	</div>

	<%@ include file="includes/allFooters.jsp" %>

</body>

</html>