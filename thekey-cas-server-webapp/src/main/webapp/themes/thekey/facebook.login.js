// login callback function
function theKeyFacebookLogin(form, eventId) {
	var response = FB.getAuthResponse();
	if (response) {
		// make sure form is a jQuery object
		form = jQuery(form).first();

		// generate the fb_signed_request field if it doesn't exist
		if (jQuery('input[name="fb_signed_request"]', form).length < 1) {
			form.append('<input type="hidden" name="fb_signed_request" />');
		}

		// set the fb_signed_request value
		jQuery('input[name="fb_signed_request"]', form).val(response.signedRequest);

		// submit the form using the specified eventId
		theKeySubmitForm(form, eventId);
	}
}
