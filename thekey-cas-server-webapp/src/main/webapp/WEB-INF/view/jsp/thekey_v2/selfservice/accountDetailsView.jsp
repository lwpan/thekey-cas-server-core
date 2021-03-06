<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_SelfServe_AccountDetails" scope="page" />
<c:set var="selectedMenu" value="account" scope="request" />
<c:set var="includePwv" value="true" scope="request" />
<c:set var="includeFb" value="true" scope="request" />
<c:set var="hasFb" value="${not empty selfservice.facebookId}" scope="request" />
<c:set var="useAutoFocus" value="false" scope="request" />

<c:set var="message_title" value="selfserve.signin.title" scope="page" />
<c:set var="message_header" value="selfserve.accountdetails.notice" scope="page" />

<c:if test="${hasFb}">
	<c:set var="fbButtonClasses" scope="page">
		<c:choose>
			<c:when test="$(rtl eq 'rtl')">
				fb_button_rtl fb_button_medium_rtl
			</c:when>
			<c:otherwise>
				fb_button fb_button_medium
			</c:otherwise>
		</c:choose>
	</c:set>
</c:if>

<%@ include file="../includes/top.jsp" %>

	<form:errors path="${commandName}">
		<div class="errors">
			<p><form:errors path="${commandName}"/></p>
		</div> <!-- .errors -->
	</form:errors>

	<form:form modelAttribute="${commandName}" id="accountDetails" cssClass="minHeight" acceptCharset="utf-8">
		<input type="hidden" name="execution" value="${flowExecutionKey}" />
		<div class="section">
			<p><spring:message code="selfserve.accountdetails.message"/></p>
			<div class="group">
				<label><spring:message code="selfserve.accountdetails.label.firstname"/></label><br/>
				<form:input cssClass="form_text" path="firstName" tabindex="1"/><br/>
				<form:errors path="firstName">
					<span class="form_error"><form:errors path="firstName"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
			<div class="group">
				<label><spring:message code="selfserve.accountdetails.label.lastname"/></label><br/>
				<form:input cssClass="form_text" path="lastName"  tabindex="2"/><br/>
				<form:errors path="lastName">
					<span class="form_error"><form:errors path="lastName"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
			<div class="group">
				<label><spring:message code="selfserve.accountdetails.label.email"/></label><br/>
				<form:input cssClass="form_text" path="email"  tabindex="3" /><br/>
				<form:errors path="email">
					<span class="form_error"><form:errors path="email"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
		</div> <!-- .section -->

		<c:if test="${includeFb}">
			<div class="section">
				<div class="group">
					<div class="facebookLogin">
						<c:choose>
							<c:when test="${hasFb}">
								<label><spring:message code="selfservice.accountdetails.facebook.label.currentaccount" arguments="${selfservice.facebookId}" /></label>
								<div class="fb-thekey-unlink-button">
									<a class="${fbButtonClasses}" onclick="theKeyFacebookUnlink('form#accountDetails', 'unlinkFacebook')">
										<span class="fb_button_text"><spring:message code="selfservice.accountdetails.facebook.button.unlink" /></span>
									</a>
								</div>
							</c:when>
							<c:otherwise>
								<div class="fb-login-button" data-length="long" data-scope="email" data-on-login="theKeyFacebookLogin('form#accountDetails', 'linkFacebook');"></div>
							</c:otherwise>
						</c:choose>
					</div> <!-- .facebookLogin -->
				</div> <!-- .group -->
			</div> <!-- .section -->
		</c:if>

		<div class="section">
			<p class="note"><spring:message code="selfserve.accountdetails.message.line1"/>&nbsp;<spring:message code="selfserve.accountdetails.message.line2"/></p>
			<div class="group">
				<label><spring:message code="selfserve.accountdetails.label.password"/></label><br/>
				<form:password cssClass="form_text" size="25" tabindex="5" path="password"  htmlEscape="true" autocomplete="off" /><br/>
				<form:errors path="password">
					<span class="form_error"><form:errors path="password"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
			<div class="group">
				<label><spring:message code="selfserve.accountdetails.label.confirmpassword"/></label><br/>
				<form:password cssClass="form_text" size="25" tabindex="6" path="retypePassword"  htmlEscape="true" autocomplete="off" /><br/>
				<form:errors path="retypePassword">
					<span class="form_error"><form:errors path="retypePassword"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
		</div> <!-- .section -->

		<div class="submit">
			<input class="form_submit" type="submit" name="_eventId_submit" tabindex="7" value="<spring:message code="selfserve.accountdetails.button.continue"/>" />
			<input class="form_cancel" type="submit" name="_eventId_cancel" tabindex="8" value="<spring:message code="selfserve.accountdetails.button.cancel"/>" />
		</div> <!-- .submit -->
	</form:form>

<%@ include file="../includes/bottom.jsp" %>
