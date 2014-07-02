<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_error" scope="request" />
<c:set var="useAutoFocus" value="false" scope="request" />

<c:set var="message_title" value="error.communication.title" scope="page" />
<c:set var="message_header" value="error.communication.notice" scope="page" />

<%@ include file="includes/top.jsp" %>

	<div class="section">
		<p><spring:message code="error.communication.message"/></p>
	</div> <!-- .section -->

<%@ include file="includes/bottom.jsp" %>
