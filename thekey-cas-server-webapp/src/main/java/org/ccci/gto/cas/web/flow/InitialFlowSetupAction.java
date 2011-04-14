package org.ccci.gto.cas.web.flow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.web.LanguageListBean;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * @author Daniel Frett
 */
public class InitialFlowSetupAction extends AbstractAction {
    protected static final Log log = LogFactory
	    .getLog(InitialFlowSetupAction.class);
    private LanguageListBean languages;

    @Override
    protected Event doExecute(RequestContext context) throws Exception {
	context.getFlowScope().put("languagelist",
		this.languages.getLanguageList());
	return result("success");
    }

    /**
     * @return the languages
     */
    public LanguageListBean getLanguages() {
	return this.languages;
    }

    /**
     * @param languages
     *            the languages to set
     */
    public void setLanguages(LanguageListBean languages) {
	this.languages = languages;
    }
}
