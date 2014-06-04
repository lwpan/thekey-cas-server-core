<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<h2><spring:message code="help.generic.title" arguments="${brand_thekey}" /></h2>
<div class="ssoHelp_area">
	<p><spring:message code="help.generic.info" /></p>

	<ul>
		<li><a href="http://bit.ly/1cpgcjo" target="_blank"><spring:message code="help.generic.link.forgotpassword.label" /></a></li>
		<li><a href="http://bit.ly/1f5LoWx" target="_blank"><spring:message code="help.generic.link.unknownemail.label" /></a></li>
		<li><a href="http://bit.ly/1aKRkmX" target="_blank"><spring:message code="help.generic.link.cantlogin.label" /></a></li>
		<li><a href="http://help.thekey.me/2013/10/30/i-want-to-change-my-email-or-password/" target="_blank"><spring:message code="help.generic.link.changecredentials.label" /></a></li>
		<li><a href="http://bit.ly/17zeNa0" target="_blank"><spring:message code="help.generic.link.changename.label" /></a></li>
		<li><a href="http://bit.ly/1h2UMhW" target="_blank"><spring:message code="help.generic.link.info.label" arguments="${brand_thekey}" /></a></li>
		<li><a href="http://bit.ly/1afLPIx" target="_blank"><spring:message code="help.generic.link.federatedlogin.label" arguments="${brand_facebook},${brand_relay}" /></a></li>
		<li><a href="http://bit.ly/1dt8nKq" target="_blank"><spring:message code="help.generic.link.deleteaccount.label" /></a></li>
		<li><a href="http://bit.ly/17zf2Sl" target="_blank"><spring:message code="help.generic.link.contactus.label" /></a></li>
	</ul>
</div>
