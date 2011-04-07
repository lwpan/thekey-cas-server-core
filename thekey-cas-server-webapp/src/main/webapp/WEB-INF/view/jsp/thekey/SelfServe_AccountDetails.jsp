<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isELIgnored="false"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><spring:message code="selfserve.signin.title"/></title>
<link href="brand/common/genericview.css" type="text/css" rel="stylesheet"/>
<%@ include file="includeLocation.jsp"  %>
<script src="brand/common/jquery.js" type="text/javascript"></script>  
<script src="brand/common/helpPopup.js" type="text/javascript"></script>
<script src="brand/common/jquery.validate.js" type="text/javascript"></script>
<script src="js.htm" type="text/javascript"></script>
</head>

<body class="page_SelfServe_AccountDetails" onLoad="setFocus(0,0);">	
	<c:set var="menu_account" value="selected" scope="page" />
	<c:set var="help_file" value="Help_SelfServe_AccountDetails.jsp" scope="page" />

	<%@ include file="includeAllHeadings.jsp" %>
	<%@ include file="includeMenu.jsp" %>
		
	<div class="content">
		
		<div class="content_header">
			<p><spring:message code="selfserve.accountdetails.notice"/></p>
		</div>
		
		<div class="mainContent">
		
			<form:errors path="user">
				<div class="errors">
					<p><form:errors path="user"/></p>
				</div>
			</form:errors>
						
			<form:form modelAttribute="user" cssClass="minHeight" acceptCharset="utf-8">
				<div class="section">
					<p><spring:message code="selfserve.accountdetails.message"/></p>
					<div class="group">
						<label><spring:message code="selfserve.accountdetails.label.firstname"/></label><br/>
						<form:input cssClass="form_text" path="firstName" tabindex="1"/><br/>
						<form:errors path="firstName">
							<span class="form_error"><form:errors path="firstName"/><br/></span>
						</form:errors>
					</div>
					<div class="group">
						<label><spring:message code="selfserve.accountdetails.label.lastname"/></label><br/>
						<form:input cssClass="form_text" path="lastName"  tabindex="2"/><br/>
						<form:errors path="lastName">
							<span class="form_error"><form:errors path="lastName"/><br/></span>
						</form:errors>						
					</div>
				</div>
				
				<div class="section">
					<div class="group">
						<label><spring:message code="selfserve.accountdetails.label.email"/></label><br/>
						<form:input cssClass="form_text" path="username"  tabindex="3" /><br/>
						<form:errors path="username">
							<span class="form_error"><form:errors path="username"/><br/></span>
						</form:errors>
					</div>
				</div>
				<div class="section">
					<p><spring:message code="selfserve.accountdetails.message.line1"/></p>
					<p><spring:message code="selfserve.accountdetails.message.line2"/></p>
										
					<div class="group">
						<label><spring:message code="selfserve.accountdetails.label.password"/></label><br/>
						<form:password cssClass="form_text" size="25" tabindex="4" path="password"  htmlEscape="true" autocomplete="off" /><br/>
						<form:errors path="password">
							<span class="form_error"><form:errors path="password"/><br/></span>
						</form:errors>
					</div>
					<div class="group">
						<label><spring:message code="selfserve.accountdetails.label.confirmpassword"/></label><br/>
						<form:password cssClass="form_text" size="25" tabindex="5" path="retypePassword"  htmlEscape="true" autocomplete="off" /><br/>
						<form:errors path="retypePassword">
							<span class="form_error"><form:errors path="retypePassword"/><br/></span>
						</form:errors>
					</div>
				</div>
				<div class="submit">
					<input class="cancel" type="submit" name="_eventId_submit" tabindex="6" value="<spring:message code="selfserve.accountdetails.button.continue"/>" />
					<input class="cancel" type="submit" name="_eventId_cancel" tabindex="7" value="<spring:message code="selfserve.accountdetails.button.cancel"/>" />
				</div>
			</form:form>

		</div>
		
		<div class="content_footer"><img class="logo" src="brand/common/images/logo_thekey.png" alt="The Key Logo"/><div class="clear"></div></div>
		
	</div>
	
	<%@ include file="includeAllFooters.jsp" %>

</body>

</html>