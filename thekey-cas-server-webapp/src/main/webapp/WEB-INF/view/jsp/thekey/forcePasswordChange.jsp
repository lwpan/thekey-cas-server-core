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
<title><spring:message code="help.selfServe.changeTempPw.title"/></title>
<!-- theme/styling -->
<link href="brand/common/genericview.css" type="text/css" rel="stylesheet"/>
<%@ include file="includeLocation.jsp"  %>
<!--  help -->
<script src="brand/common/jquery.js" type="text/javascript"></script>  
<script src="brand/common/helpPopup.js" type="text/javascript"></script>
<!--  client side password validation -->
<script src="brand/common/jquery.validate.js" type="text/javascript"></script>
<script src="js.htm" type="text/javascript"></script>

</head>

<body class="page_forcePasswordChange">
	<c:set var="menu_signin" value="selected" scope="page" />
	<c:set var="help_file" value="Help_ForcePasswordChange.jsp" scope="page" />

	<%@ include file="includeAllHeadings.jsp" %>
	<%@ include file="includeMenu.jsp" %>
	
	<div class="content">
		
		<div class="content_header">
			<p><spring:message code="selfserve.changetemppw.notice"/></p>
		</div>
		
		<div class="mainContent">

			<form:form modelAttribute="user" cssClass="minHeight" action="forcePasswordChange.htm" acceptCharset="utf-8">
				<div class="section">
					<p><spring:message code="selfserve.changetemppw.message"/></p>
					<div class="group">
						<label><spring:message code="selfserve.changetemppw.label.email"/></label><br/>
						<form:input cssClass="form_text" path="username" disabled="true"/><br/>
					</div>
					<div class="group">
						<label><spring:message code="selfserve.changetemppw.label.firstname"/></label><br/>
						<form:input cssClass="form_text" path="firstName" disabled="true"/><br/>
					</div>
					<div class="group">
						<label><spring:message code="selfserve.changetemppw.label.lastname"/></label><br/>
						<form:input cssClass="form_text" path="lastName" disabled="true"/><br/>
					</div>

				</div>
				<div class="section">
					<p><spring:message code="selfserve.changetemppw.message.line1"/></p>
					<p><spring:message code="selfserve.changetemppw.message.line2"/></p>
				
					<div class="group">
						<label><spring:message code="selfserve.changetemppw.label.password"/></label><br/>
						<input type="password" class="form_text" size="25" tabindex="1" name="password"  /><br/>
						<form:errors path="password">
									<span class="form_error"><form:errors path="password"/><br/></span>
						</form:errors>
						
					</div>
					<div class="group">
						<label><spring:message code="selfserve.changetemppw.label.confirmpassword"/></label>
						<input type="password" class="form_text" size="25" tabindex="2" name="retypePassword"  /><br/>
						<form:errors path="retypePassword">
									<span class="form_error"><form:errors path="retypePassword"/><br/></span>
						</form:errors>
					</div>
				</div>
				<div class="submit">
					<input class="cancel" type="submit" name="submit" tabindex="3" value="<spring:message code="selfserve.changetemppw.button.continue"/>" />
					<input class="cancel" type="submit" name="cancel" tabindex="4" value="<spring:message code="selfserve.changetemppw.button.cancel"/>" />
				</div>
			</form:form>
			
		</div>
		
		<div class="content_footer"><img class="logo" src="brand/common/images/logo_thekey.png" alt="The Key Logo"/><div class="clear"></div></div>

	</div>
	
	<%@ include file="includeAllFooters.jsp" %>

</body>

</html>