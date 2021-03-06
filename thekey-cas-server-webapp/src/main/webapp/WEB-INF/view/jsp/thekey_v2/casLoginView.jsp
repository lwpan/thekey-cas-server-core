<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="keyfn" uri="/WEB-INF/tags.tld" %>
<%@ include file="includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_login" scope="page" />
<c:set var="selectedMenu" value="signin" scope="request" />
<c:set var="includeFb" value="true" scope="request" />
<c:set var="showLanguages" value="true" scope="page" />
<c:set var="showMinorNav" value="true" scope="page" />

<c:set var="message_title" value="login.title" scope="page" />
<c:set var="message_header" value="login.notice.noservice" scope="page" />
<c:if test="${not empty serviceDomain}">
	<c:set var="message_header" value="login.notice" scope="page" />
	<c:set var="args_header" value="${keyfn:push(args_header, serviceDomain)}" scope="page" />
</c:if>

<%@ include file="includes/top.jsp" %>

	<form:errors path="${commandName}">
		<div class="errors">
			<div class=".errors-in">
				<p><form:errors path="${commandName}"/></p>
			</div> <!-- .errors-in -->
		</div> <!-- .errors -->
	</form:errors>

	<form:form action="${requestUri}" commandName="${commandName}" id="login_form" cssClass="minHeight">
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
				<div class="facebookLogin fb_button" style="background:none;">
					<div class="fb-login-button" data-length="long" data-scope="email" data-on-login="theKeyFacebookLogin('form#login_form', 'facebookSubmit');"></div>
				</div>
			</c:if>
			<span class="form_submit-wrap"><input class="form_submit" type="submit" tabindex="3" value="<spring:message code="login.button.submit"/>" /></span>
		</div> <!-- .submit -->
	</form:form>

<%@ include file="includes/bottom.jsp" %>
