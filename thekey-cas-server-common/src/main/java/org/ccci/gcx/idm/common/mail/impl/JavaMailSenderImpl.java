package org.ccci.gcx.idm.common.mail.impl;

import org.ccci.gcx.idm.common.mail.JavaMailSender;

/**
 * <b>JavaMailSenderImpl</b> extends the Spring implementation. This was done so that 
 * the host name could be set from within a calling class. The host value could not be 
 * injected through the context.
 *
 * @author Greg Crider  December 1, 2008 6:27:19 PM
 */
public class JavaMailSenderImpl extends org.springframework.mail.javamail.JavaMailSenderImpl implements JavaMailSender
{

}
