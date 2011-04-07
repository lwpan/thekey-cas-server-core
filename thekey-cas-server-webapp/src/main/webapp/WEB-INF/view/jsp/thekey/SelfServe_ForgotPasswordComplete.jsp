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
<title><spring:message code="selfserve.forgotpassword.complete.title"/></title>
<link href="brand/common/genericview.css" type="text/css" rel="stylesheet"/>
<%@ include file="includes/customCss.jsp"  %>
<script src="brand/common/jquery.js" type="text/javascript"></script>
<script src="brand/common/helpPopup.js" type="text/javascript"></script>
</head>

<body class="page_SelfServe_ForgotPasswordComplete">
	<c:set var="menu_account" value="selected" scope="page" />
	<c:set var="help_file" value="Help_SelfServe_ForgotPasswordComplete.jsp" scope="page" />

	<%@ include file="includes/allHeadings.jsp" %>
	<%@ include file="includes/menu.jsp" %>
	
	<div class="content">
		
		<div class="content_header">
			<p><spring:message code="selfserve.forgotpassword.complete.notice"/></p>
		</div>
		
		<div class="mainContent">
		
			<form:form cssClass="minHeight">
				<div class="section">
					<p><spring:message code="selfserve.forgotpassword.complete.message.line1"/></p>
					<p><spring:message code="selfserve.forgotpassword.complete.message.line2"/></p>
					<p><spring:message code="selfserve.forgotpassword.complete.message.line3"/></p>
					<p><spring:message code="selfserve.forgotpassword.complete.message.line4"/></p>
					<p><spring:message code="selfserve.forgotpassword.complete.message.line5"/></p>
				</div>
				<div class="submit">
					<input class="form_submit" tabindex="1" type="submit" name="_eventId_submit" value="<spring:message code="selfserve.forgotpassword.complete.button.continue"/>" />
				</div>
			</form:form>
			
		</div>
		
		<div class="content_footer"><img class="logo" src="brand/common/images/logo_thekey.png" alt="The Key Logo"/><div class="clear"></div></div>
		
	</div>

	<%@ include file="includes/allFooters.jsp" %>


</body>

</html>