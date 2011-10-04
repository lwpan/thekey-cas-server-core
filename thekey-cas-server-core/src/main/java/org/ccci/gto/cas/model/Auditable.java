package org.ccci.gto.cas.model;

public interface Auditable {
    /**
     * All properties that should be audited.
     * 
     * @return an array of property names.
     */
    public String[] getAuditProperties();
}
