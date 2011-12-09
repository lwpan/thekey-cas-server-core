<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="includes/commonVars.jsp" %>
<c:set var="title" value="logout.title" scope="request" />
<c:set var="bodyClasses" value="${bodyClasses} page_logout" scope="request" />
<c:set var="helpJsp" value="help/logout.jsp" scope="request" />
<c:set var="useAutoFocus" value="false" scope="request" />

<c:set var="message_header" value="logoutsuccessful" scope="page" />

<%@ include file="includes/top.jsp" %>

	<form id="command" class="minHeight" action="${loginUri}" method="get">
		<div class="section">
			<p class="message-first"><spring:message code="logout.message"/></p>
			<p class="message-last"><spring:message code="common.securityinfo"/></p>
			<div class="submit">
				<input class="form_submit" type="submit" tabindex="1" value="<spring:message code="logout.button.submit"/>" />
			</div> <!-- .submit -->
		</div>
	</form>

<%@ include file="includes/bottom.jsp" %>
