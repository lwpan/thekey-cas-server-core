<%@ taglib prefix="s" uri="/struts-tags" %>

<s:form namespace="/admin/login" action="login" method="post">
	<div class="formwrapper">
		<div class="groupbox">
			<span class="grouplabel">Login</span>
			<div class="group">
				<table class="form">
					<tr>
						<td class="label">Userid:</td>
						<td class="entry"><s:textfield name="email" size="64" maxlength="64"/></td>
					</tr>
					<tr>
						<td class="label">Password:</td>
						<td class="entry"><s:password name="password"/></td>
					</tr>
				</table>
			</div>
		</div>
	</div>

	<div class="verticalspacer30"></div>

	<div class="contentbuttonbar">
		<s:submit theme="simple" type="input" cssClass="btn" label="Login" value="Login"/>
		<input type="button" class="btn" value="Cancel" onClick="alert( 'Cancel' );">
	</div>
</s:form>
