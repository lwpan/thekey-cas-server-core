<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<script type="text/javascript">

var pageLast = 0 ;
var pageCurrent = 0 ;
var selectedUserEmail = "" ;
var updateLabel = "<s:property value='updateButtonLabel'/>" ;


function clearAll()
{
	document.getElementById( "firstName" ).value = "" ;
	document.getElementById( "lastName" ).value = "" ;
	document.getElementById( "email" ).value = "" ;
	
	<s:if test="%{#session['usersearchresponse'] != null}">
	document.getElementById( "userSearchResults" ).style.display = "none" ;
	document.getElementById( "userSearchNavigation" ).style.display = "none" ;
	document.getElementById( "userSearchCommand" ).style.display = "none" ;
	</s:if>
}

function requestPage( n )
{
	if ( ( ( n >= 1 ) && ( n <= pageLast ) ) && ( n != pageCurrent ) ) {
		document.getElementById( "requestedPageNumber" ).value = n ;
		document.getElementById( "searchAction" ).value = "paginate" ;
		document.forms[0].submit() ;
	}
	
	return true ;
}

function requestDirect( s )
{
	var pageNumberSelect = document.getElementById( "pageNumberSelect" ) ;
	
	return requestPage( pageNumberSelect.options[pageNumberSelect.selectedIndex].value ) ;
}

function selectUser( cn )
{
    if ( selectedUserEmail.length > 0 ) {
		document.getElementById( "cn_" + selectedUserEmail ).style.background = "white" ;
	}
	
    document.getElementById( "cn_" + cn ).style.background = "#E0E0E0" ;
    document.getElementById( "userSearchCommand" ).style.display = "block" ;
    
    selectedUserEmail = cn ;
}

function updateSelected()
{
    if ( selectedUserEmail.length > 0 ) {
		document.getElementById( "searchAction" ).value = "update" ;
		document.getElementById( "selectedUserEmail" ).value = selectedUserEmail ;
		document.getElementById( "requestedPageNumber" ).value = pageCurrent ;
		document.forms[0].submit() ;
	} else {
	 	userPopupMessage( "Error", "Please highlight a user before selecting the " + updateLabel + " button." ) ;
	}
}

</script>

<div id="introblock">
	<tiles:insertAttribute name="intro" />
</div>

<s:form namespace="/admin/edir" action="%{#session['searchactionname']}" method="post">
	<s:hidden id="requestedPageNumber" name="requestedPageNumber" value="1" />
	<s:hidden id="searchAction" name="searchAction" value="search" />
	<s:hidden id="selectedUserEmail" name="selectedUserEmail" value="" />

	<div class="formwrapper">
		<div class="groupbox" style="margin-bottom: 30px;">
			<span class="grouplabel">User Search</span>
			<div class="group">
				<table class="form">
					<tr>
						<td class="label">First&nbsp;Name:</td>
						<td class="entry"><s:textfield id="firstName" name="firstName" size="64" maxlength="64" /></td>
					</tr>
					<tr>
						<td colspan="2" style="text-align: center; padding-top: 0.25em; padding-bottom: 0.25em; font-weight: bold;">or</td>
					</tr>
					<tr>
						<td class="label">Last&nbsp;Name:</td>
						<td class="entry"><s:textfield id="lastName" name="lastName" size="64" maxlength="64" /></td>
					</tr>
					<tr>
						<td colspan="2" style="text-align: center; padding-top: 0.25em; padding-bottom: 0.25em; font-weight: bold;">or</td>
					</tr>
					<tr>
						<td class="label">E-mail:</td>
						<td class="entry"><s:textfield id="email" name="email" size="64" maxlength="64" /></td>
					</tr>
				</table>
			</div>
		</div>

		<s:if test="%{#session['usersearchresponse'] != null}">
			<div id="userSearchCommand" style="clear: both; text-align: right; background: #E0E0E0; padding: 4px 5px 4px 10px; width: 887px; margin: 0px 0px 0px 0px; bottom: 0px;">
				<input id="updateSelectedBtn" type="button" class="btn" value="<s:property value='updateButtonLabel'/>" onClick="updateSelected();" />
			</div>
		</s:if>
		<div class="listscroll" style="clear: both;">
			<s:if test="%{#session['usersearchresponse'] != null}">
				<table id="userSearchResults"
					style="width: 900px; padding: 0; margin: 0; border: none; background: white;">
					<tr>
						<th style="width: 10%; background: #3c3c3c; color: white; text-align: left; padding: 5px 10px 5px 10px; font-size: 80%;">Deactivated</th>
						<th style="width: 60%; background: #3c3c3c; color: white; text-align: left; padding: 5px 10px 5px 10px; font-size: 80%;">E-mail Address</th>
						<th style="width: 30%; background: #3c3c3c; color: white; text-align: left; padding: 5px 10px 5px 10px; font-size: 80%;">Full Name</th>
					</tr>
					<s:iterator value="session.usersearchcurrentpage" id="user">
						<tr style="cursor: pointer;" id='cn_<s:property value="email"/>' onClick="selectUser('<s:property value="email"/>');">
							<td style="width: 10%; padding-left: 10px; text-align: center;"><s:if test="%{ deactivated == true }">X</s:if></td>
							<td style="width: 60%; padding-left: 10px;"><s:property value="userid" /></td>
							<td style="width: 30%; padding-left: 10px;"><s:property value="firstName" />&nbsp;<s:property value="lastName" /></td>
						</tr>
					</s:iterator>
				</table>
			</s:if>
		</div>
		<s:if test="%{ (#session['usersearchresponse'] != null) && (#session['usersearchresponse'].pageCount > 1)}">
			<div id="userSearchNavigation" style="text-align: right; background: #E0E0E0; padding: 4px 5px 4px 10px; width: 887px; margin: 0px 0px 10px 0px; bottom: 0px;">
				<input type="button" class="btn" value="<< First" onClick="requestPage( 1 );" />
				<input type="button" class="btn" style="margin-right: 20px;" value="< Previous" onClick="requestPage( pageCurrent - 1 );" />
				<b>Page</b>
				<select id="pageNumberSelect" style="margin: 0px 5px 0px 5px; width: 60px;" onchange="requestDirect();">
<!-- TODO: javascript???? really??? -->
<script type="text/javascript">
pageCurrent = <s:property value="session.usersearchresponse.pageNumber"/> ;
pageLast = <s:property value="session.usersearchresponse.pageCount"/> ;
for( var i=1; i<=pageLast; ++i ) {
	document.write( "<option value='" + i + "'" ) ;
	if ( i == pageCurrent ) {
		document.write( " selected" ) ;
	}
	document.writeln( ">" + i + "</option>" ) ;
}
</script>
				</select>
				<b>of <s:property value="session.usersearchresponse.pageCount" /></b>
				<input type="button" class="btn" style="margin-left: 20px;" value="Next >" onClick="requestPage( pageCurrent + 1 );" />
				<input type="button" class="btn" value="Last >>" onClick="requestPage( pageLast );" />
			</div>
		</s:if>
	</div>

	<div class="verticalspacer30"></div>
	<div class="contentbuttonbar">
		<tiles:insertAttribute name="control" />
	</div>
</s:form>
