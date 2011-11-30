package org.ccci.gto.cas.services.webflow.execution;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.services.web.ViewContext;
import org.ccci.gto.cas.services.web.ViewPopulator;
import org.jasig.cas.web.support.ArgumentExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;

public class TheKeyFlowExecutionListener extends FlowExecutionListenerAdapter {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @NotNull
    private List<ArgumentExtractor> argumentExtractors;

    @NotNull
    private List<ViewPopulator> populators;

    public void setArgumentExtractors(
	    final List<ArgumentExtractor> argumentExtractors) {
	this.argumentExtractors = argumentExtractors;
    }

    /**
     * @param populators
     *            the ViewPopulators to use for this ThemeResolver
     */
    public void setPopulators(final List<ViewPopulator> populators) {
	this.populators = populators;
    }

    @Override
    public void viewRendering(final RequestContext context, final View view,
	    final StateDefinition viewState) {
	final ViewContext viewContext = new ViewContext(context,
		argumentExtractors);

	// process all the ViewPopulators
	if (populators != null) {
	    for (final ViewPopulator populator : populators) {
		populator.populate(viewContext);
	    }
	}
    }
}
