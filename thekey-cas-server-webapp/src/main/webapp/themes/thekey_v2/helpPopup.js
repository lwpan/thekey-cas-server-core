jQuery(document).ready(function($) {
	var popup = $('#ssoHelp_popup-wrap');

	$("#ssoHelp_link").click(function(event){
		popup.fadeToggle(300);
		event.stopPropagation()
	});

	$("#ssoHelp_link-active").click(function(event){
		popup.fadeOut(300);
		event.stopPropagation()
	});

	$(document.documentElement).keyup(function(event){
		if(event.keyCode === 27){
			popup.fadeOut(300);
		}
	});

	$('body').click(function(event) {
		if (!$(event.target).closest('#ssoHelp_popup').length) {
			popup.fadeOut(300);
		};
	});
});
