package manageredit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import pojo.Manager;
import pojo.Region;

public class ManagerEditServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4106855189225345261L;

	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HashMap<String, Region> regionList = Region.loadRegions(
				getServletContext().getRealPath(File.separator + "config" + File.separator + "regions"));

		req.setAttribute("regionList", regionList);
		getServletContext().getRequestDispatcher("/EditForm.jsp").forward(req, resp);
    }
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HashMap<String, String> parameters = getParameters(req);
		String regionnick = parameters.get("regionnick");
		if(regionnick == null){
			doGet(req, resp);
		}
		
		HashMap<String, Region> regionList = Region.loadRegions(
				getServletContext().getRealPath(File.separator + "config" + File.separator + "regions"));
		
		Region selectedRegion = regionList.get(regionnick);
		if(selectedRegion == null){
			selectedRegion = new Region();
		}else if(parameters.containsKey("deleteRegion")){
			Region.deleteRegion(regionnick, getServletContext().getRealPath("config" + File.separator + "regions"));
			doGet(req, resp);
			return;
		}
		
		selectedRegion.setNick(regionnick);
		selectedRegion.setName(parameters.get("regionname"));
		selectedRegion.setAddress(parameters.get("address"));
		selectedRegion.setPhones(parameters.get("phonenumbers"));
		selectedRegion.setSite(parameters.get("deltasite"));
		
		String managernick = parameters.get("managernick");
		
		if(!managernick.equals("")){
			String name = parameters.get("managername");
			String pos = parameters.get("managerposition");
			String mail = parameters.get("email");
			String phone = parameters.get("phonenumber");
			
			Boolean delete = parameters.containsKey("deleteManager");
			
			List<Manager> managers = selectedRegion.getManagers();
			//ищем, был ли менеджер с таким ником
			Manager existedManager = checkExistance(managers, managernick);
			//если да, то обновляем данные
			if(existedManager != null){
				if(delete){
					managers.remove(existedManager); //удаляем, если стояла галка. иначе обновляем инфу
				}else{
					existedManager.setName(name);
					existedManager.setPosition(pos);
					existedManager.setEmail(mail);
					existedManager.setPhonenumber(phone);
				}
			//иначе добавляем нового
			}else{
				existedManager = new Manager(managernick);
				existedManager.setName(name);
				existedManager.setPosition(pos);
				existedManager.setEmail(mail);
				existedManager.setPhonenumber(phone);
				managers.add(existedManager);
			}
		}
		
		Region.updateRegion(selectedRegion, getServletContext().getRealPath("config" + File.separator + "regions"));
		req.removeAttribute("regionList");
		doGet(req, resp);
	}

	private Manager	checkExistance(List<Manager> managers, String managernick){
		for(Manager m : managers){
			if(m.getNick().equals(managernick))
				return m;
		}
		return null;
	}
	
	private HashMap<String, String> getParameters(HttpServletRequest request){
		HashMap<String, String> values = new HashMap<>();
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			System.out.println(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024*1024);
		File tempDir = (File)getServletContext().getAttribute("javax.servlet.context.tempdir");
		factory.setRepository(tempDir);
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(1024 * 1024 * 10);
		upload.setHeaderEncoding("utf-8");
		
		try {
			List<FileItem> items = upload.parseRequest(request);
			Iterator<FileItem> iter = items.iterator();
			
			while (iter.hasNext()) {
			    FileItem item = (FileItem) iter.next();
 
			    if (item.isFormField()) {
			    	String fieldName = item.getFieldName();
					String fieldValue = item.getString("utf-8");
					values.put(fieldName, fieldValue);
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
		return values;
	}
}
