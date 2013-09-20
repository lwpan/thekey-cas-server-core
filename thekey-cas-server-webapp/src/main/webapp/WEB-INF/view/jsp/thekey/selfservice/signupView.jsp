<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_SignUp" scope="page" />
<c:set var="selectedMenu" value="signup" scope="request" />
<c:set var="helpJsp" value="../help/signup/email.jsp" scope="request" />

<c:set var="message_title" value="signup.title" scope="page" />
<c:set var="message_header" value="signup.notice" scope="page" />

<c:set var="messageAccountDetailsLink" scope="page">
	<a href="<c:out value="${accountDetailsUri}" />"><spring:message code="signup.email.message.accountdetails.linktext" /></a>
</c:set>

<%@ include file="../includes/top.jsp" %>

	<form:form commandName="user" cssClass="minHeight" acceptCharset="utf-8">
		<input type="hidden" name="execution" value="${flowExecutionKey}" />
		<div class="section">
			<p><spring:message code="signup.email.message" arguments="${messageAccountDetailsLink}" /></p>
			<div class="group">
				<label><spring:message code="signup.label.email"/></label><br/>
				<form:input type="email" cssClass="form_text auto-focus" tabindex="1" path="email"/><br/>
				<form:errors path="email">
					<span class="form_error"><form:errors path="email"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
		</div> <!-- .section -->
		<div class="section">
			<div class="group">
				<label><spring:message code="signup.name.label.firstname"/></label><br/>
				<form:input cssClass="form_text auto-focus" tabindex="2" path="firstName"/><br/>
				<form:errors path="firstName">
					<span class="form_error"><form:errors path="firstName"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
			<div class="group">
				<label><spring:message code="signup.name.label.lastname"/></label><br/>
				<form:input cssClass="form_text" tabindex="3" path="lastName"/><br/>
				<form:errors path="lastName">
					<span class="form_error"><form:errors path="lastName"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
		</div> <!-- .section -->
		<div class="section">
			<p class="note"><spring:message code="selfserve.accountdetails.message.line1"/>&nbsp;<spring:message code="selfserve.accountdetails.message.line2"/></p>
			<div class="group">
				<label><spring:message code="selfserve.accountdetails.label.password"/></label><br/>
				<form:password cssClass="form_text" size="25" tabindex="4" path="password"  htmlEscape="true" autocomplete="off" /><br/>
				<form:errors path="password">
					<span class="form_error"><form:errors path="password"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
			<div class="group">
				<label><spring:message code="selfserve.accountdetails.label.confirmpassword"/></label><br/>
				<form:password cssClass="form_text" size="25" tabindex="5" path="retypePassword"  htmlEscape="true" autocomplete="off" /><br/>
				<form:errors path="retypePassword">
					<span class="form_error"><form:errors path="retypePassword"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
		</div> <!-- .section -->
		<div class="submit">
			<input class="form_submit" tabindex="6" type="submit" name="_eventId_submit" value="<spring:message code="signup.name.button.continue"/>" />
			<input class="form_cancel" tabindex="7" type="submit" name="_eventId_cancel" value="<spring:message code="signup.name.button.cancel"/>" />
		</div> <!-- .submit -->
	</form:form>

<%@ include file="../includes/bottom.jsp" %>
