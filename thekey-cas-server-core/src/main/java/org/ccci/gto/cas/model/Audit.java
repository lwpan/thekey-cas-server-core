package org.ccci.gto.cas.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Integer version;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    private String source;

    private String changedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date changeDate;

    private String userId;

    private String objectType;

    private String action;

    private String description;

    private String property;

    private String valueOld;

    private String valueNew;

    /**
     * @return the id
     */
    public Long getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final Long id) {
	this.id = id;
    }

    /**
     * @return the version
     */
    public Integer getVersion() {
	return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(final Integer version) {
	this.version = version;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
	return created;
    }

    /**
     * @param created
     *            the created to set
     */
    public void setCreated(final Date created) {
	this.created = created;
    }

    /**
     * @return the source
     */
    public String getSource() {
	return source;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSource(final String source) {
	this.source = source;
    }

    /**
     * @return the changedBy
     */
    public String getChangedBy() {
	return changedBy;
    }

    /**
     * @param changedBy
     *            the changedBy to set
     */
    public void setChangedBy(final String changedBy) {
	this.changedBy = changedBy;
    }

    /**
     * @return the changeDate
     */
    public Date getChangeDate() {
	return changeDate;
    }

    /**
     * @param changeDate
     *            the changeDate to set
     */
    public void setChangeDate(final Date changeDate) {
	this.changeDate = changeDate;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
	return userId;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(final String userId) {
	this.userId = userId;
    }

    /**
     * @return the objectType
     */
    public String getObjectType() {
	return objectType;
    }

    /**
     * @param objectType
     *            the objectType to set
     */
    public void setObjectType(final String objectType) {
	this.objectType = objectType;
    }

    /**
     * @return the action
     */
    public String getAction() {
	return action;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(final String action) {
	this.action = action;
    }

    /**
     * @return the description
     */
    public String getDescription() {
	return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
	this.description = description;
    }

    /**
     * @return the property
     */
    public String getProperty() {
	return property;
    }

    /**
     * @param property
     *            the property to set
     */
    public void setProperty(final String property) {
	this.property = property;
    }

    /**
     * @return the valueOld
     */
    public String getValueOld() {
	return valueOld;
    }

    /**
     * @param valueOld
     *            the valueOld to set
     */
    public void setValueOld(final String valueOld) {
	this.valueOld = valueOld;
    }

    /**
     * @return the valueNew
     */
    public String getValueNew() {
	return valueNew;
    }

    /**
     * @param valueNew
     *            the valueNew to set
     */
    public void setValueNew(final String valueNew) {
	this.valueNew = valueNew;
    }
}
