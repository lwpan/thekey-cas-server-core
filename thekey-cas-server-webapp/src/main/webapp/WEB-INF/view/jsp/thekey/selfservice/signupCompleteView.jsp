<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="title" value="signup.success.title" scope="request" />
<c:set var="helpJsp" value="../help/signup/success.jsp" scope="request" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<%@ include file="../includes/htmlHead.jsp" %>
<body class="page_SignUp_Success">
	<c:set var="menu_signup" value="selected" scope="page" />

	<%@ include file="../includes/allHeadings.jsp" %>
	<%@ include file="../includes/menu.jsp" %>
	
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
						<li><spring:message code="signup.success.message.line3"/></li>
						<li><spring:message code="signup.success.message.line4"/></li>
					</ul>
					<p><spring:message code="signup.success.message.line5"/></p>
				</div>
				<div class="submit">
					<input class="form_submit" type="submit" tabindex="1" name="_cancel" value="<spring:message code="signup.success.button.continue"/>" />
				</div>
			</form:form>
		</div>
		
		<div class="content_footer"><img class="logo" src="<c:out value="${logoUri}"/>" alt="The Key Logo"/><div class="clear"></div></div>
	</div>

	<%@ include file="../includes/allFooters.jsp" %>
</body>
</html>
