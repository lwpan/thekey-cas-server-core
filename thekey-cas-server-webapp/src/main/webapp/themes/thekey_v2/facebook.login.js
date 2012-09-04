// login callback function
function theKeyFacebookLogin(form, eventId) {
	var response = FB.getAuthResponse();
	if(response) {
		// make sure form is a jQuery object
		form = jQuery(form).first();

		// generate the _eventId and fb_signed_request fields if they don't exist
		if(jQuery('input[name="_eventId"]', form).length < 1) {
			form.append('<input type="hidden" name="_eventId" />');
		}
		if(jQuery('input[name="fb_signed_request"]', form).length < 1) {
			form.append('<input type="hidden" name="fb_signed_request" />');
		}

		// set the _eventId and fb_signed_request values
		jQuery('input[name="_eventId"]', form).val(eventId);
		jQuery('input[name="fb_signed_request"]', form).val(response.signedRequest);
		form.submit();
	}
}

// function that will unlink a facebook account
function theKeyFacebookUnlink(form, eventId) {
	form = jQuery(form).first();

	// generate the _eventId field if it doesn't exist
	if(jQuery('input[name="_eventId"]', form).length < 1) {
		form.append('<input type="hidden" name="_eventId" />');
	}

	// set the _eventId
	jQuery('input[name="_eventId"]', form).val(eventId);
	form.submit();
}
