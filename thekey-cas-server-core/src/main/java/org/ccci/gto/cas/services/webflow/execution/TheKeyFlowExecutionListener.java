package org.ccci.gto.cas.services.webflow.execution;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.services.web.ViewContext;
import org.ccci.gto.cas.services.web.ViewContextFactory;
import org.ccci.gto.cas.services.web.ViewPopulator;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;

public class TheKeyFlowExecutionListener extends FlowExecutionListenerAdapter {
    @NotNull
    private ViewContextFactory viewFactory;

    @NotNull
    private List<ViewPopulator> populators;

    /**
     * @param populators
     *            the ViewPopulators to use for this ThemeResolver
     */
    public void setPopulators(final List<ViewPopulator> populators) {
	this.populators = populators;
    }

    /**
     * @param factory
     *            the ViewContextFactory to use
     */
    public void setViewContextFactory(final ViewContextFactory factory) {
	this.viewFactory = factory;
    }

    private void populateContext(final RequestContext context) {
        final ViewContext viewContext = viewFactory.getViewContext(context);

        // process all the ViewPopulators
        if (this.populators != null) {
            for (final ViewPopulator populator : this.populators) {
                populator.populate(viewContext);
            }
        }
    }

    @Override
    public void requestSubmitted(final RequestContext context) {
        this.populateContext(context);
    }

    @Override
    public void viewRendering(final RequestContext context, final View view, final StateDefinition viewState) {
        this.populateContext(context);
    }
}
