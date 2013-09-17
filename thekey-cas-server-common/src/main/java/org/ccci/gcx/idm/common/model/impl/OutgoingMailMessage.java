package org.ccci.gcx.idm.common.model.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <b>OutgoingMailMessage</b> represents a single, outgoing e-mail message
 * transmission record.
 */
public class OutgoingMailMessage implements Serializable {
    private static final long serialVersionUID = 8238135832490268560L;

    private String from = null;
    private String to = null;

    private final Map<String, Object> model = new HashMap<String, Object>();

    public final Map<String, Object> getModel() {
        return this.model;
    }

    public final void setModel(final Map<String, Object> model) {
        this.model.clear();
        this.model.putAll(model);
    }

    public final Object addToModel(final String key, final Object value) {
        return this.model.put(key, value);
    }

    /**
     * @return the replyTo
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * @param from
     *            the replyTo to set
     */
    public void setFrom(final String from) {
        this.from = from;
    }

    /**
     * @return the to
     */
    public String getTo() {
        return this.to;
    }

    /**
     * @param to
     *            the to to set
     */
    public void setTo(final String to) {
        this.to = to;
    }
}
