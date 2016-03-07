package com.ryce.frugalist;

import com.googlecode.objectify.ObjectifyService;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.model.Freebie;
import com.ryce.frugalist.model.User;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

/**
 * OfyHelper, a ServletContextListener, is setup in web.xml to run before a Servlet is run.  
 * This is required to initialize entities before any transactions are run.
 **/
public class OfyHelper implements ServletContextListener {

	public void contextInitialized(ServletContextEvent event) {
		// This will be invoked as part of a warmup request, or the first user request if no warmup
		// request.
		
		// Register Ofy entities here!
		ObjectifyService.register(User.class);
		ObjectifyService.register(Deal.class);
		ObjectifyService.register(Freebie.class);
	}

	public void contextDestroyed(ServletContextEvent event) {
		// App Engine does not currently invoke this method.
	}

}
