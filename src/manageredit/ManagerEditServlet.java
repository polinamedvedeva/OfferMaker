package manageredit;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
	private Random random = new Random();

	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HashMap<String, Region> regionList = Region.loadRegions(
				getServletContext().getRealPath(File.separator + "config" + File.separator + "regions"));

		req.setAttribute("regionList", regionList);
		getServletContext().getRequestDispatcher("/EditForm.jsp").forward(req, resp);
    }
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HashMap<String, String> parameters = new HashMap<>();
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024*1024);
		File tempDir = (File)getServletContext().getAttribute("javax.servlet.context.tempdir");
		factory.setRepository(tempDir);
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(1024 * 1024 * 10);
		upload.setHeaderEncoding("utf-8");
		
		File mngPic = null;
		
		try {
			List<FileItem> items = upload.parseRequest(req);
			Iterator<FileItem> iter = items.iterator();
			
			while (iter.hasNext()) {
			    FileItem item = (FileItem) iter.next();
 
			    if (item.isFormField()) {
			    	//если принимаемая часть данных является полем формы
			    	processFormField(item, parameters);

			    } else {
			    	//в противном случае рассматриваем как файл
			    	mngPic = processUploadedFile(item);
			    }
			}			
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		
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
					File pic = new File(getServletContext().getRealPath(
							"config" + File.separator + "regions" + File.separator
							+ "pic" + File.separator + managernick + ".jpeg"));
					if(pic.exists())
						pic.delete();
				}else{
					if(mngPic != null){
						File destination = new File(mngPic.getParentFile().getAbsolutePath() + File.separator + managernick + ".jpeg");
						if(destination.exists())
							destination.delete();
						mngPic.renameTo(destination);
					}
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
				if(mngPic != null){
					File destination = new File(mngPic.getParentFile().getAbsolutePath() + File.separator + managernick + ".jpeg");
					if(destination.exists())
						destination.delete();
					mngPic.renameTo(destination);
				}
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
	
	private File processUploadedFile(FileItem item) throws Exception {
		File uploadedFile = null;
		String path;
		if(item.getSize() == 0)
			return null;
		
		do{
			path = getServletContext().getRealPath("config" + File.separator + "regions" + File.separator + "pic"
					+ File.separator + random.nextInt());					
			uploadedFile = new File(path);		
		}while(uploadedFile.exists());
		uploadedFile.createNewFile();
		item.write(uploadedFile);
		
		return uploadedFile;
	}
	
	private void processFormField(FileItem item, HashMap<String, String> values ) throws UnsupportedEncodingException {
		String fieldName = item.getFieldName();
		String fieldValue = item.getString("utf-8");
		values.put(fieldName, fieldValue);
	}
}
