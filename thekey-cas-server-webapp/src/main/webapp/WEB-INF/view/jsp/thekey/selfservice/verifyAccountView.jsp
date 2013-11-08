<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="keyfn" uri="/WEB-INF/tags.tld" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_SelfService_VerifyAccount" scope="request" />
<c:set var="selectedMenu" value="signin" scope="request" />

<c:set var="message_title" value="selfservice.page.verifyaccount.title" scope="page" />
<c:set var="message_header" value="selfservice.page.verifyaccount.notice" scope="page" />

<c:set var="args_steps" value="${keyfn:push(args_steps, selfservice.email)}" scope="page" />

<%@ include file="../includes/top.jsp" %>

	<form:errors path="selfservice">
		<div class="errors">
			<p><form:errors path="selfservice"/></p>
		</div> <!-- .errors -->
	</form:errors>

	<form:form modelAttribute="selfservice" cssClass="minHeight" acceptCharset="utf-8">
		<input type="hidden" name="execution" value="${flowExecutionKey}" />
		<input type="hidden" name="lt" value="${loginTicket}" />
		<div class="section">
			<ol>
				<li><spring:message code="selfservice.page.verifyaccount.steps.step1" arguments="${args_steps}"/></li>
				<li><spring:message code="selfservice.page.verifyaccount.steps.step2" arguments="${args_steps}" /></li>
				<li><spring:message code="selfservice.page.verifyaccount.steps.step3" arguments="${args_steps}" /></li>
				<li><spring:message code="selfservice.page.verifyaccount.steps.step4" arguments="${args_steps}" /></li>
			</ol>
<%--
			<div class="group">
				<label><spring:message code="selfservice.page.verifyaccount.label.key"/></label><br/>
				<form:input cssClass="form_text auto-focus" path="key" tabindex="1"/><br/>
				<form:errors path="key">
					<span class="form_error"><form:errors path="key"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
--%>
		</div> <!-- .section -->
		<div class="submit">
<%-- 			<input class="form_submit" tabindex="3" type="submit" name="_eventId_verify" value="<spring:message code="selfservice.page.verifyaccount.button.verify"/>" /> --%>
			<input class="form_button" tabindex="1" type="submit" name="_eventId_resend" value="<spring:message code="selfservice.page.verifyaccount.button.resend"/>" />
			<input class="form_cancel" tabindex="2" type="submit" name="_eventId_cancel" value="<spring:message code="selfservice.page.verifyaccount.button.cancel"/>" />
		</div> <!-- .submit -->
	</form:form>

<%@ include file="../includes/bottom.jsp" %>
