<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

<!-- theme/styling -->
<c:url var="cssUri" value="${themeUri}/cas.css" />
<link href="<c:out value="${cssUri}" />" type="text/css" rel="stylesheet"/>
<%@ include file="customCss.jsp"  %>

<!-- Javascript library -->
<c:url var="jqueryUri" value="${themeUri}/jquery.js" />
<script src="<c:out value="${jqueryUri}" />" type="text/javascript"></script>

<!-- Help Menu -->
<c:url var="helpUri" value="${themeUri}/helpPopup.js" />
<script src="<c:out value="${helpUri}" />" type="text/javascript"></script>
