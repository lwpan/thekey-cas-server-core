<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_SelfServe_SignIn" scope="page" />
<c:set var="selectedMenu" value="account" scope="request" />
<c:set var="helpJsp" value="../help/selfservice/login.jsp" scope="request" />

<c:set var="message_title" value="selfserve.signin.title" scope="page" />
<c:set var="message_header" value="selfserve.signin.notice" scope="page" />

<%@ include file="../includes/top.jsp" %>

	<form:errors path="user">
		<div class="errors">
			<p><form:errors path="user"/></p>
		</div> <!-- .errors -->
	</form:errors>

	<form:form modelAttribute="user" cssClass="minHeight" acceptCharset="utf-8">
		<input type="hidden" name="execution" value="${flowExecutionKey}" />
		<div class="section">
			<p><spring:message code="selfserve.signin.message"/></p>
			<div class="group">
				<label><spring:message code="selfserve.signin.label.username"/></label><br/>
				<form:input type="email" cssClass="form_text auto-focus" path="email" tabindex="1"/><br/>
				<form:errors path="email">
					<span class="form_error"><form:errors path="email"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
			<div class="group">
				<label><spring:message code="selfserve.signin.label.password"/></label><br/>
				<form:password cssClass="form_text" size="25" tabindex="2" path="password"  htmlEscape="true" autocomplete="off" /><br/>
				<form:errors path="password">
					<span class="form_error"><form:errors path="password"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
		</div> <!-- .section -->
		<div class="submit">
			<input class="form_submit" tabindex="3" type="submit" name="_eventId_signin" value="<spring:message code="selfserve.signin.button.continue"/>" />
			<input class="form_cancel" tabindex="4" type="submit" name="_eventId_cancel" value="<spring:message code="selfserve.signin.button.cancel"/>" />
		</div> <!-- .submit -->
	</form:form>

<%@ include file="../includes/bottom.jsp" %>
