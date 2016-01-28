package com.ryce.frugalist;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.Email;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.ryce.frugalist.model.User;

@SuppressWarnings("serial")
public class FrugaListServlet extends HttpServlet {
	
	// static gson instance (note: it is thread-safe!)
	private static final Gson GSON = new Gson();
	
	
	
	@Override
	/**
	 * Handle routing to services
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		String[] pathParts = req.getPathInfo().split("/");
		
		if (pathParts == null || pathParts.length < 2 || 
			!pathParts[0].equalsIgnoreCase("get")) 
		{
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		// route get request
		if (pathParts[1].equalsIgnoreCase("user")) {
			
			List<User> users = ObjectifyService.ofy()
					.load().type(User.class).list();
			
			writeJsonResponse(resp, users);
			
		} else if (pathParts[1].equalsIgnoreCase("deal")) {
			
		} else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		String[] pathParts = req.getPathInfo().split("/");
		
		if (pathParts == null || pathParts.length < 2 ||
			!pathParts[0].equalsIgnoreCase("post")) 
		{
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		// route post request
		if (pathParts[1].equalsIgnoreCase("user")) {
			
			final String idStr = req.getParameter("id");
			final String emailStr = req.getParameter("email");
			
			if (idStr == null || emailStr == null) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid/missing parameter(s)");
				return;
			}
			
			Email email = new Email(emailStr);
			
			User user = new User(idStr, email);
			
			Key<User> userKey = ObjectifyService.ofy().save().entity(user).now();
			writeJsonResponse(resp, userKey);
			
		} else if (pathParts[1].equalsIgnoreCase("deal")) {
			
		} else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPut(req, resp);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doDelete(req, resp);
	}

	
	
	/**
	 * Convenience method to handle writing a JSON response
	 * @param resp
	 * @param respObject
	 * @throws IOException
	 */
	private static void writeJsonResponse(HttpServletResponse resp, Object respObject) 
			throws IOException {
		String jsonString = GSON.toJson(respObject);
		resp.setContentType("application/json");
		resp.getWriter().print(jsonString);
	}
	
}
