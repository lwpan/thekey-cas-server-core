package org.ccci.gto.cas.oauth.web;

import static org.ccci.gto.cas.oauth.Constants.FLOW_ATTR_REDIRECT_URI;

import java.net.URI;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

public class OAuthRedirectView extends AbstractView {
    @Override
    protected void renderMergedOutputModel(final Map<String, Object> model, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        response.sendRedirect(((URI) model.get(FLOW_ATTR_REDIRECT_URI)).toString());
    }
}
