<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>

<%-- theme related variables --%>
<c:set var="themeUri" value="/themes/thekey" scope="request" />
<c:url var="logoUri" value="${themeUri}/images/logo_thekey.png" scope="request" />
<c:url var="faviconUri" value="/favicon.ico" scope="page" />

<%-- common link variables --%>
<c:url var="loginUri" value="/login" scope="request" />
<c:set var="selfserviceUri" value="/service/selfservice"/>

<%-- Self Service links --%>
<c:url var="accountDetailsUri" value="${selfserviceUri}?target=displayAccountDetails" scope="request"/>
<c:url var="forgotPasswordUri" value="${selfserviceUri}?target=displayForgotPassword" scope="request"/>
<c:url var="signupUri" value="${selfserviceUri}?target=signup" scope="request" />

<%-- Flags that control output --%>
<c:set var="includeFb" value="false" scope="request" />
<c:set var="includeHelp" value="true" scope="request" />
<c:set var="includePwv" value="false" scope="request" />
<c:set var="showLanguages" value="false" scope="page" />
<c:set var="showMinorNav" value="false" scope="page" />
<c:set var="useAutoFocus" value="true" scope="request" />

<%-- body classes --%>
<c:set var="bodyClasses" value="v2 ${dir}" scope="request" />

<%-- spring translation message codes --%>
<c:set var="message_header" scope="page" />
<c:set var="args_header" scope="page" />
