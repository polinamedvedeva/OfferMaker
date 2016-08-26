package promo;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBException;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.P;
import org.docx4j.wml.P.Hyperlink;

import Logic.CSVReader;
import Logic.ZipReplacer;

import org.docx4j.wml.Text;

import pojo.Line;
import pojo.Table;

public class PromoCommBuilder {
	
	private String templatePath = "";
	private String tempXMLPath = "";
	private String root;
	
	
	public PromoCommBuilder(String templatePath, String tempXMLPath, String root){
		this.templatePath = templatePath;
		this.tempXMLPath = tempXMLPath;
		this.root = root;
	}
	
	public int assemble(String csvPath, String resultPath, HashMap<String, String> values, String regiontext, String audittext) {
		long startTime = System.currentTimeMillis();
		
		String picFolder = root + File.separator + "config" + File.separator + "regions" + File.separator + "pic" + File.separator;
		
		Path tempPath = Paths.get(templatePath);
		Path resultikPath = Paths.get(resultPath);
	    try {
			Files.copy(tempPath, resultikPath, REPLACE_EXISTING);
		} catch (IOException e3) {
			e3.printStackTrace();
			return 1;
		}
	    
	    ZipReplacer.zipFileReplace(resultPath, "word/media/image9.jpeg", picFolder + values.get("placeholdermanagernick") + ".jpeg");
	    
	    Random r = new Random(System.currentTimeMillis());
	    int randforname = r.nextInt();
	    String docxmlname = tempXMLPath.replace("document.xml", "" + randforname);
	    File docxmlfile = new File(docxmlname);
		ZipReplacer.getFileFromZip(resultPath, "word/document.xml", docxmlname);
		try {
			ZipReplacer.regexpReplacer(docxmlname, "<w:t>(placeholder.*?)</w:t>", "<w:t>\\$\\{$1\\}</w:t>");
		} catch (IOException e2) {
			e2.printStackTrace();
			return 1;
		}
		ZipReplacer.zipFileReplace(resultPath, "word/document.xml", docxmlname);
		docxmlfile.delete();
		
		randforname = r.nextInt();
	    String relsname = tempXMLPath.replace("document.xml", "" + randforname);
	    File relsfile = new File(relsname);
	    
		ZipReplacer.getFileFromZip(resultPath, "word/_rels/document.xml.rels", relsname);
		try {
			ZipReplacer.regexpReplacer(relsname, "//дельта-юг.рф", "//" + values.get("placeholderdeltasite").replace("www.", ""));
		} catch (IOException e2) {
			e2.printStackTrace();
			return 1;
		}
		ZipReplacer.zipFileReplace(resultPath, "word/_rels/document.xml.rels", relsname);
		relsfile.delete();
		

		CSVReader csvReader = new CSVReader(csvPath);
		
		Table table = new Table();
		Line line;
		csvReader.skipLine();
		while((line = csvReader.readLine()) != null){
			table.addLine(line);
		}
		csvReader.close();
		
		PromoDOCXworker dw = null;
		try {
			System.out.println(templatePath + "\n" + resultPath);
			dw = new PromoDOCXworker(resultPath, resultPath);
		} catch (Docx4JException e) {
			e.printStackTrace();
			return 1;
		} catch (JAXBException e) {
			e.printStackTrace();
			return 1;
		}

		for(int i = 0; i < table.getSize(); i++){
			if(i == table.getSize() - 1){
				dw.addLastRowToTable(table.getLine(i));
			}else{
				dw.addRowToTable(table.getLine(i), (i % 2 == 0));
			}
		}

		P auditParagraph = null;
	    try {
	    	auditParagraph = dw.findByPlaceholderParagraph(PromoDOCXworker.getAllElementFromObject(dw.getMainDocPart(), P.class), "phaudit");
		} catch (Docx4JException | JAXBException e1) {
			e1.printStackTrace();
			return 1;
		}
	    Text at = (Text) PromoDOCXworker.getAllElementFromObject(auditParagraph, Text.class).get(0);
	    at.setValue(audittext);
		

	    List<Object> hls = PromoDOCXworker.getAllElementFromObject(dw.getMainDocPart(), Hyperlink.class);
	    Relationship rel = dw.getMainDocPart().getRelationshipsPart().getRelationshipByID(((Hyperlink) hls.get(0)).getId());
	    rel.setTarget("mailto:" + values.get("placeholderemail"));

	    List<Object> lt = PromoDOCXworker.getAllElementFromObject(dw.templateTable, Text.class);
	    values.put("placeholderauditory", ((Text)lt.get(lt.size()-1)).getValue());

	    String[] regionArray = regiontext.split("\n");  
	    if(regionArray.length > 0){
	    	P regionParagraph = null;
		    try {
				regionParagraph = dw.findByPlaceholderParagraph(PromoDOCXworker.getAllElementFromObject(dw.getMainDocPart(), P.class), "phregion");
			} catch (Docx4JException | JAXBException e1) {
				e1.printStackTrace();
				return 1;
			}
		    int regionParagraphIndex = dw.getMainDocPart().getContent().indexOf(regionParagraph) + 1;
		    Text t = (Text) PromoDOCXworker.getAllElementFromObject(regionParagraph, Text.class).get(0);
		    t.setValue(regionArray[0]);
		    for(int i = 1; i < regionArray.length; i++){
		    	P p = (P) XmlUtils.deepCopy(regionParagraph);
		    	t = (Text) PromoDOCXworker.getAllElementFromObject(p, Text.class).get(0);
		    	t.setValue(regionArray[i]);
		    	dw.getMainDocPart().getContent().add(regionParagraphIndex++, p);
		    }
	    }

	    List<Object> footerContent = dw.getFooterPart().getContent();
	    Text t = (Text) PromoDOCXworker.getAllElementFromObject(footerContent.get(2), Text.class).get(0);
	    t.setValue(values.get("placeholderaddress"));
	    t = (Text) PromoDOCXworker.getAllElementFromObject(footerContent.get(3), Text.class).get(0);
	    t.setValue(values.get("placeholderphonenumbers"));
	    t = (Text) PromoDOCXworker.getAllElementFromObject(footerContent.get(4), Text.class).get(0);
	    t.setValue(values.get("placeholderdeltasite"));

		try {
			dw.getMainDocPart().variableReplace(values);
		} catch (JAXBException | Docx4JException e) {
			e.printStackTrace();
			return 1;
		}

		dw.writeDocx();
		System.out.println("Готово; " + (System.currentTimeMillis() - startTime) + " мс.");
		return 0;	
	}
	
}
