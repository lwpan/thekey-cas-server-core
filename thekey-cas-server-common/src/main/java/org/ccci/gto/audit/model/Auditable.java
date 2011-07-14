package org.ccci.gto.audit.model;

public interface Auditable {
    /**
     * All properties that should be audited.
     * 
     * @return an array of property names.
     */
    public String[] getAuditProperties();
}
