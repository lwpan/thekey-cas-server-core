<%@ taglib prefix="s" uri="/struts-tags" %>

<s:submit theme="simple" type="input" cssClass="btn" label="Search" value="Search"/>
<input type="button" class="btn" value="Clear All" onClick="clearAll(); return false;">
<input type="button" class="btn" value="Cancel" onClick="window.location.href='<s:url value='/edir/edirUserUpdateRestore.action' encode='false' includeParams='none'/>';return false;">
