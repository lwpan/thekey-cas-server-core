<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_SelfServe_Complete" scope="request" />
<c:set var="selectedMenu" value="account" scope="request" />
<c:set var="useAutoFocus" value="false" scope="request" />

<c:set var="message_title" value="selfserve.complete.title" scope="page" />
<c:set var="message_header" value="selfserve.complete.notice" scope="page" />

<%@ include file="../includes/top.jsp" %>

	<div class="section">
		<p class="message-first"><spring:message code="selfserve.complete.message" /></p>
	</div> <!-- .section -->
	<div class="submit">
		<input class="form_submit" type="button" tabindex="1" value="<spring:message code="selfserve.complete.button.continue"/>" onclick="window.location.href='<c:out value="${loginUri}" />'"/>
	</div> <!-- .submit -->

<%@ include file="../includes/bottom.jsp" %>
