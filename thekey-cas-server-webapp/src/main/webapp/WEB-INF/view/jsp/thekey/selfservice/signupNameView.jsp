<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="helpJsp" value="../help/signup/name.jsp" scope="request" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title><spring:message code="signup.name.title"/></title>
		<%@ include file="../includes/htmlHead.jsp"%>
	</head>
<body class="page_SignUp_Name">
	<c:set var="menu_signup" value="selected" scope="page" />

	<%@ include file="../includes/allHeadings.jsp" %>
	<%@ include file="../includes/menu.jsp" %>
	
	<div class="content">	
		<div class="content_header">
			<p><spring:message code="signup.name.notice"/></p>
		</div>
		
		<div class="mainContent">
			<form:form commandName="user" cssClass="minHeight" acceptCharset="utf-8">
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
					<input class="form_submit" tabindex="3" type="submit" name="_eventId_submit" value="<spring:message code="signup.name.button.continue"/>" />
					<input class="form_previous" tabindex="4" type="submit" name="_eventId_previous" value="<spring:message code="signup.name.button.back"/>" />
					<input class="form_cancel" tabindex="5" type="submit" name="_eventId_cancel" value="<spring:message code="signup.name.button.cancel"/>" />
				</div>
			</form:form>
		</div>
		
		<div class="content_footer"><img class="logo" src="<c:out value="${logoUri}"/>" alt="The Key Logo"/><div class="clear"></div></div>
	</div>
	
	<%@ include file="../includes/allFooters.jsp" %>
</body>
</html>
