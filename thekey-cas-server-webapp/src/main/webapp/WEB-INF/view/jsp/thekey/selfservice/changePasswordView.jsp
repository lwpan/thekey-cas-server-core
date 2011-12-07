<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="title" value="help.selfServe.changeTempPw.title" scope="request" />
<c:set var="bodyClasses" value="${bodyClasses} page_forcePasswordChange" scope="request" />
<c:set var="selectedMenu" value="signin" scope="request" />
<c:set var="helpJsp" value="../help/changeStalePassword.jsp" scope="request" />
<c:set var="includePwv" value="true" scope="request" />

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
						<p><spring:message code="selfserve.changetemppw.notice"/></p>
					</div> <!-- .content_header-in -->
				</div> <!-- .content_header -->

				<div class="content_body">
					<div class="content_body-in">

						<div class="mainContent">
							<div class="mainContent-in">
								<form:form commandName="${commandName}" modelAttribute="user" cssClass="minHeight" acceptCharset="utf-8">
									<input type="hidden" name="execution" value="${flowExecutionKey}" />
									<div class="section">
										<p><spring:message code="selfserve.changetemppw.message"/></p>
										<div class="group">
											<label><spring:message code="selfserve.changetemppw.label.email"/></label><br/>
											<form:input cssClass="form_text" path="email" disabled="true"/><br/>
										</div> <!-- .group -->
<%--
										<div class="group">
											<label><spring:message code="selfserve.changetemppw.label.firstname"/></label><br/>
											<form:input cssClass="form_text" path="firstName" disabled="true"/><br/>
										</div> <!-- .group -->
										<div class="group">
											<label><spring:message code="selfserve.changetemppw.label.lastname"/></label><br/>
											<form:input cssClass="form_text" path="lastName" disabled="true"/><br/>
										</div> <!-- .group -->
--%>
									</div> <!-- .section -->

									<div class="section">
										<p class="message-first"><spring:message code="selfserve.changetemppw.message.line1"/></p>
										<p class="message-last"><spring:message code="selfserve.changetemppw.message.line2"/></p>
										<div class="group">
											<label><spring:message code="selfserve.changetemppw.label.password"/></label><br/>
											<form:input type="password" class="form_text auto-focus" size="25" tabindex="1" path="password" /><br/>
											<form:errors path="password">
														<span class="form_error"><form:errors path="password"/><br/></span>
											</form:errors>
										</div> <!-- .group -->
										<div class="group">
											<label><spring:message code="selfserve.changetemppw.label.confirmpassword"/></label>
											<form:input type="password" class="form_text" size="25" tabindex="2" path="retypePassword" /><br/>
											<form:errors path="retypePassword">
														<span class="form_error"><form:errors path="retypePassword"/><br/></span>
											</form:errors>
										</div> <!-- .group -->
									</div> <!-- .section -->

									<div class="submit">
										<input class="cancel" type="submit" name="_eventId_submit" tabindex="3" value="<spring:message code="selfserve.changetemppw.button.continue"/>" />
										<input class="cancel" type="submit" name="_eventId_cancel" tabindex="4" value="<spring:message code="selfserve.changetemppw.button.cancel"/>" />
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
