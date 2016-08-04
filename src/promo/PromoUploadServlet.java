package promo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
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

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;


@SuppressWarnings("restriction")
public class PromoUploadServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Random random = new Random();
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("NOTHING HERE");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HashMap<String, String> values = new HashMap<>();
		String regiontext = "";
		String audittext = "";

		String saveName = getServletContext().getRealPath("results" + File.separator + "KP_" + random.nextLong() + ".docx");
		String examplePath = getServletContext().getRealPath("config" + File.separator + "DELTA KP.docx");
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
		
		String csvPath = null;
		
		
		try {
			List<FileItem> items = upload.parseRequest(request);
			Iterator<FileItem> iter = items.iterator();
			
			while (iter.hasNext()) {
			    FileItem item = (FileItem) iter.next();
 
			    if (item.isFormField()) {
			    	//если принимаемая часть данных является полем формы
			    	if(item.getFieldName().equals("regiontext")){
						regiontext = item.getString("utf-8");
					}else if(item.getFieldName().equals("audittext")){
						audittext = item.getString("utf-8");
					}else{
						processFormField(item, values);
					}

			    } else {
			    	//в противном случае рассматриваем как файл
			    	csvPath = processUploadedFile(item);
			    }
			}			
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		
		//обработка файла
		PromoCommBuilder cb = new PromoCommBuilder(examplePath, tempXMLpath, getServletContext().getRealPath(File.separator));
		
		int code = cb.assemble(csvPath, saveName, values, regiontext, audittext);
		System.out.println("Assemble is done. Code: " + code);
		
		///отправка файла на скачивание
		File resultFile = new File(saveName);
        String fileType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

        response.setContentType(fileType);
        
        String filename = "Дельта - коммерческое предложение по продвижению сайта.docx";
        filename = MimeUtility.encodeText(filename, "utf-8", "B");
        File csvfile = new File(csvPath);
        
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
        
        csvfile.delete();
        resultFile.delete();
        
	}

	private String processUploadedFile(FileItem item) throws Exception {
		File uploadedFile = null;
		String path;
		do{
			path = getServletContext().getRealPath("/upload/" + random.nextInt() + item.getName());					
			uploadedFile = new File(path);		
		}while(uploadedFile.exists());
		uploadedFile.createNewFile();
		item.write(uploadedFile);
		return path;
	}
	
	private void processFormField(FileItem item, HashMap<String, String> values ) throws UnsupportedEncodingException {
		String fieldName = item.getFieldName();
		String fieldValue = item.getString("utf-8");
		values.put("placeholder" + fieldName, fieldValue);
	}
}
