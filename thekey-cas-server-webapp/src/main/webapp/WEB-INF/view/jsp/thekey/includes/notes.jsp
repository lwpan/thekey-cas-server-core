<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="notes">
	<div class="notes-in">
		<p class="notes_signup"><a href="<c:out value="${signupUri}" />"><spring:message code="login.noaccountA" /></a></p>
		<p class="notes_account"><a href="<c:out value="${accountDetailsUri}" />"><spring:message code="login.canalsoA" /></a></p>
	</div> <!-- .notes-in -->
</div> <!-- .notes -->
