// function that will submit a webform with a specific eventId
function theKeySubmitForm(form, eventId) {
	form = jQuery(form).first();

	// generate the _eventId field if it doesn't exist
	if (jQuery('input[name="_eventId"]', form).length < 1) {
		form.append('<input type="hidden" name="_eventId" />');
	}

	// set the _eventId
	jQuery('input[name="_eventId"]', form).val(eventId);
	form.submit();
}
