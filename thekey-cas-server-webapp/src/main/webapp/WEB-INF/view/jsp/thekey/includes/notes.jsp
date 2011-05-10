<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="notes">
	<p><a href="<c:out value="${signupUri}" />"><spring:message code="login.noaccountA" /></a>&nbsp;<spring:message code="login.noaccountB" />.</p>
	<p><a href="<c:out value="${accountDetailsUri}" />"><spring:message code="login.canalsoA" /></a>&nbsp;<spring:message code="login.canalsoB" />.</p>
</div>
