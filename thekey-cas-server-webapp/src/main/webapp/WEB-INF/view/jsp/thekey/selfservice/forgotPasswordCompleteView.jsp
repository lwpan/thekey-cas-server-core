<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_SelfServe_ForgotPasswordComplete" scope="page" />
<c:set var="selectedMenu" value="account" scope="request" />
<c:set var="helpJsp" value="../help/selfservice/forgotPasswordComplete.jsp" scope="request" />
<c:set var="useAutoFocus" value="false" scope="request" />

<c:set var="message_title" value="selfserve.forgotpassword.complete.title" scope="page" />
<c:set var="message_header" value="selfserve.forgotpassword.complete.notice" scope="page" />

<%@ include file="../includes/top.jsp" %>

	<form id="command" class="minHeight" action="${loginUri}" method="get">
		<div class="section">
			<p class="message-first"><spring:message code="selfserve.forgotpassword.complete.message.line1"/></p>
			<p class="message-mid message-mid1"><spring:message code="selfserve.forgotpassword.complete.message.line2"/></p>
			<p class="message-mid message-mid2"><spring:message code="selfserve.forgotpassword.complete.message.line3"/></p>
			<p class="message-mid message-mid3"><spring:message code="selfserve.forgotpassword.complete.message.line4"/></p>
			<p class="message-last"><spring:message code="selfserve.forgotpassword.complete.message.line5"/></p>
		</div> <!-- .section -->
		<div class="submit">
			<input class="form_submit" type="submit" tabindex="1" value="<spring:message code="selfserve.forgotpassword.complete.button.continue"/>" />
		</div> <!-- .submit -->
	</form>

<%@ include file="../includes/bottom.jsp" %>
