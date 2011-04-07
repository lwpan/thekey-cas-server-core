<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

  
   $(document).ready(function() {  

		$.validator.addMethod("haveNumber", function(value, element) { 
		  return this.optional(element) || /.*[0-9].*/.test(value); 
		}, "Password must have a number (0-9)");

		$.validator.addMethod("haveUpper", function(value, element) {
			return this.optional(element) || /.*[A-Z].*/.test(value);
		}, "Password must have an uppercase letter");

		$.validator.addMethod("haveSymbol", function(value, element) {
			return this.optional(element) || !/^[a-zA-Z0-9]*$/.test(value);
		}, "Password must have a symbol character");
		
		$.validator.addMethod("comparePW", function(value, element, param)
		{
            (param[0]).val() == (param[1]).val();
		}, "Passwords do not match"); 

   		<%= session.getAttribute( "clientjavascript" ) %>
	   
   });
	
	