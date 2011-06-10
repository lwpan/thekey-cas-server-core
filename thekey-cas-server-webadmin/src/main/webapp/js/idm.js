var userMessageDialogWindow ;

var statusBlockEnabled = false ;


function clearStatus()
{
  var ele = document.getElementById( "status" ) ;
  ele.innerHTML = "" ;
  ele.style.display = "none" ;
}


function displayStatus( text )
{
  var ele = document.getElementById( "status" ) ;
  ele.innerHTML = text ;
  ele.style.display = "block" ;
  setTimeout( 'clearStatus()', 5000 ) ;
}


function displayErrorBlock( mode )
{
  var ele = document.getElementById( "errorblock" ) ;
  
  if ( mode == "on" ) {
    ele.style.display = "block" ;
  } else {
    ele.style.display = "none" ;
  }
  
  return true ;
}


function displayStatusBlock( mode )
{
  var ele = document.getElementById( "statusblock" ) ;
	  
  if ( mode == "on" ) {
    ele.style.display = "block" ;
    statusBlockEnabled = true ;
  } else {
    ele.style.display = "none" ;
    statusBlockEnabled = false ;
  }
	  
  return true ;
}


function optionSort( opt )
{
  for ( var i=0; i<(opt.length-1); i=i+1 ) {
    for ( var j=i+1; j<opt.length; j=j+1) {
      if ( opt.options[j].text < opt.options[i].text ) {
        var option1 = new Option( opt.options[i].text, opt.options[i].value, false, false ) ;
        var option2 = new Option( opt.options[j].text, opt.options[j].value, false, false ) ;
        opt.options[i] = option2 ;
        opt.options[j] = option1 ;
      }
    }
  }
}


function transferOption( src, dest ) 
{
  var i ;
  var l ;
  
  for ( i=0, l=src.length; i<l; i=i+1 ) {
    if ( src.options[i].selected ) {
      var transferee = new Option( src.options[i].text, src.options[i].value, false, false ) ;
      dest.options[dest.length] = transferee ;
    }
  }
  
  for( i=src.length-1; i>=0; i=i-1 ) {
    if ( src.options[i].selected ) {
      src.options[i] = null;
    }
  }
  
  optionSort( dest ) ;
  
  return true ;
}


function transferOptions( src, dest )
{
  var i ;
  
  for( i=0; i<src.length; i=i+1 ) {
    var transferee = new Option( src.options[i].text, src.options[i].value, false, false ) ;
    dest.options[dest.length] = transferee ;
  }
  
  for( i=src.length-1; i>=0; i=i-1 ) {
    src.options[i] = null;
  }
  
  optionSort( dest ) ;

  return true ;
}


function selectAllOptions( src )
{
  var i ;
  
  for( i=0; i<src.length; i=i+1 ) {
    src.options[i].selected = true ;
  }

  return true ;
}


function toggleInstruction()
{
  var ele ;
  
  ele = document.getElementById( "instruction" ) ;
  if ( ele.style.display == "block" ) {
    ele.style.display = "none" ;
  } else {
    ele.style.display = "block" ;
  }
  
  ele = document.getElementById( "instruction_icon" ) ;
  if ( ele.style.display == "block" ) {
    ele.style.display = "none" ;
  } else {
    ele.style.display = "block" ;
  }
}


function initInstruction()
{
  var eleDiv = document.getElementById( "instruction" ) ;
  var eleIcon = document.getElementById( "instruction_icon" ) ;

  eleDiv.style.display = "block" ;
  eleIcon.style.display = "none" ;
}


function enableModalBackground( enable )
{
  var modalBackground = document.getElementById( "modalBackground" ) ;
  if ( enable ) {
    modalBackground.style.display = "block" ;
  } else {
    modalBackground.style.display = "none" ;
  }
  
}

function userPopupMessage( title, message )
{
  enableModalBackground( true ) ;
      
  var dialog = "<table width='100%' height='100%'><tr><td align='center' valign='center'><form>" +
               message + "<p><br><p>" +
               "<input onclick='userMessageDialogWindow.close();' class='btn' type='button' value='Close'>" +
               "</form></td></tr></table>" ;

  // Open the popup window  
  userMessageDialogWindow = dhtmlwindow.open( 
    "usermessagedialogwindow", 
    "inline", 
    dialog, 
    title, 
    "width=300px,height=150px,resize=1,scrolling=1,center=1" ) ;

  // On close event handler to restore flags  
  userMessageDialogWindow.onclose = function() {
    enableModalBackground( false ) ;
    // This must go last
    win.cleanup() ;
     return true;
  } ;

  // Make sure the dialog is at the very top (above ModalBackground)
  userMessageDialogWindow.setZindex( 998 ) ;
  userMessageDialogWindow.show() ;    

  return false ;
}


function preLoadInitialize()
{
}


function postLoadInitialize()
{
	// If the status block is on, turn it off after 15 seconds
	if ( statusBlockEnabled == true ) {
		setTimeout( "displayStatusBlock( 'off' )", 15000 ) ;
	}
}