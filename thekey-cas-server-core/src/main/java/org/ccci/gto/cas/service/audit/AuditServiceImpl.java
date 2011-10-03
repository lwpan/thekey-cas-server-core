package org.ccci.gto.cas.service.audit;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.common.IdmException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.impl.AbstractAuditService;
import org.ccci.gto.cas.model.Audit;
import org.ccci.gto.cas.model.Auditable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class AuditServiceImpl extends AbstractAuditService {

    /**
     * Create a new audit based on the specified, generic information.
     * 
     * @param a_Source
     *            Source that lead to the audit.
     * @param a_ChangedBy
     *            Who made change that lead to audit.
     * @param a_Userid
     *            Userid of person affected by change.
     * @param a_Description
     *            Description of change.
     * @param a_Entity
     *            New entity that is being audited.
     * 
     * @see org.ccci.gcx.idm.core.service.AuditService#create(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.Object)
     */
    @Transactional
    public void create(String a_Source, String a_ChangedBy, String a_Userid,
	    String a_Description, Auditable a_Entity) {
	Audit audit = new Audit();

	Date currentDate = new Date();

	String objectType = a_Entity.getClass().getName()
		.substring(a_Entity.getClass().getName().lastIndexOf(".") + 1);

	audit.setCreated(currentDate);
	audit.setAction("C");
	audit.setSource(a_Source);
	audit.setChangeDate(currentDate);
	audit.setChangedBy(a_ChangedBy);
	audit.setUserId(a_Userid);
	audit.setDescription(a_Description);
	audit.setObjectType(objectType);

	if (log.isDebugEnabled()) {
	    log.debug("Creating new Audit: " + audit);
	}

	this.getAuditDao().save(audit);

	log.debug("Audit saved");
    }

    /**
     * Audit an object that was updated.
     * 
     * @param a_Source
     *            Source that lead to the audit.
     * @param a_ChangedBy
     *            Who made change that lead to audit.
     * @param a_Userid
     *            Userid of person affected by change.
     * @param a_Description
     *            Description of change.
     * @param a_Original
     *            Original version of object before change was made.
     * @param a_Current
     *            Current version of object with new changes to it.
     */
    @Transactional
    public void update(String a_Source, String a_ChangedBy, String a_Userid,
	    String a_Description, Auditable a_Original, Auditable a_Current) {
	Assert.notNull(a_Original);
	Assert.notNull(a_Current);
	Assert.isAssignable(a_Original.getClass(), a_Current.getClass(),
		"Current object's class is not assignable from the original class.");

	ArrayList<Audit> audits = new ArrayList<Audit>();

	Date currentDate = new Date();

	String objectType = a_Current.getClass().getName()
		.substring(a_Current.getClass().getName().lastIndexOf(".") + 1);

	// Check each auditable property for a change
	for (int i = 0; (a_Original.getAuditProperties() != null)
		&& (i < a_Original.getAuditProperties().length); i++) {
	    String name = a_Original.getAuditProperties()[i];
	    Object originalValue = null;
	    Object currentValue = null;
	    boolean wasChanged = false;
	    try {
		originalValue = BeanUtils.getProperty(a_Original, name);
		currentValue = BeanUtils.getProperty(a_Current, name);
		PropertyDescriptor descriptor = PropertyUtils
			.getPropertyDescriptor(a_Current, name);
		// For String objects, make sure the change is necessary and not
		// based on padding
		// Do String based comparison to pick up on null/whitespace type
		// distinctions
		if (String.class.isAssignableFrom(descriptor.getPropertyType())) {
		    originalValue = StringUtils.trim((String) originalValue);
		    currentValue = StringUtils.trim((String) currentValue);
		    wasChanged = (((!StringUtils
			    .isBlank((String) originalValue)) && (!originalValue
			    .equals(currentValue))) || ((StringUtils
			    .isBlank((String) originalValue)) && (!StringUtils
			    .isBlank((String) currentValue))));
		    // For all other object types, use basic comparators
		} else {
		    wasChanged = (((originalValue != null) && (!originalValue
			    .equals(currentValue))) || ((originalValue == null) && (currentValue != null)));
		}
	    } catch (Exception e) {
		String error = "Unable to locate property \"" + name
			+ "\" from ModelObject: " + a_Current;
		log.error(error, e);
		throw new IdmException(error, e);
	    }
	    if (log.isDebugEnabled()) {
		log.debug("***** Compare: Name(" + name + ") New("
			+ currentValue + ") -> Original(" + originalValue
			+ ") = Changed(" + wasChanged + ")");
	    }
	    if (wasChanged) {
		if (log.isDebugEnabled()) {
		    log.debug("***** UPDATED: ModelObject property \"" + name
			    + "\" is being updated");
		}
		String originalValueStr = (originalValue == null) ? ""
			: originalValue.toString();
		String currentValueStr = (currentValue == null) ? ""
			: currentValue.toString();
		Audit audit = new Audit();
		audit.setCreated(currentDate);
		audit.setAction("U");
		audit.setSource(a_Source);
		audit.setChangeDate(currentDate);
		audit.setChangedBy(a_ChangedBy);
		audit.setUserId(a_Userid);
		audit.setDescription(a_Description);
		audit.setObjectType(objectType);
		audit.setProperty(name);
		audit.setValueOld(originalValueStr);
		audit.setValueNew(currentValueStr);
		audits.add(audit);
	    }
	}

	// If there was any audit information found, persist it.
	if (audits.size() > 0) {
	    /* = DEBUG = */if (log.isDebugEnabled())
		log.debug("***** SAVING: Saving audit information.");
	    this.getAuditDao().saveAll(audits);
	}
    }

    /**
     * Audit an individual property, by name, that was changed.
     * 
     * @param a_Source
     *            Source that lead to the audit.
     * @param a_ChangedBy
     *            Who made change that lead to audit.
     * @param a_Userid
     *            Userid of person affected by change.
     * @param a_Description
     *            Description of change.
     * @param a_Entity
     *            Updated entity that is being audited.
     * @param a_PropertyName
     *            Name of property on entity that was updated.
     */
    @Transactional
    public void updateProperty(String a_Source, String a_ChangedBy,
	    String a_Userid, String a_Description, Auditable a_Entity,
	    String a_PropertyName) {
	Audit audit = new Audit();

	Date currentDate = new Date();

	String objectType = a_Entity.getClass().getName()
		.substring(a_Entity.getClass().getName().lastIndexOf(".") + 1);

	audit.setCreated(currentDate);
	audit.setAction("U");
	audit.setSource(a_Source);
	audit.setChangeDate(currentDate);
	audit.setChangedBy(a_ChangedBy);
	audit.setUserId(a_Userid);
	audit.setDescription(a_Description);
	audit.setObjectType(objectType);
	audit.setProperty(a_PropertyName);

	if (log.isDebugEnabled()) {
	    log.debug("Creating new Audit: " + audit);
	}

	this.getAuditDao().save(audit);

	log.debug("Audit saved");
    }

    /**
     * Audit a user being merged into another user.
     * 
     * @param a_Source
     *            Source that lead to the audit.
     * @param a_ChangedBy
     *            Who made change that lead to audit.
     * @param a_PrimaryUser
     *            Primary {@link GcxUser}.
     * @param a_UserBeingMerged
     *            {@link GcxUser} being merged into primary.
     * @param a_Description
     *            Description of change.
     */
    @Transactional
    public void merge(String a_Source, String a_ChangedBy,
	    GcxUser a_PrimaryUser, GcxUser a_UserBeingMerged,
	    String a_Description) {
	Audit audit = new Audit();

	Date currentDate = new Date();

	String objectType = a_PrimaryUser
		.getClass()
		.getName()
		.substring(
			a_PrimaryUser.getClass().getName().lastIndexOf(".") + 1);

	audit.setCreated(currentDate);
	audit.setAction("M");
	audit.setSource(a_Source);
	audit.setChangeDate(currentDate);
	audit.setChangedBy(a_ChangedBy);
	audit.setUserId(a_PrimaryUser.getEmail());
	audit.setDescription(a_Description);
	audit.setObjectType(objectType);
	audit.setProperty(GcxUser.FIELD_GUID);
	audit.setValueNew(a_PrimaryUser.getGUID());
	audit.setValueOld(a_UserBeingMerged.getGUID());

	if (log.isDebugEnabled()) {
	    log.debug("Creating new Audit: " + audit);
	}

	this.getAuditDao().save(audit);

	log.debug("Audit saved");
    }
}
