<#--
/*
 * $Id: head.ftl,v 1.3 2008/03/28 17:49:29 gcrider Exp $
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
<link rel="stylesheet" href="<@s.url value='/struts/css_xhtml/styles.css' includeParams='none' encode='false' />" type="text/css" />
<link rel="stylesheet" href="<@s.url value='/style/screen-style.css' encode='false' includeParams='none'/>" type="text/css" />
<link rel="stylesheet" href="<@s.url value='/style/theme_css_xhtml.css' encode='false' includeParams='none'/>" type="text/css" />
<link rel="stylesheet" href="<@s.url value='/style/dhtmlwindow.css' encode='false' includeParams='none'/>" type="text/css" />
<#include "/${parameters.templateDir}/${themeProperties.parent}/head.ftl" />

<!--
<script type="text/javascript" src="<@s.url value='/struts/ajax/dojoRequire.js' includeParams='none' encode='false'  />"></script>
<script type="text/javascript" src="<@s.url value='/struts/CommonFunctions.js' includeParams='none' encode='false'/>"></script>
  -->

<script type="text/javascript" src="<@s.url value='/js/idm.js' encode='false' includeParams='none'/>"></script>

<script type="text/javascript">
    //dojo.require( "dojo.rpc.*" ) ;
    var imagesURI = "<@s.url value='/images' encode='false' includeParams='none'/>" ;
    preLoadInitialize() ;
</script>

<script type="text/javascript" src="<@s.url value='/js/dhtmlwindow.js' encode='false' includeParams='none'/>"></script>
        