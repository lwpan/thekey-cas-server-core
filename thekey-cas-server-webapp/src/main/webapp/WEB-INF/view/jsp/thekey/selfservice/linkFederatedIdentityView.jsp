<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_linkFederatedIdentity" scope="request" />
<c:set var="selectedMenu" value="signin" scope="request" />
<c:set var="includeJquery" value="true" scope="request" />

<c:set var="message_title" value="selfservice.linkidentities.title" scope="page" />
<c:set var="message_header" value="selfservice.linkidentities.notice" scope="page" />

<%-- show the link existing form whenever there is an error --%>
<c:set var="showLinkExistingForm" value="false" scope="page" />
<spring:hasBindErrors name="${commandName}">
	<c:set var="showLinkExistingForm" value="true" scope="page" />
</spring:hasBindErrors>

<%@ include file="../includes/top.jsp" %>

	<form:errors path="${commandName}">
		<div class="errors">
			<div class=".errors-in">
				<p><form:errors path="${commandName}"/></p>
			</div> <!-- .errors-in -->
		</div> <!-- .errors -->
	</form:errors>

	<div class="section">
		<p class="message-first"><spring:message code="relay.selfservice.linkidentities.message.line1" /></p>
		<p class="message-last"><spring:message code="relay.selfservice.linkidentities.message.line2" /></p>
	</div>

	<form:form action="${requestUri}" commandName="${commandName}" cssClass="minHeight">
		<input type="hidden" name="lt" value="${loginTicket}" />
		<input type="hidden" name="execution" value="${flowExecutionKey}" />

		<div id="linkOrCreateForm" class="section"<c:if test="${showLinkExistingForm}"> style="display:none;"</c:if>>
			<div class="submit">
				<input name="_eventId_createNew" class="form_submit" type="submit" tabindex="4" value="<spring:message code="selfservice.linkidentities.button.createnew" />" />
				<input type="button" class="form_submit" onclick="$('#linkExistingForm').show(); $('#linkOrCreateForm').hide();" value="<spring:message code="selfservice.linkidentities.button.showlinkform" />" />
			</div>
		</div>
	</form:form>

	<form:form action="${requestUri}" commandName="${commandName}" cssClass="minHeight">
		<input type="hidden" name="lt" value="${loginTicket}" />
		<input type="hidden" name="execution" value="${flowExecutionKey}" />

		<div id="linkExistingForm" class="section"<c:if test="${not showLinkExistingForm}"> style="display:none;"</c:if>>
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

			<div class="submit">
				<input name="_eventId_linkExisting" class="form_submit" type="submit" tabindex="3" value="<spring:message code="selfservice.linkidentities.button.linkidentities" />" />
				<input type="button" class="form_cancel" onclick="$('#linkExistingForm').hide(); $('#linkOrCreateForm').show();" value="<spring:message code="selfservice.linkidentities.button.showcreateform" />" />
			</div> <!-- .submit -->
		</div> <!-- .section -->
	</form:form>

<%@ include file="../includes/bottom.jsp" %>
