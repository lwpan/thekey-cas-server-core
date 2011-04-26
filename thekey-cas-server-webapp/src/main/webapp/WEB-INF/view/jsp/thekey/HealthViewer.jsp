<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isELIgnored="false"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="themes/thekey/health.css" type="text/css" rel="stylesheet"/>
<title>Health Viewer: SSO</title>
</head>
<body>

<table id="statii" cellspacing="0" summary="Server Health">
<caption>SSO Server health monitor</caption>
<tr><th>Status</th><th>Description</th><th>Status</th></tr>

<c:forEach var="statusBean" items="${statuslist}" >
<tr>
<td><c:out value="${statusBean.title}"/></td>
<td><c:out value="${statusBean.description}"/></td>
<td><c:out value="${statusBean.value}"/></td>
</tr>
</c:forEach>
</table>

</body>
</html>