<%@ page pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html dir="${dir}">
	<%@ include file="htmlHead.jsp" %>
	<body class="${bodyClasses}">
		<%@ include file="allHeadings.jsp" %>
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
