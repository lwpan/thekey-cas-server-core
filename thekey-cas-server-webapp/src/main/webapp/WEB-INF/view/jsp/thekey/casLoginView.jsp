<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ include file="includes/commonVars.jsp" %>
<c:set var="includeFb" value="true" scope="request" />
<c:set var="helpJsp" value="help/login.jsp" scope="request" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
	<head>
		<title><spring:message code="login.title"/></title>
		<%@ include file="includes/htmlHead.jsp" %>
		<c:if test="${includeFb}">
			<script>
				function fb_login(response) {
					var form = jQuery('form#login_form');
					jQuery('input[name="_eventId"]', form).val('facebookSubmit');
					jQuery('input[name="fb_key"]', form).val(response.session.access_token);
					form.submit();
				}
			</script>
		</c:if>
	</head>

<body class="page_login" onLoad="setFocus(0,0);">
	<c:set var="menu_signin" value="selected" scope="page" />
	
	<%@ include file="includes/allHeadings.jsp" %>
	<%@ include file="includes/menu.jsp" %>
	
	<div class="content">
	
		<div class="content_header">
			<p><spring:message code="login.notice"/></p>
		</div>
	
		<div class="mainContent">
	
			<form:errors path="${commandName}">
				<div class="errors">
					<p><form:errors path="${commandName}"/></p>
				</div>
			</form:errors>
			
			<form:form commandName="${commandName}" id="login_form" cssClass="minHeight">
				<input type="hidden" name="lt" value="${flowExecutionKey}" />
				<input type="hidden" name="_eventId" value="submit" />
				<input type="hidden" name="fb_key" />
				<div class="section">
					<div class="group">
						<label for="username"><spring:message code="login.label.username"/></label><br/>
						<form:input cssClass="form_text" path="username" tabindex="1"/><br/>
						<form:errors path="username"><span class="form_error"><form:errors path="username"/><br/></span></form:errors>
					</div>
					<div class="group">
						<label for="password"><spring:message code="login.label.password"/></label><br/>
						<form:password cssClass="form_text" size="25" tabindex="2" path="password"  htmlEscape="true" /><br/>
						<form:errors path="password"><span class="form_error"><form:errors path="password"/><br/></span></form:errors>
						<a href="<c:out value="${forgotPasswordUri}" />"><spring:message code="login.forgotpassword"/></a>
					</div>
				</div>
				<div class="submit">
					<c:if test="${includeFb}">
						<fb:login-button length="long" perms="email" onlogin="FB.getLoginStatus(fb_login)"></fb:login-button>
					</c:if>
					<input class="form_submit" type="submit" tabindex="3" value="<spring:message code="login.button.submit"/>" />
				</div>
			</form:form>
		</div>
	
		<div class="content_footer"><img class="logo" src="<c:out value="${logoUri}"/>" alt="The Key Logo"/>
	
			<%@ include file="includes/notes.jsp" %>

			<div class="clear"></div>
		</div>
		
	</div>
	
	<%@ include file="includes/languagesList.jsp" %>
	<%@ include file="includes/allFooters.jsp" %>
	
</body>

</html>
                                                              
