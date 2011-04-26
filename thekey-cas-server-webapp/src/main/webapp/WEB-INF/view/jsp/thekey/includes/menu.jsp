<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%
	String filename = (String) pageContext.getAttribute("help_file");
%>

<ul class="menu">
	<li class="link1 <c:out value="${pageScope.menu_signin}"/>"><a href="login.htm"><span><spring:message code="menu.signin"/></span></a></li>
	<li class="link2 <c:out value="${pageScope.menu_signup}"/>"><a href="signup.htm"><span><spring:message code="menu.signup"/></span></a></li>
	<li class="link3 <c:out value="${pageScope.menu_account}"/>"><a href="selfservice.htm?target=displayAccountDetails"><span><spring:message code="menu.accountdetails"/></span></a></li>
	<li id="ssoHelp">
		<a href="#" id="ssoHelp_link"><span><spring:message code="help.label.help"/></span></a>
		<a href="#" id="ssoHelp_link_close"><span><spring:message code="help.label.close"/></span></a>
		<div id="ssoHelp_popup_container">
			<div id="ssoHelp_popup">
				<div class="ssoHelp_content_border">
					<div class="ssoHelp_popup_content">
						<jsp:include page="<%=filename%>"/>
					</div>
				</div>
			</div>
		</div>
	</li>
</ul>