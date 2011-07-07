<#--
/*
 * $Id: Action.java 502296 2007-02-01 17:33:39Z niallp $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
-->
<script language="JavaScript" type="text/javascript">
    // Dojo configuration
    djConfig = {
        baseRelativePath: "<@s.url includeParams='none' value='/struts/dojo' includeParams="none" encode='false'/>",
        isDebug: ${parameters.debug?default(false)},
        bindEncoding: "${parameters.encoding}",
        // Changed this to false since it is causing problems with newer browsers such as Firefox 3. When
        // debugAtAllCosts is true, it causes the page to reload after the initial load and then spin forever.
        // We are creating this new version of simple/head.ftl to override the one that comes stock with
        // Struts2
        debugAtAllCosts: ${parameters.debug?default(false)}, // not needed, but allows the Venkman debugger to work with the includes
    };
</script>

<!--
<script type="text/javascript" src="<@s.url includeParams='none' value='/struts/dojo/dojo.js' includeParams="none" encode='false'/>"></script>
<script type="text/javascript" src="<@s.url includeParams='none' value='/struts/simple/dojoRequire.js' includeParams="none" encode='false'/>"></script>
  -->