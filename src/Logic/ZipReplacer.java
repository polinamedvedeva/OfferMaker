package Logic;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipReplacer {

	public static void zipFileReplace(String zipFileString, String fileInsideZipString, String myFileString){
		Path zipFilePath = Paths.get(zipFileString);
		
		Path myFilePath = Paths.get(myFileString);
		
	    try( FileSystem fs = FileSystems.newFileSystem(zipFilePath, null) ){
	        Path fileInsideZipPath = fs.getPath(fileInsideZipString);
	        Files.copy(myFilePath, fileInsideZipPath, REPLACE_EXISTING);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void getFileFromZip(String zipFileString, String fileInsideZipString, String myFileString){
		Path zipFilePath = Paths.get(zipFileString);
				
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(myFileString);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try( FileSystem fs = FileSystems.newFileSystem(zipFilePath, null) ){
	        Path fileInsideZipPath = fs.getPath(fileInsideZipString);
	        Files.copy(fileInsideZipPath, fos);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }finally{
	    	try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	public static void regexpReplacer(String docPath, String regexp, String toInsert) throws IOException{
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(docPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String xmlContent = "";
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader bReader = new BufferedReader(isr);
		String buf = "";
		
		while((buf = bReader.readLine()) != null){			
			xmlContent += buf + "\n";
		};
		
		bReader.close();
		
		xmlContent = xmlContent.replaceAll(regexp, toInsert);
		
		FileOutputStream fos = new FileOutputStream(docPath);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		BufferedWriter bWriter = new BufferedWriter(osw);
		bWriter.write(xmlContent);
		bWriter.flush();
		bWriter.close();
	}
}
