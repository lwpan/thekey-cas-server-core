<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_OAuth_Authorize" scope="page" />
<c:set var="selectedMenu" value="signin" scope="request" />

<!-- TODO: update these values -->
<c:set var="helpJsp" value="../help/selfservice/login.jsp" scope="request" />

<c:set var="message_title" value="selfserve.signin.title" scope="page" />
<c:set var="message_header" value="selfserve.signin.notice" scope="page" />
<!-- END TODO -->

<%@ include file="../includes/top.jsp" %>

	<form:form cssClass="minHeight" acceptCharset="utf-8">
		<input type="hidden" name="execution" value="${flowExecutionKey}" />
		<input type="hidden" name="lt" value="${loginTicket}" />

		<div class="section">
			<p class="message-first"><spring:message code="oauth.authorize.message" arguments="${oauthClient.name}" /></p>
			<ul>
				<li><spring:message code="oauth.authorize.scope.fullticket" /></li>
			</ul>
			<p class="message-last"><c:out value="${oauthClient.description}" /></p>
		</div> <!-- .section -->

		<div class="submit">
			<input class="form_submit" tabindex="1" type="submit" name="_eventId_authorize" value="<spring:message code="oauth.authorize.button.authorize"/>" />
			<input class="form_cancel" tabindex="2" type="submit" name="_eventId_cancel" value="<spring:message code="oauth.authorize.button.cancel"/>" />
		</div> <!-- .submit -->
	</form:form>

<%@ include file="../includes/bottom.jsp" %>
