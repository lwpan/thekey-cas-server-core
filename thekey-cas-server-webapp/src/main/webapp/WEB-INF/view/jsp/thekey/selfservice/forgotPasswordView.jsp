<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="helpJsp" value="../help/selfservice/forgotPassword.jsp" scope="request" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title><spring:message code="selfserve.forgotpassword.title" /></title>
		<%@ include file="../includes/htmlHead.jsp"%>
	</head>
<body class="page_SelfServe_ForgotPassword" onLoad="setFocus(0,0);">
	<c:set var="menu_account" value="selected" scope="page" />
	
	<%@ include file="../includes/allHeadings.jsp" %>
	<%@ include file="../includes/menu.jsp" %>
	
	<div class="content">
		<div class="content_header">
			<p><spring:message code="selfserve.forgotpassword.notice"/></p>
		</div>
		
		<div class="mainContent">
			<form:errors path="user">
				<div class="errors">
					<p><form:errors path="user"/></p>
				</div>
			</form:errors>
			<form:form modelAttribute="user" cssClass="minHeight" acceptCharset="utf-8">
				<div class="section">
					<p><spring:message code="selfserve.forgotpassword.message"/></p>
					<div class="group">
						<label><spring:message code="selfserve.forgotpassword.label.username"/></label><br/>
						<form:input cssClass="form_text" tabindex="1" path="username"/><br/>
						<form:errors path="username">
							<span class="form_error"><form:errors path="username"/><br/></span>
						</form:errors>
					</div>
				</div>
				<div class="submit">
					<input class="form_submit" type="submit" tabindex="2" name="_eventId_submit" value="<spring:message code="selfserve.forgotpassword.button.continue"/>" />
					<input class="form_cancel" type="submit" tabindex="3" name="_eventId_cancel" value="<spring:message code="selfserve.forgotpassword.button.cancel"/>" />
				</div>
			</form:form>
		</div>
		
		<div class="content_footer"><img class="logo" src="<c:out value="${logoUri}"/>" alt="The Key Logo"/><div class="clear"></div></div>
	</div>
		
	<%@ include file="../includes/allFooters.jsp" %>
</body>
</html>