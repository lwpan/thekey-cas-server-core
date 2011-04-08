var popupStatus = 0;

 function loadPopup(){  
	 //loads popup only if it is disabled  
	if(popupStatus==0){  
		$("#backgroundPopup").css({"opacity": "0.8"});  
		$("#backgroundPopup").fadeIn("slow");  
		$("#popupHelp").fadeIn("slow");  
		popupStatus = 1;  
	 }  
}
 
 function disablePopup(){  
  //disables popup only if it is enabled  
   if(popupStatus==1){  
	  $("#backgroundPopup").fadeOut("slow");  
	  $("#popupHelp").fadeOut("slow");  
	  popupStatus = 0;  
   }  
 } 
 
  function centerPopup(){  
	  //request data for centering 

	  var windowWidth = $(window).width();  
	  var windowHeight = $(window).height();  
	  var popupHeight = $("#popupHelp").height();  
	  var popupWidth = $("#popupHelp").width();  
	  //centering  
	  $("#popupHelp").css({  
		  "position": "absolute",  
		  "top": windowHeight/3-popupHeight/2,  
		  "left": windowWidth/4-popupWidth/2  
	  });  
	    
	  $("#backgroundPopup").css({"height": windowHeight});  
	    
  }  
  
  
   $(document).ready(function() {  
		$("#button").click(function(){
			centerPopup();
			loadPopup();
		});

		$("#popupHelpClose").click(function(){
			disablePopup();
		});

		$("#backgroundPopup").click(function(){
			disablePopup();
		});

		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus==1){
				disablePopup();
			}
		});

		$.validator.addMethod("haveNumber", function(value, element) { 
			  return this.optional(element) || /.*[0-9].*/.test(value); 
			}, "Password must have a number (0-9)");

			$.validator.addMethod("haveUpper", function(value, element) {
				return this.optional(element) || /.*[A-Z].*/.test(value);
			}, "Password must have an uppercase letter");

			$.validator.addMethod("haveSymbol", function(value, element) {
				return this.optional(element) || !/^[a-zA-Z0-9]*$/.test(value);
			}, "Password must have a symbol character");
			


	   		$("#user").validate({
	   			rules:{
	   			retypePassword:{
				required:true,
				equalTo:"#password"
	   			},
			password:{
				required:true,
				minlength:8,
				maxlength:35,
		haveNumber:true}
	  
	 },
	messages:{ 
		retypePassword:{
		required:"You must retype your password.",
		equalTo:"Your password and retype password fields did not match. Please try again."
		},  
	password:{ 
		required:"Password is a required field.",
		minlength:"Your password does not meet the minimum length requirement.",
		maxlength:"You are a son of a silly person.",
		haveNumber:"A number (0-9) is required."
		}
	}  
	   		});
		
		
   });
	
	