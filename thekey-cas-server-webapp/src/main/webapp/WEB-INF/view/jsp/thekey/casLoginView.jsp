<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ include file="includes/commonVars.jsp" %>
<c:set var="title" value="login.title" scope="request" />
<c:set var="bodyClasses" value="${bodyClasses} page_login" scope="request" />
<c:set var="selectedMenu" value="signin" scope="request" />
<c:set var="includeFb" value="true" scope="request" />
<c:set var="helpJsp" value="help/login.jsp" scope="request" />

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
						<p>
							<c:choose>
								<c:when test="${not empty serviceDomain}">
									<spring:message code="login.notice" arguments="${serviceDomain}" />
								</c:when>
								<c:otherwise>
									<spring:message code="login.notice.noservice" />
								</c:otherwise>
							</c:choose>
						</p>
					</div> <!-- .content_header-in -->
				</div> <!-- .content_header -->

				<div class="content_body">
					<div class="content_body-in">

						<div class="mainContent">
							<div class="mainContent-in">

								<form:errors path="${commandName}">
									<div class="errors">
										<div class=".errors-in">
											<p><form:errors path="${commandName}"/></p>
										</div> <!-- .errors-in -->
									</div> <!-- .errors -->
								</form:errors>

								<form:form commandName="${commandName}" id="login_form" cssClass="minHeight">
									<input type="hidden" name="lt" value="${loginTicket}" />
									<input type="hidden" name="execution" value="${flowExecutionKey}" />
									<input type="hidden" name="_eventId" value="submit" />
									<div class="section">
										<div class="group">
											<label for="username"><spring:message code="login.label.username"/></label><br/>
											<form:input cssClass="form_text auto-focus" path="username" tabindex="1"/><br/>
											<form:errors path="username"><span class="form_error"><form:errors path="username"/><br/></span></form:errors>
										</div> <!-- .group -->
										<div class="group">
											<label for="password"><spring:message code="login.label.password"/></label><br/>
											<form:password cssClass="form_text" size="25" tabindex="2" path="password"  htmlEscape="true" /><br/>
											<form:errors path="password"><span class="form_error"><form:errors path="password"/><br/></span></form:errors>
											<a href="<c:out value="${forgotPasswordUri}" />"><spring:message code="login.forgotpassword"/></a>
										</div> <!-- .group -->
									</div> <!-- .section -->
									<div class="submit">
										<c:if test="${includeFb}">
											<fb:thekey-login-button length="long" perms="email" form="form#login_form" action="facebookSubmit"></fb:thekey-login-button>
										</c:if>
										<input class="form_submit" type="submit" tabindex="3" value="<spring:message code="login.button.submit"/>" />
									</div> <!-- .submit -->
								</form:form>

							</div> <!-- .mainContent-in -->
						</div> <!-- .mainContent -->

						<div class="minorContent">
							<div class="minorContent-in">
								<div class="logo-wrap"><img class="logo" src="<c:out value="${logoUri}"/>" alt="The Key Logo"/></div>
								<%@ include file="includes/notes.jsp" %>
							</div> <!-- .minorContent-in -->
						</div> <!-- .minorContent -->

					</div> <!-- .content_body-in -->
				</div> <!-- .content_body -->

			</div> <!-- .content-in -->
		</div> <!-- .content -->

		<%@ include file="includes/languagesList.jsp" %>
		<%@ include file="includes/allFooters.jsp" %>
	</body>
</html>
