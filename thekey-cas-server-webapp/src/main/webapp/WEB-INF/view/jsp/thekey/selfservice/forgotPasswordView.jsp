<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="title" value="selfserve.forgotpassword.title" scope="request" />
<c:set var="bodyClasses" value="${bodyClasses} page_SelfServe_ForgotPassword" scope="request" />
<c:set var="selectedMenu" value="account" scope="request" />
<c:set var="helpJsp" value="../help/selfservice/forgotPassword.jsp" scope="request" />

<!DOCTYPE html>
<html dir="${dir}">
	<%@ include file="../includes/htmlHead.jsp" %>
	<body class="${bodyClasses}">
		<%@ include file="../includes/allHeadings.jsp" %>
		<%@ include file="../includes/menu.jsp" %>

		<div class="content">
			<div class="content-in">

				<div class="content_header">
					<div class="content_header-in">
						<p><spring:message code="selfserve.forgotpassword.notice"/></p>
					</div> <!-- .content_header-in -->
				</div> <!-- .content_header -->

				<div class="content_body">
					<div class="content_body-in">

						<div class="mainContent">
							<div class="mainContent-in">
								<form:errors path="user">
									<div class="errors">
										<p><form:errors path="user"/></p>
									</div> <!-- .errors -->
								</form:errors>

								<form:form modelAttribute="user" cssClass="minHeight" acceptCharset="utf-8">
									<input type="hidden" name="execution" value="${flowExecutionKey}" />
									<div class="section">
										<p><spring:message code="selfserve.forgotpassword.message"/></p>
										<div class="group">
											<label><spring:message code="selfserve.forgotpassword.label.username"/></label><br/>
											<form:input cssClass="form_text auto-focus" tabindex="1" path="email"/><br/>
											<form:errors path="email">
												<span class="form_error"><form:errors path="email"/><br/></span>
											</form:errors>
										</div> <!-- .group -->
									</div> <!-- .section -->
									<div class="submit">
										<input class="form_submit" type="submit" tabindex="2" name="_eventId_submit" value="<spring:message code="selfserve.forgotpassword.button.continue"/>" />
										<input class="form_cancel" type="submit" tabindex="3" name="_eventId_cancel" value="<spring:message code="selfserve.forgotpassword.button.cancel"/>" />
									</div> <!-- .submit -->
								</form:form>

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

		<%@ include file="../includes/allFooters.jsp" %>
	</body>
</html>
