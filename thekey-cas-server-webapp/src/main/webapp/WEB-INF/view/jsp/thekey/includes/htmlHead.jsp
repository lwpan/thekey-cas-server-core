<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

<!-- Mobile viewport optimized: j.mp/bplateviewport -->
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- theme/styling -->
<c:url var="baseCssUri" value="${themeUri}/base.css" />
<c:url var="themeCssUri" value="/css">
	<c:param name="css" value="${template}"/>
</c:url>
<link href="<c:out value="${baseCssUri}" />" type="text/css" rel="stylesheet"/>
<link href="<c:out value='${themeCssUri}'/>" type="text/css" rel="stylesheet"/>

<c:if test="${includeHelp or includePwv or includeFb}">
	<!-- JavaScript -->
	<c:url var="jqueryUri" value="${themeUri}/jquery-1.6.4.min.js" />
	<script src="<c:out value="${jqueryUri}" />" type="text/javascript"></script>

	<c:if test="${includeHelp}">
		<!-- Help Menu JavaScript -->
		<c:url var="helpUri" value="${themeUri}/helpPopup.js" />
		<script src="<c:out value="${helpUri}" />" type="text/javascript"></script>
	</c:if>

	<c:if test="${includePwv}">
		<!-- Password Validation JavaScript -->
		<c:url var="validatorUri" value="${themeUri}/jquery.validate.min.js" />
		<c:url var="pwvUri" value="/pwv.js" />
		<script src="<c:out value="${validatorUri}" />" type="text/javascript"></script>
		<script src="<c:out value="${pwvUri}" />" type="text/javascript"></script>
	</c:if>

	<c:if test="${includeFb}">
		<c:url var="fbUri" value="${themeUri}/facebook.login.js" />
		<script src="https://connect.facebook.net/en_US/all.js"></script>
		<script src="<c:out value="${fbUri}" />" type="text/javascript"></script>
		<script>
			jQuery(document).ready(function() {
				FB.init({
					appId:'${facebook.appId}',
					oauth:true,
					status:false, xfbml:true
				});
			});
		</script>
	</c:if>
</c:if>
