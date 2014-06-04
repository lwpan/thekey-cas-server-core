<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="keyfn" uri="/WEB-INF/tags.tld" %>
<%@include file="includes/top.jsp"%>
<form:form action="${registeredService.id ge 0 ? 'edit.html' : 'add.html'}?id=${fn:escapeXml(param.id)}" cssClass="v" cssStyle="width:75%;" commandName="${commandName}">

		<c:if test="${not empty successMessage}">
			<div id="msg" class="info">${successMessage}</div>
		</c:if>

		<spring:hasBindErrors name="${commandName}">
			<div id="msg" class="errors">
			<spring:message code="application.errors.global" />
		</div>
		</spring:hasBindErrors>
	<fieldset class="repeat"><legend><spring:message code="${pageTitle}" /></legend>
	<div class="fieldset-inner">
		<p class="instructions"><spring:message code="management.services.add.instructions" /></p>
		<span class="oneField" style="display:block; margin:5px 0;">
			<label for="name" class="preField"><spring:message code="management.services.add.property.name" /> </label>
			<form:input path="name" size="51" maxlength="50" cssClass="required" cssErrorClass="error" />
			<form:errors path="name" cssClass="formError" />
			<br />
		</span>

			<c:if test="${keyfn:instanceOf(registeredService, 'org.ccci.gto.cas.services.TheKeyRegisteredService')}">
				<span class="oneField">
					<label for="contactEmail" class="preField">Contact Email</label>
					<form:input path="contactEmail" size="51" maxlength="255" cssClass="required" cssErrorClass="error" />
					<form:errors path="contactEmail" cssClass="formError" />
					<br />
				</span>
			</c:if>
		
		<span class="oneField">
			<label for="serviceId" class="preField"><spring:message code="management.services.add.property.serviceUrl" /></label>
			<form:input path="serviceId" size="51" maxlength="255" cssClass="required" cssErrorClass="error" />
				<span class="oneChoice">
					<form:checkbox path="regex" value="true" cssClass="check" />
					<label for="regex1" id="regex-l" class="postField">Regular Expression</label>
				</span>
			<form:errors path="serviceId" cssClass="formError" />
<%--
			<br />
			<div class="hint"><spring:message code="management.services.add.property.serviceUrl.instructions" /></div>
--%>
		</span>

			<c:if test="${keyfn:instanceOf(registeredService, 'org.ccci.gto.cas.services.TheKeyRegisteredService')}">
				<span class="oneField">
					<label for="templateCssUrl" class="preField">Template URL</label>
					<form:input path="templateCssUrl" size="51" maxlength="255" cssErrorClass="error" />
					<form:errors path="templateCssUrl" cssClass="formError" />
					<form:select path="viewName">
						<optgroup label="Current Layout">
							<form:option label="TheKey (v4)" value="thekey_views" />
						</optgroup>
						<optgroup label="Legacy Layouts">
							<form:option label="TheKey (v2)" value="thekey_v2_views" />
						</optgroup>
					</form:select>
					<br />
				</span>
			</c:if>
		
		<span class="oneField">
			<label for="description" class="preField"><spring:message code="management.services.add.property.description" /></label>
			<form:textarea path="description" cssClass="required" cssErrorClass="error" cols="49" rows="5" />
			<form:errors path="description" cssClass="formError" />
			<br />
		</span>
		
<%--
		<span class="oneField">
			<label for="theme" class="preField"><spring:message code="management.services.add.property.themeName" /></label>
			<form:input path="theme" size="11" maxlength="10" cssClass="required" cssErrorClass="error" />
			<form:errors path="theme" cssClass="formError" />
			<br />
		</span>
