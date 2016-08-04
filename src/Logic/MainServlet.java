package Logic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pojo.Region;

//@WebServlet("/")
public class MainServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4106855189225345261L;

	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HashMap<String, Region> regionList = Region.loadRegions(
				getServletContext().getRealPath(File.separator + "config" + File.separator + "regions"));
		
		req.setAttribute("regionList", regionList);
		getServletContext().getRequestDispatcher("/Form.jsp").forward(req, resp);
    }
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}


}
