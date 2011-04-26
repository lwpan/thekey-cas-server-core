<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isELIgnored="false"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title><spring:message code="login.title"/></title>
<link href="themes/thekey/cas.css" type="text/css" rel="stylesheet"/>
<%@ include file="includes/customCss.jsp"  %>
<script src="themes/thekey/jquery.js" type="text/javascript"></script>
<script src="themes/thekey/helpPopup.js" type="text/javascript"></script>
</head>

<body class="page_login" onLoad="setFocus(0,0);">
	<c:set var="menu_signin" value="selected" scope="page" />
	<c:set var="help_file" value="help/login.jsp" scope="page" />
	
	<%@ include file="includes/allHeadings.jsp" %>
	<%@ include file="includes/menu.jsp" %>
	
	<div class="content">
	
		<div class="content_header">
			<p><spring:message code="login.notice"/></p>
		</div>
	
		<div class="mainContent">
	
			<form:errors path="${commandName}">
				<div class="errors">
					<p><form:errors path="${commandName}"/></p>
				</div>
			</form:errors>
			
			<form:form commandName="${commandName}" name="login_form" cssClass="minHeight">
				<input type="hidden" name="lt" value="${flowExecutionKey}" />
				<input type="hidden" name="_eventId" value="submit" />
				<div class="section">
					<div class="group">
						<label for="username"><spring:message code="login.label.username"/></label><br/>
						<form:input cssClass="form_text" path="username" tabindex="1"/><br/>
						<form:errors path="username"><span class="form_error"><form:errors path="username"/><br/></span></form:errors>
					</div>
					<div class="group">
						<label for="password"><spring:message code="login.label.password"/></label><br/>
						<form:password cssClass="form_text" size="25" tabindex="2" path="password"  htmlEscape="true" /><br/>
						<form:errors path="password"><span class="form_error"><form:errors path="password"/><br/></span></form:errors>
						<a href="selfservice.htm?target=displayForgotPassword"><spring:message code="login.forgotpassword"/></a>
					</div>
				</div>
				<div class="submit">
					<input class="form_submit" type="submit" tabindex="3" name="submit" value="<spring:message code="login.button.submit"/>" />
				</div>
			</form:form>
	
		</div>
	
		<div class="content_footer"><img class="logo" src="themes/thekey/images/logo_thekey.png" alt="The Key Logo"/>
	
			<%@ include file="includes/notes.jsp" %>

			<div class="clear"></div>
		</div>
		
	</div>
	
	<%@ include file="includes/languagesList.jsp" %>
	<%@ include file="includes/allFooters.jsp" %>
	
</body>

</html>
                                                              
