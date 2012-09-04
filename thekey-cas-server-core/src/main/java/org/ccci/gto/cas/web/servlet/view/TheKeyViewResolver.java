package org.ccci.gto.cas.web.servlet.view;

import static org.ccci.gto.cas.Constants.VIEW_BASENAME_THEKEY;
import static org.ccci.gto.cas.Constants.VIEW_BASENAME_THEKEY_V2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

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

public class TheKeyViewResolver extends WebApplicationObjectSupport implements ViewResolver, Ordered {
    private final static Logger LOG = LoggerFactory.getLogger(TheKeyViewResolver.class);

    private final static Set<String> supportedViews = new HashSet<String>();
    static {
        supportedViews.add(VIEW_BASENAME_THEKEY);
        supportedViews.add(VIEW_BASENAME_THEKEY_V2);
    }

    private int order = LOWEST_PRECEDENCE;

    @NotNull
    private ServicesManager servicesManager;

    @NotNull
    private List<ArgumentExtractor> argumentExtractors;

    private final Map<String, ViewResolver> resolvers = new HashMap<String, ViewResolver>();

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
                if (basename != null) {
                    LOG.debug("found custom view basename {} for {}", basename, rService);
                    return this.getViewResolver(basename).resolveViewName(viewName, locale);
                }
            }
        } catch (final Exception e) {
            LOG.debug("error resolving legacy The Key views", e);
        }

        // default to not resolving any view
        return null;
    }

    private ViewResolver getViewResolver(final String basename) {
        // create and cache the actual view resolver
        if (!resolvers.containsKey(basename) && supportedViews.contains(basename)) {
            loadViewResolver(basename);
        }

        return resolvers.get(basename);
    }

    private void loadViewResolver(final String basename) {
        final ResourceBundleViewResolver resolver = new ResourceBundleViewResolver();
        resolver.setApplicationContext(getApplicationContext());
        resolver.setServletContext(getServletContext());
        resolver.setBasename(basename);
        resolvers.put(basename, resolver);
    }
}
