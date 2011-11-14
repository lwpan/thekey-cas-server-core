<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="keyfn" uri="/WEB-INF/tags.tld" %>
<%@ page session="false" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %><cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>
	<cas:authenticationSuccess>
		<cas:user>${fn:escapeXml(email)}</cas:user>
		<cas:attributes>
			<c:forEach var="attr" items="${assertion.chainedAuthentications[0].principal.attributes}">
				<c:choose>
					<c:when test="${attr.key == 'guid'}">
						<ssoGuid>${fn:escapeXml(attr.value)}</ssoGuid>
					</c:when>
					<c:when test="${keyfn:instanceOf(attr.value, 'java.util.Collection')}">
						<c:forEach var="value" items="${attr.value}">
							<${fn:escapeXml(attr.key)}>${fn:escapeXml(value)}</${fn:escapeXml(attr.key)}>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<${fn:escapeXml(attr.key)}>${fn:escapeXml(attr.value)}</${fn:escapeXml(attr.key)}>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</cas:attributes>
<c:if test="${not empty pgtIou}">
		<cas:proxyGrantingTicket>${fn:escapeXml(pgtIou)}</cas:proxyGrantingTicket>
</c:if>
<c:if test="${(fn:length(assertion.chainedAuthentications) > 1) or (not empty proxyAuthUri)}">
		<cas:proxies>
			<c:if test="${not empty proxyAuthUri}">
				<cas:proxy type="authentication">${fn:escapeXml(proxyAuthUri)}</cas:proxy>
			</c:if>
			<c:if test="${fn:length(assertion.chainedAuthentications) > 1}">
<c:forEach var="proxy" items="${assertion.chainedAuthentications}" varStatus="loopStatus" begin="0" end="${fn:length(assertion.chainedAuthentications)-2}" step="1">
			<cas:proxy>${fn:escapeXml(proxy.principal.id)}</cas:proxy>
</c:forEach>
			</c:if>
		</cas:proxies>
</c:if>
	</cas:authenticationSuccess>
</cas:serviceResponse>