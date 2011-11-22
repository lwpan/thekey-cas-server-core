<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="thekey/includes/commonVars.jsp" %>
<%
	if (exception != null) {
		while (exception instanceof javax.servlet.ServletException) {
			exception = ((javax.servlet.ServletException) exception).getRootCause();
		}
		pageContext.setAttribute("exception", exception);
	}
%>
<c:set var="title" value="error.exception.title" scope="request" />
<c:set var="helpJsp" value="thekey/help/error.jsp" scope="request" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<%@ include file="thekey/includes/htmlHead.jsp" %>
	<body class="page_error">
		<%@ include file="thekey/includes/allHeadings.jsp" %>
		<%@ include file="thekey/includes/menu.jsp" %>

		<div class="content">
			<div class="content_header">
				<p><spring:message code="error.exception.notice"/></p>
			</div>

			<div class="mainContent">
				<div class="section">
					<p><spring:message code="error.exception.message"/></p>
					<p><spring:message code="error.exception.details"/></p>
<%-- 					<p><c:out value="${exception.message}" /></p> --%>
				</div>
			</div>

			<div class="content_footer"><img class="logo" src="<c:out value="${logoUri}"/>" alt="The Key Logo"/><div class="clear"></div></div>
		</div>
		<%@ include file="thekey/includes/allFooters.jsp" %>
	</body>
</html>
