<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="keyfn" uri="/WEB-INF/tags.tld" %>
<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>
	<cas:authenticationSuccess>
		<cas:user>${fn:escapeXml(assertion.chainedAuthentications[fn:length(assertion.chainedAuthentications)-1].principal.id)}</cas:user>
		<cas:attributes>
			<c:forEach var="attr" items="${assertion.chainedAuthentications[fn:length(assertion.chainedAuthentications)-1].principal.attributes}">
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