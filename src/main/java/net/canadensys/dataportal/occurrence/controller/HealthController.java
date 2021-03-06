package net.canadensys.dataportal.occurrence.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import net.canadensys.dataportal.occurrence.config.OccurrencePortalConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to check the status of the web application. This controller will answer to HEAD and GET queries.
 * This is often used by external service like UptimeRobot.
 * @author canadensys
 *
 */
@Controller
public class HealthController {
	
	@Autowired
	@Qualifier("occurrencePortalConfig")
	private OccurrencePortalConfig appConfig;
	 
	/**
	 * Response to HEAD request with a HTTP_OK and no content, as defined by the standard.
	 * @param response
	 */
    @RequestMapping(value="/status", method=RequestMethod.HEAD)
    public void healthCheckHead(HttpServletResponse response) {
        response.setContentLength(0);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    /**
     * Response to GET request with a HTTP_OK and the string OK.
     * @param response
     */
    @RequestMapping(value="/status", method=RequestMethod.GET)
    public void healthCheckGet(HttpServletResponse response) {
        try {
			response.getWriter().println("OK");
			response.getWriter().println("Version : " + appConfig.getCurrentVersion());
			response.getWriter().close();
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
