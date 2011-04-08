var popupStatus = 0;

function setFocus(formN,elementN) {
	if(document.forms.length > 0) {
            try {
		document.forms[formN].elements[elementN].focus();
            } catch(e) {
            }
	}
}

function loadPopup(){
         //loads popup only if it is disabled  
        if(popupStatus==0){
                popupStatus = 1;
        }
}

function disablePopup(){
        //disables popup only if it is enabled
        if(popupStatus==1){
                popupStatus = 0;
        }
 }


$(document).ready(function() {

        $("#ssoHelp_link").click(function(){
                loadPopup();
                $("#ssoHelp").addClass("ssoHelp_selected");
        });


        $("#ssoHelp_link_close").click(function(){
                disablePopup();
                $("#ssoHelp").removeClass("ssoHelp_selected");
        });


        $(document).keypress(function(e){
                if(e.keyCode==27 && popupStatus==1){
                        disablePopup();
                }
        });

});

