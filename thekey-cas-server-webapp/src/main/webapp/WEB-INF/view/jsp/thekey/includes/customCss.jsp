<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test='${location != "css.htm?css="}'><link href="<c:out value='${location}'/>" type="text/css" rel="stylesheet"/></c:if>
	