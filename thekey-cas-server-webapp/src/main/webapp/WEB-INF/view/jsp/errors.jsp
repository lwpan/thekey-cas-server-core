<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="thekey/includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_error" scope="request" />
<c:set var="useAutoFocus" value="false" scope="request" />

<c:set var="message_title" value="error.exception.title" scope="page" />
<c:set var="message_header" value="error.exception.notice" scope="page" />

<%@ include file="thekey/includes/top.jsp" %>

	<div class="section">
		<p class="message-first"><spring:message code="error.exception.message"/></p>
		<p class="message-last"><spring:message code="error.exception.details"/></p>
		<%
			if (exception != null) {
				exception.printStackTrace();
				while (exception instanceof javax.servlet.ServletException) {
					exception = ((javax.servlet.ServletException) exception).getRootCause();
				}
				pageContext.setAttribute("exception", exception);
			}
		%>
<%--
		<p class="message-last"><c:out value="${exception.message}" /></p>
--%>
	</div> <!-- .section -->

<%@ include file="thekey/includes/bottom.jsp" %>
