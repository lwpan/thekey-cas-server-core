<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="helpJsp" value="../help/signup/email.jsp" scope="request" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title><spring:message code="signup.title"/></title>
		<%@ include file="../includes/htmlHead.jsp"%>
	</head>
<body class="page_SignUp_Email" onLoad="setFocus(0,0);">
	<c:set var="menu_signup" value="selected" scope="page" />

	<%@ include file="../includes/allHeadings.jsp" %>
	<%@ include file="../includes/menu.jsp" %>
	
	<div class="content">
		<div class="content_header"><p><spring:message code="signup.notice"/></p></div>
		
		<div class="mainContent">
			<form:form commandName="user" cssClass="minHeight" acceptCharset="utf-8">
				<div class="section">
					<div class="group">
						<label><spring:message code="signup.label.email"/></label><br/>
						<form:input cssClass="form_text" tabindex="1" path="username"/><br/>
						<form:errors path="username">
							<span class="form_error"><form:errors path="username"/><br/></span>
						</form:errors>
					</div>
				</div>
				<input type="hidden" name="_page" value="0"/>
				<div class="submit">
					<input class="form_submit" tabindex="2" type="submit" name="_target1" value="<spring:message code="signup.button.continue"/>" />
					<input class="form_cancel" tabindex="3" type="submit" name="_cancel" value="<spring:message code="signup.button.cancel"/>" />
				</div>
			</form:form>
		</div>
		
		<div class="content_footer"><img class="logo" src="<c:out value="${logoUri}"/>" alt="The Key Logo"/><div class="clear"></div></div>
	</div>
			
	<%@ include file="../includes/allFooters.jsp" %>
</body>
</html>