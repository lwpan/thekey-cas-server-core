<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ include file="includes/commonVars.jsp" %>
<c:set var="title" value="login.title" scope="request" />
<c:set var="bodyClasses" value="${bodyClasses} page_login" scope="request" />
<c:set var="selectedMenu" value="signin" scope="request" />
<c:set var="includeFb" value="true" scope="request" />
<c:set var="showLanguages" value="true" scope="page" />
<c:set var="showMinorNav" value="true" scope="page" />
<c:set var="helpJsp" value="help/login.jsp" scope="request" />

<c:set var="message_header" value="login.notice.noservice" scope="page" />
<c:if test="${not empty serviceDomain}">
	<c:set var="message_header" value="login.notice" scope="page" />
	<c:set var="args_header" value="${serviceDomain}" scope="page" />
</c:if>

<%@ include file="includes/top.jsp" %>

	<form:errors path="${commandName}">
		<div class="errors">
			<div class=".errors-in">
				<p><form:errors path="${commandName}"/></p>
			</div> <!-- .errors-in -->
		</div> <!-- .errors -->
	</form:errors>

	<form:form commandName="${commandName}" id="login_form" cssClass="minHeight">
		<input type="hidden" name="lt" value="${loginTicket}" />
		<input type="hidden" name="execution" value="${flowExecutionKey}" />
		<input type="hidden" name="_eventId" value="submit" />
		<div class="section">
			<div class="group">
				<label for="username"><spring:message code="login.label.username"/></label><br/>
				<form:input cssClass="form_text auto-focus" path="username" tabindex="1"/><br/>
				<form:errors path="username"><span class="form_error"><form:errors path="username"/><br/></span></form:errors>
			</div> <!-- .group -->
			<div class="group">
				<label for="password"><spring:message code="login.label.password"/></label><br/>
				<form:password cssClass="form_text" size="25" tabindex="2" path="password"  htmlEscape="true" /><br/>
				<form:errors path="password"><span class="form_error"><form:errors path="password"/><br/></span></form:errors>
				<a href="<c:out value="${forgotPasswordUri}" />"><spring:message code="login.forgotpassword"/></a>
			</div> <!-- .group -->
		</div> <!-- .section -->
		<div class="submit">
			<c:if test="${includeFb}">
				<fb:thekey-login-button length="long" perms="email" form="form#login_form" action="facebookSubmit"></fb:thekey-login-button>
			</c:if>
			<span class="form_submit-wrap"><input class="form_submit" type="submit" tabindex="3" value="<spring:message code="login.button.submit"/>" /></span>
		</div> <!-- .submit -->
	</form:form>

<%@ include file="includes/bottom.jsp" %>
