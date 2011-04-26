package org.ccci.gcx.idm.web;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class IntTestController implements Controller {

	/** Logger for this class and subclasses */
	protected static final Log log = LogFactory.getLog(CssServiceController.class);

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("CHARACTER ENCODING= " + request.getCharacterEncoding());
		String value = request.getParameter("value");
		log.info("VALUE:" + value + "[length=" + (value==null?"no length":""+value.length())+"]");

		HashMap<String,String> myModel = new HashMap<String,String>();
		myModel.put("value", value);

		return new ModelAndView("inttest", "model", myModel);
	}
}
