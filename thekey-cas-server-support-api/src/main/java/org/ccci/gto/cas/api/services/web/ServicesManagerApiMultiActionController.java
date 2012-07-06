package org.ccci.gto.cas.api.services.web;

import static org.ccci.gto.cas.api.Constants.KEYLENGTH;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.services.TheKeyRegisteredServiceImpl;
import org.ccci.gto.cas.util.RandomKeyGenerator;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

public class ServicesManagerApiMultiActionController extends MultiActionController {
    /** Instance of ServicesManager. */
    @NotNull
    private final ServicesManager servicesManager;

    public ServicesManagerApiMultiActionController(final ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    public ModelAndView generateApiKey(final HttpServletRequest request, final HttpServletResponse response) {
        // find the specified registered service
        final long id = Long.parseLong(request.getParameter("id"));
        final RegisteredService r = this.servicesManager.findServiceBy(id);

        final ModelAndView modelAndView = new ModelAndView(new MappingJacksonJsonView());
        if (r == null) {
            modelAndView.addObject("error", "invalid service");
        } else if (r instanceof TheKeyRegisteredServiceImpl) {
            // generate a new key and return the new key
            final TheKeyRegisteredServiceImpl service = (TheKeyRegisteredServiceImpl) r;
            service.setApiKey(RandomKeyGenerator.generateUriSafeKey(KEYLENGTH));
            this.servicesManager.save(service);
            modelAndView.addObject("apiKey", service.getApiKey());
        } else {
            modelAndView.addObject("error", "unsupported");
        }

        return modelAndView;
    }
}
