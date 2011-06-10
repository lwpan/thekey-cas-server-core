<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">

function confirmSubmit()
{
	enableModalBackground( true ) ;
      
  	var dialog = "<table width='100%' height='100%'><tr><td align='center' valign='center'><form>" +
                 "Commiting a merge can not be undone. Are you sure that you want to perform this merge action?<p/><br/><p/>" + 
                 "<input onclick='dialogWindow.commitSubmit();' class='btn' type='button' value='Yes'>&nbsp;&nbsp;" + 
                 "<input onclick='dialogWindow.close();' class='btn' type='button' value='No'>" +
                 "</form></td></tr></table>" ;

  	// Open the popup window  
 	dialogWindow = dhtmlwindow.open( 
  	  "confirmsubmit", 
 	  "inline", 
 	  dialog, 
 	  "Confirm Merge Commit", 
 	  "width=300px,height=150px,resize=1,scrolling=1,center=1" ) ;

	// Commit if user confirms
 	dialogWindow.commitSubmit = function() {
  		dialogWindow.close() ;
		document.getElementById( "mergeAction" ).value = "merge" ;
		document.edirMergeUpdate.submit() ;
  	} ;

  	// Onclose event handler to restore flags  
  	dialogWindow.onclose = function() {
    	enableModalBackground( false ) ;
    	// This must go last
    	win.cleanup() ;
     	return true;
  	} ;

  	// Make sure the dialog is at the very top (above ModalBackground)
  	dialogWindow.setZindex( 998 ) ;
  	dialogWindow.show() ;    

	return true ;
}

function doAction( actionName )
{
	document.getElementById( "mergeAction" ).value = actionName ;
	document.edirMergeUpdate.submit() ;
	
	return true ;
}

</script>

<s:form namespace="/edir" action="edirMergeUpdate" method="post">
<s:hidden id="mergeAction" name="mergeAction" value="merge"/>

<div class="formwrapper">

<div class="groupbox" style="margin-bottom: 30px;">
<span class="grouplabel">User Details</span>
<div class="group">
<table class="form">
<tr><td class="label" style="padding-bottom: 20px;">Status:</td>
<td class="entry" style="padding-bottom: 20px;">
<s:if test="%{ gcxUser.deactivated == true }">
<span style="color: red; font-weight: bold;">Deactivated</span>
</s:if>
<s:else>Active</s:else>
</td></tr>
<tr><td class="label">First&nbsp;Name:</td><td class="entry"><s:property value="gcxUser.firstName"/></td></tr>
<tr><td class="label">Last&nbsp;Name:</td><td class="entry"><s:property value="gcxUser.lastName"/></td></tr>
<tr><td class="label">E-mail:</td><td class="entry"><s:property value="gcxUser.userid"/></td></tr>
<tr><td class="label">&nbsp;</td><td class="entry">&nbsp;</td></tr>
<tr><td class="label">SSO&nbsp;GUID:</td><td class="entry"><s:property value="gcxUser.GUID"/></td></tr>
<tr><td class="label">Domains Visited:</td><td class="entry"><span style="white-space: pre;"><s:property value="domainsVisitedFormatted"/></span></td></tr>
<tr><td class="label">&nbsp;</td><td class="entry">&nbsp;</td></tr>
<tr><td class="label">Additional SSO&nbsp;GUID:</td><td class="entry"><span style="white-space: pre;"><s:property value="GUIDAdditionalFormatted"/></span></td></tr>
<tr><td class="label">Additional Domains Visited:</td><td class="entry"><span style="white-space: pre;"><s:property value="domainsVisitedAdditionalFormatted"/></span></td></tr>
<tr><td cols="2" style="padding-top: 20px; padding-bottom: 10px; text-align: left;">Account Restrictions</td></tr>
<tr><td class="label">Last&nbsp;Login</td><td class="entry">
<s:if test="%{gcxUser.loginTime == null}">never</s:if>
<s:else><s:date name="gcxUser.loginTime" format="MM/dd/yyyy 'at' hh:mm a" nice="true"/></s:else>
</td></tr>
<tr><td class="label">Change&nbsp;Password:</td><td class="entry"><s:property value="gcxUser.passwordAllowChange"/></td></tr>
<tr><td class="label">Force&nbsp;Password&nbsp;Change:</td><td class="entry"><s:property value="gcxUser.forcePasswordChange"/></td></tr>
<tr><td class="label">Account&nbsp;Disabled:</td><td class="entry"><s:property value="gcxUser.loginDisabled"/></td></tr>
<tr><td class="label">Locked&nbsp;by&nbsp;Intruder:</td><td class="entry"><s:property value="gcxUser.locked"/></td></tr>
</table>

</div> <!-- End Group -->
</div> <!-- End Groupbox -->

</div> <!-- End Formwrapper -->

<div class="verticalspacer30"></div>
<div class="contentbuttonbar">
<input id="mergeBtn" type="button" class="btn" value="Merge" onClick="confirmSubmit();return false;">
<input id="cancelBtn" type="button" class="btn" value="Dismiss" onClick="doAction( 'cancel' );return false;">
</div> <!-- End Contentbuttonbar -->

</s:form>
