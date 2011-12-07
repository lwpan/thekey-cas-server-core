jQuery(document).ready(function($) {
	$("#ssoHelp_link").click(function(){
		$("#ssoHelp_popup_container").fadeToggle(300);
	});

	$("#ssoHelp_link_close").click(function(){
		$("#ssoHelp_popup_container").fadeOut(300);
	});

	$(document.documentElement).keyup(function(e){
		if(e.keyCode === 27){
			$("#ssoHelp_popup_container").fadeOut(300);
		}
	});
});
