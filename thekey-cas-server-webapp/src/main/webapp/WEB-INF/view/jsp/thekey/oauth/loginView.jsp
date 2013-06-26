<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_OAuth_SignIn" scope="page" />
<c:set var="selectedMenu" value="signin" scope="request" />
<c:set var="showLanguages" value="true" scope="page" />
<c:set var="helpJsp" value="../help/oauth/login.jsp" scope="request" />

<c:set var="message_title" value="oauth.signin.title" scope="page" />
<c:set var="message_header" value="oauth.signin.notice" scope="page" />

<%@ include file="../includes/top.jsp" %>

	<form:errors path="credentials">
		<div class="errors">
			<p><form:errors path="credentials"/></p>
		</div> <!-- .errors -->
	</form:errors>

	<form:form modelAttribute="credentials" cssClass="minHeight" acceptCharset="utf-8" autocomplete="off">
		<input type="hidden" name="execution" value="${flowExecutionKey}" />
		<input type="hidden" name="lt" value="${loginTicket}" />
		<div class="section">
			<p><spring:message code="oauth.signin.message"/></p>
			<div class="group">
				<label><spring:message code="oauth.signin.label.username"/></label><br/>
				<form:input type="email" cssClass="form_text auto-focus" path="username" tabindex="1" placeholder="<spring:message code="oauth.signin.placeholder.username"/>"/><br/>
				<form:errors path="username">
					<span class="form_error"><form:errors path="username"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
			<div class="group">
				<label><spring:message code="oauth.signin.label.password"/></label><br/>
				<form:password cssClass="form_text" size="25" tabindex="2" path="password"  htmlEscape="true" autocomplete="off" placeholder="<spring:message code="oauth.signin.placeholder.password"/>" /><br/>
				<form:errors path="password">
					<span class="form_error"><form:errors path="password"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
		</div> <!-- .section -->
		<div class="submit">
			<input class="form_submit" tabindex="3" type="submit" name="_eventId_submit" value="<spring:message code="oauth.signin.button.continue"/>" />
			<input class="form_cancel" tabindex="4" type="submit" name="_eventId_cancel" value="<spring:message code="oauth.signin.button.cancel"/>" />
		</div> <!-- .submit -->
	</form:form>

<%@ include file="../includes/bottom.jsp" %>
