<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>

<%-- theme related variables --%>
<c:set var="themeUri" value="/themes/thekey" scope="request" />
<c:url var="logoUri" value="${themeUri}/images/logo_thekey.png" scope="request" />

<%-- common link variables --%>
<c:url var="loginUri" value="/login" scope="request" />
<c:url var="signupUri" value="/signup.htm" scope="request" />
