<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../includes/commonVars.jsp" %>
<c:set var="bodyClasses" value="${bodyClasses} page_SelfServe_AccountDetails" scope="page" />
<c:set var="selectedMenu" value="account" scope="request" />
<c:set var="helpJsp" value="../help/selfservice/accountDetails.jsp" scope="request" />
<c:set var="includePwv" value="true" scope="request" />
<c:set var="includeFb" value="true" scope="request" />
<c:set var="hasFb" value="${not empty selfservice.facebookId}" scope="request" />
<c:set var="includeRelay" value="true" scope="request" />
<c:set var="hasRelay" value="${not empty selfservice.relayGuid}" scope="request" />
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

<c:if test="${not hasRelay}">
	<%-- Generate the relay login uri --%>
	<c:url var="serviceUri" value="${requestUri}" scope="page">
		<c:param name="execution" value="${flowExecutionKey}" />
		<c:param name="_eventId" value="linkRelay" />
	</c:url>
	<c:url var="relayLoginUri" value="${relayUri}login" scope="page">
		<c:param name="renew" value="true" />
		<c:param name="service" value="${serviceUri}" />
	</c:url>
</c:if>

<%@ include file="../includes/top.jsp" %>

	<form:errors path="${commandName}">
		<div class="errors">
			<p><form:errors path="${commandName}"/></p>
		</div> <!-- .errors -->
	</form:errors>

	<form:form action="${requestUri}" modelAttribute="${commandName}" id="accountDetails" cssClass="minHeight" acceptCharset="utf-8">
		<input type="hidden" name="execution" value="${flowExecutionKey}" />
		<div class="section">
			<p><spring:message code="selfservice.page.accountdetails.section.name.label"/></p>
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
		</div> <!-- .section -->
		<div class="section">
			<p><spring:message code="selfservice.page.accountdetails.section.email.label"/></p>
			<div class="group">
				<label><spring:message code="selfserve.accountdetails.label.email"/></label><br/>
				<form:input type="email" cssClass="form_text" path="email"  tabindex="3" /><br/>
				<form:errors path="email">
					<span class="form_error"><form:errors path="email"/><br/></span>
				</form:errors>
			</div> <!-- .group -->
		</div> <!-- .section -->

		<c:if test="${includeFb or includeRelay}">
			<div class="section federated-identities">
				<p><spring:message code="selfservice.page.accountdetails.section.federatedidentities.label"/></p>
				<c:if test="${includeFb}">
					<div class="group">
						<div class="facebook-identity facebookLogin">
							<c:choose>
								<c:when test="${hasFb}">
									<label><spring:message code="selfservice.accountdetails.facebook.label.currentaccount" arguments="${selfservice.facebookId}" /></label>
									<div class="fb-thekey-unlink-button">
										<a class="${fbButtonClasses}" onclick="theKeySubmitForm('form#accountDetails', 'unlinkFacebook')">
											<span class="fb_button_text"><spring:message code="selfservice.accountdetails.facebook.button.unlink" /></span>
										</a>
									</div>
								</c:when>
								<c:otherwise>
									<!-- TODO: we should reauthenticate the user to be safe -->
									<div class="fb-login-button" data-length="long" data-scope="email" data-on-login="theKeyFacebookLogin('form#accountDetails', 'linkFacebook');"></div>
								</c:otherwise>
							</c:choose>
						</div> <!-- .facebookLogin -->
					</div> <!-- .group -->
				</c:if>

				<c:if test="${includeRelay}">
					<div class="group">
						<div class="relay-identity">
							<c:choose>
								<c:when test="${hasRelay}">
									<label><spring:message code="selfservice.accountdetails.relay.label.currentaccount" arguments="${selfservice.relayGuid}" /></label>
									<div class="relay_button">
										<a class="relay_button_link" onclick="theKeySubmitForm('form#accountDetails', 'unlinkRelay')">
											<span><spring:message code="selfservice.accountdetails.relay.button.unlink" /></span>
										</a>
									</div>
								</c:when>
								<c:otherwise>
									<div class="relay_button">
										<a class="relay_button_link" href="<c:out value="${relayLoginUri}" />"><span><spring:message code="selfservice.accountdetails.relay.button.link" /></span></a>
									</div>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</c:if>
			</div> <!-- .section -->
		</c:if>

		<div class="section">
			<p><spring:message code="selfservice.page.accountdetails.section.password.label"/></p>
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
