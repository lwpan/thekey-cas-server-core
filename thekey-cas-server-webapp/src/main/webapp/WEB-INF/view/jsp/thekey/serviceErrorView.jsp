<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="includes/commonVars.jsp" %>
<c:set var="title" value="error.communication.title" scope="request" />
<c:set var="helpJsp" value="help/error.jsp" scope="request" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
	<%@ include file="includes/htmlHead.jsp" %>
	<body class="page_error">
		<%@ include file="includes/allHeadings.jsp" %>
		<%@ include file="includes/menu.jsp" %>
		<div class="content">
			<div class="content_header">
				<p><spring:message code="error.communication.notice"/></p>
			</div>

			<div class="mainContent">
				<div class="section">
					<p><spring:message code="error.communication.message"/></p>
				</div>
			</div>

			<div class="content_footer">
				<img class="logo" src="<c:out value="${logoUri}"/>" alt="The Key Logo"/>
				<div class="clear"></div>
			</div>
		</div>
		<%@ include file="includes/allFooters.jsp" %>
	</body>
</html>
