<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="helpJsp" value="../help/selfservice/forgotPasswordComplete.jsp" scope="request" />
<c:set var="menu_account" value="selected" scope="page" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title><spring:message code="selfserve.forgotpassword.complete.title"/></title>
		<%@ include file="../includes/htmlHead.jsp"%>
	</head>
<body class="page_SelfServe_ForgotPasswordComplete">
	<%@ include file="../includes/allHeadings.jsp" %>
	<%@ include file="../includes/menu.jsp" %>
	
	<div class="content">
		<div class="content_header">
			<p><spring:message code="selfserve.forgotpassword.complete.notice"/></p>
		</div>

		<div class="mainContent">
			<form id="command" class="minHeight" action="${loginUri}" method="get">
				<div class="section">
					<p><spring:message code="selfserve.forgotpassword.complete.message.line1"/></p>
					<p><spring:message code="selfserve.forgotpassword.complete.message.line2"/></p>
					<p><spring:message code="selfserve.forgotpassword.complete.message.line3"/></p>
					<p><spring:message code="selfserve.forgotpassword.complete.message.line4"/></p>
					<p><spring:message code="selfserve.forgotpassword.complete.message.line5"/></p>
				</div>
				<div class="submit">
					<input class="form_submit" type="submit" tabindex="1"
						value="<spring:message code="selfserve.forgotpassword.complete.button.continue"/>" />
				</div>
			</form>
		</div>

		<div class="content_footer"><img class="logo" src="<c:out value="${logoUri}"/>" alt="The Key Logo"/><div class="clear"></div></div>
	</div>

	<%@ include file="../includes/allFooters.jsp" %>
</body>
</html>
