<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<s:head/>
<title><tiles:getAsString name="title"/></title>
</head>
<body onLoad="postLoadInitialize();displayErrorBlock( 'on' );">

<div id="modalBackground"></div>

<div id="wrapper">

<tiles:insertAttribute name="header"/>
<tiles:insertAttribute name="menu"/>

<div id="content">
<div id="breadcrumb"></div> <!-- End Breadcrumb -->

<div id="errorblock">
<div id="erroricon">
<img src="<s:url value='/images/icon_error1.gif' includeParams='none' encode='false' />">
</div>
<div id="errorcontent">
<h2>Error: <tiles:getAsString name="error"/></h2>
<tiles:insertAttribute name="content"/>
</div>
</div>

</div> <!-- End Content -->

</div> <!-- End Wrapper -->

</body>
</html>