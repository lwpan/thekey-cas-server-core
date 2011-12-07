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
<c:set var="bodyClasses" value="${bodyClasses} page_error" scope="request" />
<c:set var="helpJsp" value="thekey/help/error.jsp" scope="request" />
<c:set var="useAutoFocus" value="false" scope="request" />

<!DOCTYPE html>
<html dir="${dir}">
	<%@ include file="thekey/includes/htmlHead.jsp" %>
	<body class="${bodyClasses}">
		<%@ include file="thekey/includes/allHeadings.jsp" %>
		<%@ include file="thekey/includes/menu.jsp" %>

		<div class="content">
			<div class="content-in">

				<div class="content_header">
					<div class="content_header-in">
						<p><spring:message code="error.exception.notice"/></p>
					</div> <!-- .content_header-in -->
				</div> <!-- .content_header -->

				<div class="content_body">
					<div class="content_body-in">

						<div class="mainContent">
							<div class="mainContent-in">
								<div class="section">
									<p class="message-first"><spring:message code="error.exception.message"/></p>
									<p class="message-last"><spring:message code="error.exception.details"/></p>
<%-- 								<p class="message-last"><c:out value="${exception.message}" /></p> --%>
								</div> <!-- .section -->
							</div> <!-- .mainContent-in -->
						</div> <!-- .mainContent -->

						<div class="minorContent">
							<div class="minorContent-in">
								<div class="logo-wrap"><img class="logo" src="<c:out value="${logoUri}"/>" alt="The Key Logo"/></div>
							</div> <!-- .minorContent-in -->
						</div> <!-- .minorContent -->

					</div> <!-- .content_body-in -->
				</div> <!-- .content_body -->

			</div> <!-- .content-in -->
		</div> <!-- .content -->

		<%@ include file="thekey/includes/allFooters.jsp" %>
	</body>
</html>
