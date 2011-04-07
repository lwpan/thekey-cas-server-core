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
<title><spring:message code="signup.success.title"/></title>
<link href="brand/common/genericview.css" type="text/css" rel="stylesheet"/>
<%@ include file="includes/customCss.jsp"  %>
<script src="brand/common/jquery.js" type="text/javascript"></script>  
<script src="brand/common/helpPopup.js" type="text/javascript"></script>
</head>

<body class="page_SignUp_Success">
	<c:set var="menu_signup" value="selected" scope="page" />
	<c:set var="help_file" value="Help_SignUp_Success.jsp" scope="page" />

	<%@ include file="includes/allHeadings.jsp" %>
	<%@ include file="includes/menu.jsp" %>
	
	<div class="content">
		
		<div class="content_header">
			<p><spring:message code="signup.success.notice"/></p>
		</div>
		
		<div class="mainContent">
			
			<form:form commandName="user" cssClass="minHeight">
				<div class="section">
					<p><spring:message code="signup.success.message"/></p>
					<p><spring:message code="signup.success.message.line1"/></p>
					<ul>
						<li><spring:message code="signup.success.message.line2"/></li>
						<span><spring:message code="signup.success.message.line3"/></span>
						<li><spring:message code="signup.success.message.line4"/></li>
					</ul>
					<p><spring:message code="signup.success.message.line5"/></p>
				</div>
				<div class="submit">
					<input class="form_submit" type="submit" tabindex="1" name="_cancel" value="<spring:message code="signup.success.button.continue"/>" />
				</div>
			</form:form>
			
		</div>
		
		<div class="content_footer"><img class="logo" src="brand/common/images/logo_thekey.png" alt="The Key Logo"/><div class="clear"></div></div>
		
	</div>

	<%@ include file="includes/allFooters.jsp" %>

</body>

</html>