package org.ccci.gto.mail.impl;

import org.apache.velocity.app.VelocityEngine;
import org.ccci.gto.mail.TemplateMessagePreparator;

public class VelocityTemplateMailSender extends AbstractTemplateMailSender {
    /** Velocity engine */
    private VelocityEngine velocityEngine;

    protected TemplateMessagePreparator getTemplatePreparator() {
	VelocityTemplateMessagePreparator preparator = new VelocityTemplateMessagePreparator();
	preparator.setVelocityEngine(this.getVelocityEngine());
	return preparator;
    }

    /**
     * @param velocityEngine the velocityEngine to set
     */
    public void setVelocityEngine(VelocityEngine velocityEngine) {
	this.velocityEngine = velocityEngine;
    }

    /**
     * @return the velocityEngine
     */
    public VelocityEngine getVelocityEngine() {
	return velocityEngine;
    }
}
