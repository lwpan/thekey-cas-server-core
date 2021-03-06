<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- theme related variables --%>
<c:set var="themeUri" value="/themes/thekey" scope="request" />
<c:url var="logoUri" value="${themeUri}/images/logo_thekey.png" scope="request" />
<c:url var="faviconUri" value="/favicon.ico" scope="page" />

<%-- common link variables --%>
<c:url var="loginUri" value="/login?${commonUriParams}" scope="request" />
<c:set var="selfserviceUri" value="/service/selfservice?${commonUriParams}"/>

<%-- Self Service links --%>
<c:url var="accountDetailsUri" value="${selfserviceUri}" scope="request">
	<c:param name="target" value="displayAccountDetails" />
</c:url>
<c:url var="forgotPasswordUri" value="${selfserviceUri}" scope="request">
	<c:param name="target" value="displayForgotPassword" />
</c:url>
<c:url var="signupUri" value="${selfserviceUri}" scope="request">
	<c:param name="target" value="signup" />
</c:url>

<%-- Flags that control output --%>
<c:set var="includeJquery" value="false" scope="request" />
<c:set var="includeFb" value="false" scope="request" />
<c:set var="includeHelp" value="true" scope="request" />
<c:set var="includePwv" value="false" scope="request" />
<c:set var="includeRelay" value="false" scope="request" />
<c:set var="showLanguages" value="false" scope="page" />
<c:set var="showMinorNav" value="false" scope="page" />
<c:set var="useAutoFocus" value="true" scope="request" />

<%-- body classes --%>
<c:set var="bodyClasses" value="${dir} v5" scope="request" />
<c:choose>
	<c:when test="${isMobile}">
		<c:set var="bodyClasses" value="mobile ${bodyClasses}" scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="bodyClasses" value="desktop ${bodyClasses}" scope="request" />
	</c:otherwise>
</c:choose>

<%-- Brand Names --%>
<c:set var="brand_thekey" value="The Key" scope="request" />
<c:set var="brand_facebook" value="Facebook" scope="request" />
<c:set var="brand_relay" value="Relay" scope="request" />

<%-- spring translation message codes --%>
<c:set var="message_title" scope="page" />
<c:set var="message_header" scope="page" />
<c:set var="args_header" scope="page" />
