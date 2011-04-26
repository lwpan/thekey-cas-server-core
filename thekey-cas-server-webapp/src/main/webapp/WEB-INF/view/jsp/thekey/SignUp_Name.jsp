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
<title><spring:message code="signup.name.title"/></title>
<link href="themes/thekey/cas.css" type="text/css" rel="stylesheet"/>
<%@ include file="includes/customCss.jsp"  %>
<script src="themes/thekey/jquery.js" type="text/javascript"></script>
<script src="themes/thekey/helpPopup.js" type="text/javascript"></script>
</head>

<body class="page_SignUp_Name">
	<c:set var="menu_signup" value="selected" scope="page" />
	<c:set var="help_file" value="Help_SignUp_Name.jsp" scope="page" />

	<%@ include file="includes/allHeadings.jsp" %>
	<%@ include file="includes/menu.jsp" %>
	
	<div class="content">	
			
		<div class="content_header">
			<p><spring:message code="signup.name.notice"/></p>
		</div>
		
		<div class="mainContent">
			
			<form:form commandName="user" cssClass="minHeight" acceptCharset="utf-8">
				<input type="hidden" name="_page" value="2"/>
				<div class="section">
					<p><spring:message code="signup.name.message"/></p>
					<div class="group">
						<label><spring:message code="signup.name.label.firstname"/></label><br/>
						<form:input cssClass="form_text" tabindex="1" path="firstName"/><br/>
						<form:errors path="firstName">
							<span class="form_error"><form:errors path="firstName"/><br/></span>
						</form:errors>
					</div>
					<div class="group">
						<label><spring:message code="signup.name.label.lastname"/></label><br/>
						<form:input cssClass="form_text" tabindex="2" path="lastName"/><br/>
						<form:errors path="lastName">
							<span class="form_error"><form:errors path="lastName"/><br/></span>
						</form:errors>
					</div>
				</div>
				<div class="submit">
					<input class="form_submit" tabindex="3" type="submit" name="_finish" value="<spring:message code="signup.name.button.continue"/>" />
					<input class="form_previous" tabindex="4" type="submit" name="_target0" value="<spring:message code="signup.name.button.back"/>" />
					<input class="form_cancel" tabindex="5" type="submit" name="_cancel" value="<spring:message code="signup.name.button.cancel"/>" />
				</div>
			</form:form>
	
		</div>
		
		<div class="content_footer"><img class="logo" src="themes/thekey/images/logo_thekey.png" alt="The Key Logo"/><div class="clear"></div></div>
		
	</div>
	
	<%@ include file="includes/allFooters.jsp" %>

</body>

</html>