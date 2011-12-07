<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ include file="includes/commonVars.jsp" %>
<c:set var="title" value="logout.title" scope="request" />
<c:set var="bodyClasses" value="${bodyClasses} page_logout" scope="request" />
<c:set var="helpJsp" value="help/logout.jsp" scope="request" />
<c:set var="useAutoFocus" value="false" scope="request" />

<!DOCTYPE html>
<html dir="${dir}">
	<%@ include file="includes/htmlHead.jsp" %>
	<body class="${bodyClasses}">
		<%@ include file="includes/allHeadings.jsp" %>
		<%@ include file="includes/menu.jsp" %>

		<div class="content">
			<div class="content-in">

				<div class="content_header">
					<div class="content_header-in">
						<p><spring:message code="logoutsuccessful"/></p>
					</div> <!-- .content_header-in -->
				</div> <!-- .content_header -->

				<div class="content_body">
					<div class="content_body-in">

						<div class="mainContent">
							<div class="mainContent-in">
								<form id="command" class="minHeight" action="${loginUri}" method="get">
									<p><spring:message code="logout.message"/></p>
									<div class="submit">
										<input class="form_submit" type="submit" tabindex="1" value="<spring:message code="logout.button.submit"/>" />
									</div> <!-- .submit -->
								</form>
							</div> <!-- .mainContent-in -->
						</div> <!-- .mainContent -->

						<div class="minorContent">
							<div class="minorContent-in">
								<div class="logo-wrap"><img class="logo" src="<c:out value="${logoUri}"/>" alt="The Key Logo"/></div>
								<%@ include file="includes/securityNote.jsp" %>
							</div> <!-- .minorContent-in -->
						</div> <!-- .minorContent -->

					</div> <!-- .content_body-in -->
				</div> <!-- .content_body -->

			</div> <!-- .content-in -->
		</div> <!-- .content -->

		<%@ include file="includes/allFooters.jsp" %>
	</body>
</html>
