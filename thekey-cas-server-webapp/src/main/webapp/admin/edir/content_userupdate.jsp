<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
	var changed = false;

	function setChanged() {
		changed = true;

		return true;
	}

	function confirmSubmit(title, message, actionName) {
		enableModalBackground(true);

		var dialog = "<table width='100%' height='100%'><tr><td align='center' valign='center'><form>"
				+ message
				+ "<p/><br/><p/>"
				+ "<input onclick='dialogWindow.commitSubmit();' class='btn' type='button' value='Yes'>&nbsp;&nbsp;"
				+ "<input onclick='dialogWindow.close();' class='btn' type='button' value='No'>"
				+ "</form></td></tr></table>";

		// Open the popup window
		dialogWindow = dhtmlwindow
				.open("confirmsubmit", "inline", dialog, title,
						"width=300px,height=150px,resize=1,scrolling=1,center=1");

		// Commit if user confirms
		dialogWindow.commitSubmit = function() {
			dialogWindow.close();
			document.getElementById("updateAction").value = actionName;
			document.edirUserUpdate.submit();
		};

		// Onclose event handler to restore flags
		dialogWindow.onclose = function() {
			enableModalBackground(false);
			// This must go last
			win.cleanup();
			return true;
		};

		// Make sure the dialog is at the very top (above ModalBackground)
		dialogWindow.setZindex(998);
		dialogWindow.show();

		return true;
	}

	function deactivate() {
		confirmSubmit(
				"Confirm Dectivation",
				"Your changes will not be saved. Are you sure that you want to deactivate the user?",
				"deactivate");
	}

	function activate() {
		confirmSubmit(
				"Confirm Activation",
				"Your changes will not be saved. Are you sure that you want to activate the user?",
				"activate");
	}

	function doAction(actionName, confirmOnChange) {
		if ((confirmOnChange == true) && (changed == true)) {
			confirmSubmit("Changes Made",
					"Your changes will not be saved. Do you want to continue?",
					actionName);
		} else {
			document.getElementById("updateAction").value = actionName;
			document.edirUserUpdate.submit();
		}

		return true;
	}
</script>

<s:form namespace="/admin/edir" action="edirUserUpdate" method="post">
	<s:hidden id="updateAction" name="updateAction" value="save" />

	<div class="formwrapper">

		<div class="groupbox" style="margin-bottom: 30px;">
			<span class="grouplabel">User Details</span>
			<div class="group">
				<table class="form">
					<tr>
						<td class="label" style="padding-bottom: 20px;">Status:</td>
						<td class="entry" style="padding-bottom: 20px;">
							<s:if test="%{ gcxUser.deactivated == true }"><span style="color: red; font-weight: bold;">Deactivated</span></s:if>
							<s:else>Active</s:else></td>
					</tr>
					<tr>
						<td class="label">First&nbsp;Name:</td>
						<td class="entry"><s:textfield name="gcxUser.firstName" size="64" maxlength="64" onchange="setChanged();" /></td>
					</tr>
					<tr>
						<td class="label">Last&nbsp;Name:</td>
						<td class="entry"><s:textfield name="gcxUser.lastName" size="64" maxlength="64" onchange="setChanged();" /></td>
					</tr>
					<tr>
						<td class="label">E-mail:</td>
						<td class="entry"><s:textfield name="gcxUser.userid" size="64" maxlength="64" onchange="setChanged();" /></td>
					</tr>
					<tr>
						<td class="label">&nbsp;</td>
						<td class="entry">&nbsp;</td>
					</tr>
					<tr>
						<td class="label">SSO&nbsp;GUID:</td>
						<td class="entry"><s:property value="gcxUser.GUID" /></td>
					</tr>
					<tr>
						<td class="label">Domains Visited:</td>
						<td class="entry"><s:textarea cols="64" name="domainsVisitedFormatted" onchange="setChanged();"></s:textarea></td>
					</tr>
					<tr>
						<td class="label">&nbsp;</td>
						<td class="entry">&nbsp;</td>
					</tr>
					<tr>
						<td class="label">Additional&nbsp;SSO&nbsp;GUID:</td>
						<td class="entry">
							<span style="white-space: pre;"><s:property value="GUIDAdditionalFormatted" /></span>
						</td>
					</tr>
					<tr>
						<td class="label">Additional Domains Visited:</td>
						<td class="entry"><s:textarea cols="64" name="domainsVisitedAdditionalFormatted" onchange="setChanged();"></s:textarea></td>
					</tr>
					<tr>
						<td cols="2" style="padding-top: 20px; padding-bottom: 10px; text-align: left;">Account&nbsp;Restrictions</td>
					</tr>
					<tr>
						<td class="label">Last&nbsp;Login</td>
						<td class="entry">
							<s:if test="%{gcxUser.loginTime == null}">never</s:if>
							<s:else>
								<s:date name="gcxUser.loginTime" format="MM/dd/yyyy 'at' hh:mm a" nice="true" />
							</s:else>
						</td>
					</tr>
					<tr>
						<td class="label">Change&nbsp;Password:</td>
						<td class="entry"><s:checkbox name="gcxUser.passwordAllowChange" onchange="setChanged();" /></td>
					</tr>
					<tr>
						<td class="label">Force&nbsp;Password&nbsp;Change:</td>
						<td class="entry"><s:checkbox name="gcxUser.forcePasswordChange" onchange="setChanged();" /></td>
					</tr>
					<tr>
						<td class="label">Account&nbsp;Disabled:</td>
						<td class="entry"><s:checkbox name="gcxUser.loginDisabled" onchange="setChanged();" /></td>
					</tr>
					<tr>
						<td class="label">Locked&nbsp;by&nbsp;Intruder:</td>
						<td class="entry"><s:checkbox name="gcxUser.locked" onchange="setChanged();" /></td>
					</tr>
				</table>
			</div>
		</div>
	</div>

	<div class="verticalspacer30"></div>
	<div class="contentbuttonbar">
		<s:if test="%{ gcxUser.deactivated == false }">
			<span class="btnGroup">
				<input id="mergeSearchBtn" type="button" class="btn" value="Merge Search" onClick="doAction( 'mergesearch', true ); return false;" />
			</span>
			<span class="btnGroup">
				<input id="deactivateBtn" type="button" class="btn" value="Deactivate" onClick="deactivate(); return false;" />
				<input id="resetPasswordBtn" type="button" class="btn" value="Reset Password" onClick="doAction( 'resetpassword', true ); return false;" />
			</span>
		</s:if>
		<s:else>
			<span class="btnGroup">
				<input id="activateeBtn" type="button" class="btn" value="Activate" onClick="activate(); return false;" />
			</span>
		</s:else>
		<span class="btnGroup">
			<input id="applyBtn" type="button" class="btn" value="Apply Changes" onClick="doAction( 'apply', false );return false;" />
			<input id="saveBtn" type="button" class="btn" value="Save" onClick="doAction( 'save', false );return false;" />
			<input id="cancelBtn" type="button" class="btn" value="Dismiss" onClick="doAction( 'cancel', true );return false;" />
		</span>
	</div>
</s:form>
