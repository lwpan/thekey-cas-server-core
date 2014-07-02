package org.ccci.gto.cas.web.servlet.view;

import static org.ccci.gto.cas.Constants.VIEW_BASENAME_THEKEY;
import static org.ccci.gto.cas.Constants.VIEW_BASENAME_THEKEY_V2;
import static org.ccci.gto.cas.Constants.VIEW_BASENAME_THEKEY_V4;

import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.web.support.ArgumentExtractor;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TheKeyViewResolver extends WebApplicationObjectSupport implements ViewResolver, Ordered {
    private static final Logger LOG = LoggerFactory.getLogger(TheKeyViewResolver.class);

    private static final List<String> supportedViews = Collections.unmodifiableList(Arrays.asList
            (VIEW_BASENAME_THEKEY_V2, VIEW_BASENAME_THEKEY_V4, VIEW_BASENAME_THEKEY));

    private int order = LOWEST_PRECEDENCE;

    @NotNull
    private ServicesManager servicesManager;

    @NotNull
    private List<ArgumentExtractor> argumentExtractors;

    private final ViewResolver[] resolvers = new ViewResolver[supportedViews.size()];

    public void setServicesManager(final ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    public void setArgumentExtractors(final List<ArgumentExtractor> argumentExtractors) {
        this.argumentExtractors = argumentExtractors;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public View resolveViewName(final String viewName, final Locale locale) throws Exception {
        try {
            // find the RegisteredService for the current request
            final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes()).getRequest();
            final Service service = WebUtils.getService(this.argumentExtractors, request);
            final RegisteredService rService = this.servicesManager.findServiceBy(service);
            if (rService instanceof TheKeyRegisteredService) {
                final String basename = ((TheKeyRegisteredService) rService).getViewName();
                final int index = supportedViews.indexOf(basename);
                if (index >= 0 && index < supportedViews.size()) {
                    LOG.debug("using custom view basename {} for {}", basename, rService);
                    return this.getViewResolver(index).resolveViewName(viewName, locale);
                }
            }
        } catch (final Exception e) {
            LOG.debug("error resolving legacy The Key views", e);
        }

        // default to not resolving any view
        return null;
    }

    private ViewResolver getViewResolver(final int index) {
        // create and cache the actual view resolver
        if (resolvers[index] == null) {
            loadViewResolver(index);
        }

        return resolvers[index];
    }

    private void loadViewResolver(final int index) {
        final ResourceBundleViewResolver resolver = new ResourceBundleViewResolver();
        resolver.setApplicationContext(getApplicationContext());
        resolver.setServletContext(getServletContext());
        resolver.setBasenames(supportedViews.subList(index, supportedViews.size()).toArray(new String[supportedViews
                .size() - index]));
        resolvers[index] = resolver;
    }
}
