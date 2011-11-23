<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<ul class="menu">
	<li class="link1<c:if test="${selectedMenu eq 'signin'}"> selected</c:if>">
		<a href="<c:out value="${loginUri}" />"><span><spring:message code="menu.signin" /></span></a>
	</li>
	<li class="link2<c:if test="${selectedMenu eq 'signup'}"> selected</c:if>">
		<a href="<c:out value="${signupUri}" />"><span><spring:message code="menu.signup" /></span></a>
	</li>
	<li class="link3<c:if test="${selectedMenu eq 'account'}"> selected</c:if>">
		<a href="<c:out value="${accountDetailsUri}" />"><span><spring:message code="menu.accountdetails" /></span></a>
	</li>
	<c:if test="${includeHelp}">
		<li id="ssoHelp">
			<a href="#" id="ssoHelp_link"><span><spring:message code="help.label.help" /></span></a>
			<a href="#" id="ssoHelp_link_close"><span><spring:message code="help.label.close" /></span></a>
			<div id="ssoHelp_popup_container">
				<div id="ssoHelp_popup">
					<div class="ssoHelp_content_border">
						<div class="ssoHelp_popup_content">
							<jsp:include page="${helpJsp}" />
						</div>
					</div>
				</div>
			</div>
		</li>
	</c:if>
</ul>
