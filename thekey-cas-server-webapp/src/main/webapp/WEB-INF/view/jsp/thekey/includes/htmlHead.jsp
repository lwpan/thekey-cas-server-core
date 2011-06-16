<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

<!-- theme/styling -->
<c:url var="cssUri" value="${themeUri}/cas.css" />
<link href="<c:out value="${cssUri}" />" type="text/css" rel="stylesheet"/>
<%@ include file="customCss.jsp"  %>

<c:if test="${includeHelp or includePwv or includeFb}">
	<!-- JavaScript -->
	<c:url var="jqueryUri" value="${themeUri}/jquery-1.6.1.min.js" />
	<script src="<c:out value="${jqueryUri}" />" type="text/javascript"></script>

	<c:if test="${includeHelp}">
		<!-- Help Menu JavaScript -->
		<c:url var="helpUri" value="${themeUri}/helpPopup.js" />
		<script src="<c:out value="${helpUri}" />" type="text/javascript"></script>
	</c:if>

	<c:if test="${includePwv}">
		<!-- Password Validation JavaScript -->
		<c:url var="validatorUri" value="${themeUri}/jquery.validate.js" />
		<c:url var="pwvUri" value="/pwv.js" />
		<script src="<c:out value="${validatorUri}" />" type="text/javascript"></script>
		<script src="<c:out value="${pwvUri}" />" type="text/javascript"></script>
	</c:if>

	<c:if test="${includeFb}">
		<script src="https://connect.facebook.net/en_US/all.js"></script>
		<script>
			jQuery(document).ready(function() {
				FB.init({
					appId:'${facebook.appId}',
					status:false, xfbml:true
				});
			});
		</script>
	</c:if>
</c:if>
