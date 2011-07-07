<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<s:head/>
<title><tiles:getAsString name="title"/></title>
</head>
<body onLoad="postLoadInitialize();">

<div id="modalBackground"></div>

<div id="wrapper">

<tiles:insertAttribute name="header"/>
<tiles:insertAttribute name="menu"/>

<div id="content">
<div id="breadcrumb"></div> <!-- End Breadcrumb -->

<div id="errorblock">
<div id="erroricon">
<img src="<s:url value='/struts/thekey/images/icon_error1.gif' includeParams='none' encode='false' />">
</div>
<div id="errorcontent">
<h2>Error: Please correct the following and resubmit your request:</h2>
<s:actionmessage />
<s:actionerror />
<s:fielderror />
</div>
</div>

<div id="statusblock">
<div id="statusicon">
<img src="<s:url value='/struts/thekey/images/info.jpg' includeParams='none' encode='false' />">
</div>
<div id="statuscontent">
<s:property value="session.statusmessage"/>
</div>
</div>
<s:component template="statusmessage.ftl"/>

<tiles:insertAttribute name="content"/>

</div> <!-- End Content -->

</div> <!-- End Wrapper -->

</body>
</html>