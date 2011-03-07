<%@ page import="org.ccci.gcx.idm.web.Constants" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>
<%@ page import="org.ccci.gcx.idm.web.RedListBean" %>

<%
/*
* This view is called by login flow when a successful login occurs. In order to support
* the Campus Crusade modifications to CAS, we return custom headers on succesful login.
* kb 7/8/10
*/
	ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
	RedListBean redlist = (RedListBean) context.getBean("redListBean");

    String redirectURL = (String) session.getAttribute( Constants.SESSIONATTRIBUTE_LOCATION );

	if(redlist.isListedService(redirectURL))
	{
		response.setHeader(Constants.RESPONSEHEADER_TICKET,(String)session.getAttribute(Constants.SESSIONATTRIBUTE_TICKET));
		response.setHeader(Constants.RESPONSEHEADER_SERVICE,(String)session.getAttribute(Constants.SESSIONATTRIBUTE_SERVICE));
	}
	
    response.sendRedirect(redirectURL);
%>

