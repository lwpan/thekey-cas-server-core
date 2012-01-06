<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="helpLabelMoreLink" scope="page">
	<a href="http://getsatisfaction.com/gcx/products/gcx_gcx_identity">http://getsatisfaction.com/gcx</a>
</c:set>

<p><spring:message code="help.label.more" arguments="${helpLabelMoreLink}"/></p>

<p class="ssoHelp_footer"><spring:message code="help.exitinstructions"/></p>
