package creation;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.xml.bind.JAXBException;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.P.Hyperlink;

import Logic.ZipReplacer;
import promo.PromoDOCXworker;

import org.docx4j.wml.Text;


public class CreationCommBuilder {
	
	private String templatePath = "";
	private String tempXMLPath = "";
	private String root;
	
	
	public CreationCommBuilder(String templatePath, String tempXMLPath, String root){
		this.templatePath = templatePath;
		this.tempXMLPath = tempXMLPath;
		this.root = root;
	}
	
	public int assemble(String resultPath, HashMap<String, String> values, ArrayList<String> moduleNames, ArrayList<String> modulePrices) {
		long startTime = System.currentTimeMillis();
		
		int summ = parsePrice(values, modulePrices);
		String ssumm = String.valueOf(summ);
		
		values.put("pholdersumm", ssumm.substring(0, ssumm.length()-3) + " " + ssumm.substring(ssumm.length()-3));
		
		String picFolder = root + File.separator + "config" + File.separator + "regions" + File.separator + "pic" + File.separator;
		
		//копируем шаблон
		Path tempPath = Paths.get(templatePath);
		Path resultikPath = Paths.get(resultPath);
	    try {
			Files.copy(tempPath, resultikPath, REPLACE_EXISTING);
		} catch (IOException e3) {
			e3.printStackTrace();
			return 1;
		}
	    
	    //меняем фотку менеджера
	    ZipReplacer.zipFileReplace(resultPath, "word/media/image8.jpeg", picFolder + values.get("pholdermanagernick") + ".jpeg");
	    values.remove("pholdermanagernick");
	    
		//делаем переменные подстановки понятными для docx4j
	    Random r = new Random(System.currentTimeMillis());
	    int randforname = r.nextInt();
	    String docxmlname = tempXMLPath.replace("document.xml", "" + randforname);
	    File docxmlfile = new File(docxmlname);
		ZipReplacer.getFileFromZip(resultPath, "word/document.xml", docxmlname); //вытскиваем document.xml из docx
		try {
			ZipReplacer.regexpReplacer(docxmlname, "<w:t>(pholder.*?)</w:t>", "<w:t>\\$\\{$1\\}</w:t>");
		} catch (IOException e2) {
			e2.printStackTrace();
			return 1;
		}
		ZipReplacer.zipFileReplace(resultPath, "word/document.xml", docxmlname);
		docxmlfile.delete();
		
		//ссылки
		randforname = r.nextInt();
	    String relsname = tempXMLPath.replace("document.xml", "" + randforname);
	    File relsfile = new File(relsname);
	    
		ZipReplacer.getFileFromZip(resultPath, "word/_rels/document.xml.rels", relsname); //достаем document.xml.rels из docx
		try {
			ZipReplacer.regexpReplacer(relsname, "//дельта-юг.рф", "//" + values.get("pholderdeltasite").replace("www.", ""));
		} catch (IOException e2) {
			e2.printStackTrace();
			return 1;
		}
		ZipReplacer.zipFileReplace(resultPath, "word/_rels/document.xml.rels", relsname);
		relsfile.delete();
		

		
		//открываем для редактирования
		CreationDOCXworker dw = null;
		try {
			dw = new CreationDOCXworker(resultPath, resultPath);
		} catch (Docx4JException e) {
			LOG("DOCXWorker - Docx4JException");
			e.printStackTrace();
			return 1;
		} catch (JAXBException e) {
			LOG("DOCXWorker - JAXBException");
			e.printStackTrace();
			return 1;
		}

		
	    //почта со ссылкйо внизу
	    List<Object> hls = PromoDOCXworker.getAllElementFromObject(dw.getMainDocPart(), Hyperlink.class);
	    Relationship rel = dw.getMainDocPart().getRelationshipsPart().getRelationshipByID(((Hyperlink) hls.get(1)).getId());
	    rel.setTarget("mailto:" + values.get("pholdermanageremail"));

	    //футер
	    List<Object> footerContent = dw.getFooterPart().getContent();
	    Text t = (Text) PromoDOCXworker.getAllElementFromObject(footerContent.get(2), Text.class).get(0);
	    t.setValue(values.get("pholderdeltaaddress"));
	    t = (Text) PromoDOCXworker.getAllElementFromObject(footerContent.get(3), Text.class).get(0);
	    t.setValue(values.get("pholderdeltaphonenumbers"));
	    t = (Text) PromoDOCXworker.getAllElementFromObject(footerContent.get(4), Text.class).get(0);
	    t.setValue(values.get("pholderdeltasite"));
	    //заменяем переменные
		try {
			dw.getMainDocPart().variableReplace(values);
		} catch (Exception e) {
			dw.writeDocx();
			e.printStackTrace();
			return 1;
		}
		
		Iterator<String> iNam = moduleNames.iterator();
		Iterator<String>  iPri = modulePrices.iterator();
		while(iNam.hasNext()){
			System.out.println(iNam.next() + ":" + iPri.next());
		}
		
		dw.addModules(moduleNames, modulePrices);
		
		
		//сохраняем
		dw.writeDocx();
		System.out.println("Готово; " + (System.currentTimeMillis() - startTime) + " мс.");
		return 0;
		
	}
	
	private int parsePrice(HashMap<String, String> values, ArrayList<String> modulePrices){
		int result = 0;
		for(Entry<String, String> ent : values.entrySet()){
			int t;
			try{
				t = Integer.valueOf(ent.getValue().replace(" ", ""));
				System.out.println(ent.getKey() + ":" + ent.getValue() + ":" + t);
			}catch(NumberFormatException e){
				t = 0;
				System.out.println(ent.getKey() + ": NOPE");
			}
			result += t;
		}
		
		for(String s : modulePrices){
			int t;
			try{
				t = Integer.valueOf(s.replace(" ", ""));
			}catch(NumberFormatException e){
				t = 0;
			}
			result += t;
		}
		
		return result;
	}
	
	private static void LOG(String message){
		System.out.println("----------\n" + message + "\n----------");
	}
}
