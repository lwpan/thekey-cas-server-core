package org.ccci.gto.cas.service.audit;

import org.apache.commons.beanutils.BeanUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.AuditService;
import org.ccci.gto.cas.model.Audit;
import org.ccci.gto.cas.model.Auditable;
import org.ccci.gto.cas.persist.AuditDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;

@Deprecated
public class AuditServiceImpl implements AuditService {
    private static final Logger LOG = LoggerFactory.getLogger(AuditServiceImpl.class);

    @NotNull
    private AuditDao auditDao;

    /**
     * @param dao
     *            the auditDao to set
     */
    public void setAuditDao(final AuditDao dao) {
        this.auditDao = dao;
    }

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
     *      Auditable)
     */
    @Override
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

        LOG.debug("Creating new Audit: {}", audit);

        this.auditDao.save(audit);

        LOG.debug("Audit saved");
    }

    /**
     * Audit an object that was updated.
     * 
     * @param source
     *            Source that lead to the audit.
     * @param changedBy
     *            Who made change that lead to audit.
     * @param userId
     *            Userid of person affected by change.
     * @param description
     *            Description of change.
     * @param original
     *            Original version of object before change was made.
     * @param current
     *            Current version of object with new changes to it.
     * @throws AuditException
     */
    @Override
    @Transactional(readOnly = false)
    public void update(final String source, final String changedBy,
	    final String userId, final String description,
	    final Auditable original, final Auditable current)
	    throws AuditException {
	Assert.notNull(original);
	Assert.notNull(current);
	Assert.isTrue(original.getClass().equals(current.getClass()),
		"current and original objects are not the same class.");

	// common values
	final String className = current.getClass().getSimpleName();
	final Date currentDate = new Date();

	// Check each auditable property for a change
	final ArrayList<Audit> audits = new ArrayList<Audit>();
	for (final String name : original.getAuditProperties()) {
	    try {
            String originalValue = BeanUtils.getProperty(original, name);
            String currentValue = BeanUtils.getProperty(current, name);

            // trim string values to eliminate padding having an effect on comparison
            if (originalValue != null) {
                originalValue = originalValue.trim();
            }
            if (currentValue != null) {
                currentValue = currentValue.trim();
            }

            LOG.debug("***** Compare: Name({}) New({}) -> Original({})", name, currentValue, originalValue);
            if ((originalValue == null && currentValue == null) || (originalValue != null && !originalValue.equals
                    (currentValue))) {
                LOG.debug("***** UPDATED: ModelObject property \"{}\" is being updated", name);

                // generate an audit for the current property
                final Audit audit = new Audit();
                audit.setCreated(currentDate);
                audit.setAction("U");
                audit.setSource(source);
                audit.setChangeDate(currentDate);
                audit.setChangedBy(changedBy);
                audit.setUserId(userId);
                audit.setDescription(description);
                audit.setObjectType(className);
                audit.setProperty(name);
                audit.setValueOld(originalValue != null ? originalValue : "");
                audit.setValueNew(currentValue != null ? currentValue : "");
                audits.add(audit);
            }
        } catch (final Exception e) {
            final String error = "Unable to locate property \"" + name + "\" from ModelObject: " + current;
            LOG.error(error, e);
            throw new AuditException(error, e);
        }
    }

        // save any audits
        LOG.debug("***** SAVING: Saving audit information.");
        this.auditDao.saveAll(audits);
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
    @Override
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

        LOG.debug("Creating new Audit: {}", audit);

        this.auditDao.save(audit);

        LOG.debug("Audit saved");
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
    @Override
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

        LOG.debug("Creating new Audit: {}", audit);

        this.auditDao.save(audit);

        LOG.debug("Audit saved");
    }
}
