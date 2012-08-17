<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_linkFederatedIdentity" scope="page" />
<c:set var="selectedMenu" value="signin" scope="request" />
<c:set var="helpJsp" value="../help/linkFederatedIdentity.jsp" scope="request" />

<c:set var="message_title" value="selfservice.linkidentities.title" scope="page" />
<c:set var="message_header" value="selfservice.linkidentities.notice" scope="page" />

<%@ include file="../includes/top.jsp" %>

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
			</div> <!-- .group -->
		</div> <!-- .section -->
		<div class="submit">
			<input name="_eventId_linkExisting" class="form_submit" type="submit" tabindex="3" value="<spring:message code="login.button.submit"/>" />
		</div> <!-- .submit -->

		<div class="section">
			<div class="submit">
				<input name="_eventId_createNew" class="form_submit" type="submit" tabindex="4" value="<spring:message code="selfservice.linkidentities.button.createnew" />" />
			</div>
		</div>
	</form:form>

<%@ include file="../includes/bottom.jsp" %>
