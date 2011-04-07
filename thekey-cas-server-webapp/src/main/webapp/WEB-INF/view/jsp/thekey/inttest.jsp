<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isELIgnored="false"%>

<html>
<head>
<META http-equiv="Content-Type" content="text/html;charset=UTF-8">

</head>
<body>
<form action='inttest.htm' accept-charset="utf-8" method="post">
VALUE:<input name='value' id='value' />

<input type='submit' value='go'/>
</form>

<hr/>
You entered: <c:out value='${model.value}'/>
</body>
</html>
