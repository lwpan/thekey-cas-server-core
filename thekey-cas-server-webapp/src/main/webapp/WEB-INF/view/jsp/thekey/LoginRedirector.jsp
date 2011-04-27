<%@ page import="org.ccci.gcx.idm.web.Constants" %>

<%
/*
* This view is called by login flow when a successful login occurs. In order to support
* the Campus Crusade modifications to CAS, we return custom headers on succesful login.
* kb 7/8/10
*/
    String redirectURL = (String) session.getAttribute( Constants.SESSIONATTRIBUTE_LOCATION );

    response.sendRedirect(redirectURL);
%>