--%>
 
		<span class="oneField">
			<span class="label preField"><spring:message code="management.services.add.property.status" /></span>
			<span>
				<span class="oneChoice">
					<form:checkbox path="enabled" value="true" cssClass="check" />
					<label for="enabled1" id="enabled-l" class="postField"><spring:message code="management.services.add.property.status.enabled" /></label>
				</span>
				<span class="oneChoice">
					<form:checkbox path="allowedToProxy" value="true" cssClass="check" />
					<label for="allowedToProxy1" id="proxy-l" class="postField"><spring:message code="management.services.add.property.status.allowedToProxy" /></label>
				</span>
				<span class="oneChoice">
					<form:checkbox path="ssoEnabled" value="true" cssClass="check" />
					<label for="ssoEnabled1" id="ssl-l" class="postField"><spring:message code="management.services.add.property.status.ssoParticipant" /></label>
				</span>
				
				<span class="oneChoice">
					<form:checkbox path="anonymousAccess" value="true" cssClass="check" />
					<label for="anonymousAccess1" id="anonymousAccess-l" class="postField"><spring:message code="management.services.add.property.status.anonymousAccess" /></label>
				</span>
			</span>
			<br/>
		</span>
			
		<c:if test="${keyfn:instanceOf(registeredService, 'org.ccci.gto.cas.services.TheKeyRegisteredService')}">
			<c:if test="${registeredService.id gt 0}">
				<span class="oneField">
					<span class="label preField">API Key</span>
					<input id="apiKey" size="51" readonly="readonly" value="${registeredService.apiKey}" />
					<label class="postField"><a id="generateApiKey" href="#" onclick="$('#generateApiKey-confirm').dialog('option', 'position', 'center').dialog('open');return false;">Generate API Key</a></label>
					<span class="oneChoice">
						<form:checkbox path="apiEnabled" value="true" cssClass="check" />
						<label for="apiEnabled1" id="apiEnabled-1" class="postField">Enable API</label>
					</span>
				</span>

				<div id="generateApiKey-confirm" title="Generate API Key?" style="display:none;">
					<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>This will generate a new API key and immediately invalidate any existing API key. Are you sure?</p>
				</div>
				<script type="text/javascript">
					jQuery(function ($) {
						// make generateApiKey a dialog
						$("#generateApiKey-confirm").dialog({
							autoOpen: false,
							resizable: false,
							modal: true,
							buttons: {
								"Generate API Key": function() {
									var dialog = $(this);
									$.getJSON('api/generateApiKey.json?id=${registeredService.id}', function(data) {
										if(!data.error) {
											$('#apiKey').val(data.apiKey);
										}
										dialog.dialog("close");
									});
								},
								Cancel: function() {
									$(this).dialog("close");
								}
							}
						});

						// attach dialog styles
						var link = $("<link type='text/css' rel='stylesheet' />");
						link.attr('href', 'https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.5/themes/base/jquery-ui.css');
						$("head").append(link);
					});
				</script>

				<span class="oneField">
					<span class="label preField">Supported APIs</span>
					<form:select path="supportedApis" multiple="true">
						<form:option value="attributes" />
						<form:option value="linkedIdentities" />
					</form:select>
				</span>
			</c:if>

			<span class="oneField">
				<span class="label preField">The Key</span>
				<span>
					<span class="oneChoice">
						<form:checkbox path="legacyLogin" value="true" cssClass="check" />
						<label for="legacyLogin1" id="legacyLogin-l" class="postField">Legacy Login</label>
					</span>
					<span class="oneChoice">
						<form:checkbox path="legacyHeaders" value="true" cssClass="check" />
						<label for="legacyHeaders1" id="legacyHeaders-l" class="postField">Legacy Headers</label>
					</span>
				</span>
				<br />
				<div class="hint">Legacy Headers and Legacy Login should not be used, they are here to support several older clients</div>
			</span>
		</c:if>

		<span class="oneField"><label class="preField ieFix" style="float:left;"><spring:message code="management.services.add.property.attributes" /></label>
			<form:select path="allowedAttributes" items="${availableAttributes}" multiple="true" />
		</span>
	     
    	<span class="oneField"><label class="preField ieFix" style="float:left;"><spring:message code="management.services.manage.label.usernameAttribute" /></label>
    		<form:select path="usernameAttribute" items="${availableUsernameAttributes}" />
    		<form:errors path="usernameAttribute" cssClass="formError" />
    	</span>
      		            
	    <span class="oneChoice">
	      <form:checkbox path="ignoreAttributes" value="true" cssClass="check" />
	      <label for="ignoreAttributes1" id="ignoreAttributes-l" class="postField"><spring:message code="management.services.add.property.ignoreAttributes" /></label>
	    </span>
	    
	    <span class="oneField">
	      <label for="theme" class="preField"><spring:message code="management.services.add.property.evaluationOrder" /></label>
	      <form:input path="evaluationOrder" size="11" maxlength="10" cssClass="required" cssErrorClass="error" />
	      <form:errors path="evaluationOrder" cssClass="formError" />
	      <br />
	    </span>

	</div>
	</fieldset>
	<div class="actions">
		<button type="submit" class="primaryAction" id="submit-wf_FormGardenDemonst" value="<spring:message code="management.services.add.button.save" />">
		<spring:message code="management.services.add.button.save" /></button> 
		or <a href="manage.html" style="color:#b00;"><spring:message code="management.services.add.button.cancel" /></a>
	</div>
</form:form>
<div id="tableWrapper" style="display:none"><table id="scrollTable"><tbody /></table></div>
<%@include file="includes/bottom.jsp" %>