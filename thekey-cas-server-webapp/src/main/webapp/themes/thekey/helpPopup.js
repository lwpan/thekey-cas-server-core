jQuery(document).ready(function($) {
	$("#ssoHelp_link").click(function(){
//		$("#ssoHelp_popup").fadeIn(300);
		$("#ssoHelp").addClass("ssoHelp_selected");
	});

	$("#ssoHelp_link_close").click(function(){
//		$("#ssoHelp_popup").fadeOut(300);
		$("#ssoHelp").removeClass("ssoHelp_selected");
	});

	$(document.documentElement).keyup(function(e){
		if(e.keyCode === 27){
			$("#ssoHelp_link_close").click();
		}
	});
});
