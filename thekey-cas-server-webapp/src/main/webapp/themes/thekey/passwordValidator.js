jQuery(document).ready(function($) {
	$.validator.addMethod("haveNumber", function(value, element) {
		return this.optional(element) || /.*[0-9].*/.test(value);
	}, "Password must have a number (0-9)");

	$.validator.addMethod("haveUpper", function(value, element) {
		return this.optional(element) || /.*[A-Z].*/.test(value);
	}, "Password must have an uppercase letter");

	$.validator.addMethod("haveSymbol", function(value, element) {
		return this.optional(element) || !/^[a-zA-Z0-9]*$/.test(value);
	}, "Password must have a symbol character");

	// function that will enable password validation for the selected form
	$.extend($.fn, {
		enablePwv: function(password, retype, messages) {
			this.validate({
				onsubmit: false
			});

			$(password, this).addClass('password').rules("add", {
				messages: messages
			});
			$(retype, this).addClass('password').rules("add", {
				equalTo: password,
				messages: messages
			});
		}
	});
});
