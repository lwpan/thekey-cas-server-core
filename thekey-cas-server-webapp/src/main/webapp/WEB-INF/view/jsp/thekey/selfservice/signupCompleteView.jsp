<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_SignUp_Success" scope="request" />
<c:set var="selectedMenu" value="signup" scope="request" />
<c:set var="helpJsp" value="../help/signup/success.jsp" scope="request" />
<c:set var="useAutoFocus" value="false" scope="request" />

<c:set var="message_title" value="signup.success.title" scope="page" />
<c:set var="message_header" value="signup.success.notice" scope="page" />

<%@ include file="../includes/top.jsp" %>

	<form:form commandName="user" cssClass="minHeight">
		<div class="section">
			<p class="message-first"><spring:message code="signup.success.message"/></p>
			<p class="message-last"><spring:message code="signup.success.message.line1"/></p>
			<ul>
				<li><spring:message code="signup.success.message.line2"/></li>
				<li><spring:message code="signup.success.message.line3"/></li>
				<li><spring:message code="signup.success.message.line4"/></li>
			</ul>
			<p><spring:message code="signup.success.message.line5"/></p>
		</div> <!-- .section -->
		<div class="submit">
			<input class="form_submit" type="submit" tabindex="1" name="_cancel" value="<spring:message code="signup.success.button.continue"/>" />
		</div> <!-- .submit -->
	</form:form>

<%@ include file="../includes/bottom.jsp" %>
