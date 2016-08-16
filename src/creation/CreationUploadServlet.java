package creation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;


@SuppressWarnings("restriction")
public class CreationUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Random random = new Random();
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("NOTHING HERE");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HashMap<String, String> values = new HashMap<>();
		
		ArrayList<String> moduleNames = new ArrayList<>();
		ArrayList<String> modulePrices = new ArrayList<>();

		String saveName = getServletContext().getRealPath("results" + File.separator + "KPCREAT_" + random.nextLong() + ".docx");
		String examplePath = getServletContext().getRealPath("config" + File.separator + "KP_CREATION.docx");
		String tempXMLpath = getServletContext().getRealPath("config" + File.separator + "document.xml");
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
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
 
			    //если принимаемая часть данных является полем формы
			    if (item.isFormField()) {
			    	if(item.getFieldName().equals("modulename")){
			    		moduleNames.add(item.getString("utf-8"));
			    	}else if(item.getFieldName().equals("moduleprice")){
			    		modulePrices.add(item.getString("utf-8"));
			    	}else{
			    		processFormField(item, values);
			    	}

			    }
			}			
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		
		//обработка файла
		CreationCommBuilder cb = new CreationCommBuilder(examplePath, tempXMLpath, getServletContext().getRealPath(File.separator));
		
		int code = cb.assemble(saveName, values, moduleNames, modulePrices);
		System.out.println("Assemble is done. Code: " + code);
		
		///отправка файла на скачивание
		File resultFile = new File(saveName);
        String fileType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

        response.setContentType(fileType);
        
        String filename = "Дельта - коммерческое предложение по созданию сайта.docx";
        filename = MimeUtility.encodeText(filename, "utf-8", "B");
        
        response.setHeader("Content-disposition","attachment; "
        		+ "filename=" + filename);

        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(resultFile);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0){
           out.write(buffer, 0, length);
        }
        in.close();
        out.flush();
        
        resultFile.delete();
        
	}
	
	private void processFormField(FileItem item, HashMap<String, String> values ) throws UnsupportedEncodingException {
		String fieldName = "pholder" + item.getFieldName();
		String fieldValue = item.getString("utf-8");
		values.put(fieldName, fieldValue);
	}
	
}
