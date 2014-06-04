<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_SignUp_Name" scope="page" />
<c:set var="selectedMenu" value="signup" scope="request" />

<c:set var="message_title" value="signup.name.title" scope="page" />
<c:set var="message_header" value="signup.name.notice" scope="page" />

<%@ include file="../includes/top.jsp" %>

	<form:form commandName="user" cssClass="minHeight" acceptCharset="utf-8">
		<input type="hidden" name="execution" value="${flowExecutionKey}" />
		<div class="section">
			<p><spring:message code="signup.name.message"/></p>
			<div class="group">
				<label><spring:message code="signup.name.label.firstname"/></label><br/>
				<form:input cssClass="form_text auto-focus" tabindex="1" path="firstName"/><br/>
				<form:errors path="firstName">
					<span class="form_error"><form:errors path="firstName"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
			<div class="group">
				<label><spring:message code="signup.name.label.lastname"/></label><br/>
				<form:input cssClass="form_text" tabindex="2" path="lastName"/><br/>
				<form:errors path="lastName">
					<span class="form_error"><form:errors path="lastName"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
		</div> <!-- .section -->
		<div class="submit">
			<input class="form_submit" tabindex="3" type="submit" name="_eventId_submit" value="<spring:message code="signup.name.button.continue"/>" />
			<input class="form_previous" tabindex="4" type="submit" name="_eventId_previous" value="<spring:message code="signup.name.button.back"/>" />
			<input class="form_cancel" tabindex="5" type="submit" name="_eventId_cancel" value="<spring:message code="signup.name.button.cancel"/>" />
		</div> <!-- .submit -->
	</form:form>

<%@ include file="../includes/bottom.jsp" %>
