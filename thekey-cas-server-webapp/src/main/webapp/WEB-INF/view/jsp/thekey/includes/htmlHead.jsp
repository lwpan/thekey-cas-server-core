<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<head>
	<title><spring:message code="${message_title}" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

	<!-- Mobile viewport optimized: j.mp/bplateviewport -->
	<meta name="viewport" content="width=device-width, initial-scale=1.0">

	<!-- theme/styling -->
	<c:url var="cssUri" value="${themeUri}/default.css" />
	<c:if test='${template != null && template != ""}'>
		<c:url var="cssUri" value="/css">
			<c:param name="css" value="${template}" />
			<c:param name="default" value="${cssUri}" />
		</c:url>
	</c:if>
	<link href="<c:out value="${cssUri}" />" type="text/css" rel="stylesheet"/>

	<%-- relay button styles --%>
	<c:if test="${includeRelay}">
		<c:url var="relayCssUri" value="${themeUri}/relay.css" scope="page" />
		<link href="<c:out value="${relayCssUri}" />" type="text/css" rel="stylesheet"/>
	</c:if>

	<%-- favicon --%>
	<link rel="icon" href="${faviconUri}" type="image/x-icon" />

	<!-- JavaScript -->
	<c:if test="${includeJquery or useAutoFocus or includeHelp or includePwv or includeFb}">
		<c:url var="jqueryUri" value="${themeUri}/jquery-1.6.4.min.js" />
		<c:url var="thekeyJsUri" value="${themeUri}/thekey.js" scope="page" />
		<script src="<c:out value="${jqueryUri}" />" type="text/javascript"></script>
		<script src="<c:out value="${thekeyJsUri}" />" type="text/javascript"></script>

		<c:if test="${useAutoFocus}">
			<script type="text/javascript">
				jQuery(document).ready(function($) {
					$('.auto-focus:first').focus();
				});
			</script>
		</c:if>

		<c:if test="${includeHelp}">
			<!-- Help Menu JavaScript -->
			<c:url var="helpUri" value="${themeUri}/helpPopup.js" />
			<script src="<c:out value="${helpUri}" />" type="text/javascript"></script>
		</c:if>

		<c:if test="${includePwv}">
			<!-- Password Validation JavaScript -->
			<c:url var="validatorUri" value="${themeUri}/jquery.validate.min.js" />
			<c:url var="pwvUri" value="${themeUri}/passwordValidator.js" />
			<script src="<c:out value="${validatorUri}" />" type="text/javascript"></script>
			<script src="<c:out value="${pwvUri}" />" type="text/javascript"></script>
			<script type="text/javascript">
				jQuery(document).ready(function($) {
					var json = <c:out escapeXml="false" value="${jsonPasswordRules}" />;
					$.validator.addClassRules("password", json.rules);

					$('form').enablePwv('#password', '#retypePassword', json.messages);
				});
			</script>
		</c:if>

		<c:if test="${includeFb}">
			<c:url var="fbUri" value="${themeUri}/facebook.login.js" />
			<script src="https://connect.facebook.net/<spring:message code="facebook.locale" />/all.js"></script>
			<script src="<c:out value="${fbUri}" />" type="text/javascript"></script>
			<script type="text/javascript">
				jQuery(document).ready(function($) {
					FB.init({
						appId:'${facebook.appId}',
						oauth:true,
						status:false, xfbml:true
					});
				});
			</script>
		</c:if>
	</c:if>

	<%-- Google Analytics --%>
	<c:if test='${googleAnalyticsAccount != null && googleAnalyticsAccount != ""}'>
		<script type="text/javascript">
			var _gaq = _gaq || [];
			_gaq.push(['_setAccount', '<c:out value="${googleAnalyticsAccount}" />']);
			_gaq.push(['_trackPageview']);

			(function() {
				var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
				ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
				var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
			})();
		</script>
	</c:if>
</head>
