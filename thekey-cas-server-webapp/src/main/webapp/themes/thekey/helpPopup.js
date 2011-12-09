jQuery(document).ready(function($) {
	var popup = $('#ssoHelp_popup-wrap');

	$("#ssoHelp_link").click(function(){
		popup.fadeToggle(300);
	});

	$("#ssoHelp_link-active").click(function(){
		popup.fadeOut(300);
	});

	$(document.documentElement).keyup(function(e){
		if(e.keyCode === 27){
			popup.fadeOut(300);
		}
	});
});
