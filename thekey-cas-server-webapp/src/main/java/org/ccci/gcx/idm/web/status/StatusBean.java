package org.ccci.gcx.idm.web.status;

/**
 * Provides a bean to contain a status for something.  (hows that for specific)
 * @author ken
 *
 */
public class StatusBean 
{
	private String title;
	private String description;
	private String value;
	
	public StatusBean(String a_title, String a_desc, String a_value)
	{
		title = a_title; description = a_desc;
		value = a_value;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	

}
