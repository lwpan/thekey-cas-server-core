<%@ page pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html dir="${dir}">
	<%@ include file="htmlHead.jsp" %>
	<body class="${bodyClasses}">
		<c:if test="${includeFb}">
			<div id="fb-root"></div>
		</c:if>

		<div class="main">
			<div class="main-in1">
				<div class="main-in2">
					<div class="main-in3">

						<!-- the header is left for use if we ever need to use it. but for now it's unused -->
						<div class="header">
							<div class="header-in">
								<p></p>
							</div>
						</div> <!-- .header" -->

						<!--  extra_header is for potential client css style needs -->
						<div class="extra_header1"></div>
						<div class="extra_header2"></div>

						<%@ include file="menu.jsp" %>

						<div class="content">
							<div class="content-in">

								<div class="content_header">
									<div class="content_header-in">
										<p>
											<spring:message code="${message_header}" arguments="${args_header}" />
										</p>
									</div> <!-- .content_header-in -->
								</div> <!-- .content_header -->

								<div class="content_body">
									<div class="content_body-in">

										<div class="mainContent">
											<div class="mainContent-in">
