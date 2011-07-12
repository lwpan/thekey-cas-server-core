<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="menu">
	<ul>
		<s:if test="%{#session['authentication']==null}">
			<li><a href="<s:url value='/admin/login/loginPrompt.action' encode='false' includeParams='none'/>" title="<s:text name='tooltip.login'/>">Login</a></li>
		</s:if>
		<s:else>
			<li><a href="<s:url value='/admin/edir/edirUserSearchPrompt.action' encode='false' includeParams='none'/>" title="<s:text name='tooltip.edirusersearch'/>">eDirectory Admin</a></li>
			<li><a onClick="javascript:userPopupMessage( 'Operation Not Available', 'Sorry but that operation is not currently available.' );" title="<s:text name='tooltip.casadmin'/>"><u>CAS Administration</u></a></li>
			<li><a href="<s:url value='/admin/login/logout.action' encode='false' includeParams='none'/>" title="<s:text name='tooltip.logout'/>">Logout</a></li>
		</s:else>
	</ul>
</div>
