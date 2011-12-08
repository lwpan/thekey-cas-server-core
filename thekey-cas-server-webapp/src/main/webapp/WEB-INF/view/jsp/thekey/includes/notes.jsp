<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="menu_minorContent-wrap">
	<ul class="menu_minorContent">
		<li><a href="<c:out value="${signupUri}" />"><span><spring:message code="login.noaccountA" /></a></span></li>
		<li><a href="<c:out value="${accountDetailsUri}" />"><span><spring:message code="login.canalsoA" /></span></a></li>
	</ul> <!-- .menu_minorContent -->
</div> <!-- .menu_minorContent-wrap -->
