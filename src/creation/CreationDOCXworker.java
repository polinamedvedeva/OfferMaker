package creation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.docx4j.XmlUtils;
import org.docx4j.model.structure.HeaderFooterPolicy;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.wml.Tbl;

public class CreationDOCXworker {
	
	private WordprocessingMLPackage template;
	private String resultFileName;
		
	private FooterPart footerPart;
	
	private List<Object> allObjects;
	
	public CreationDOCXworker(String sourceFileName, String resultFileName) throws Docx4JException, JAXBException{
		this.resultFileName = resultFileName;
		
		try {
			this.template = WordprocessingMLPackage.load(new FileInputStream(new File(sourceFileName)));
		} catch (FileNotFoundException e) {
			//TODO 
			e.printStackTrace();
		}
		
		this.allObjects = getAllElementFromObject(template.getMainDocumentPart(), Text.class);
			
		
		List<SectionWrapper> sectionWrappers = template.getDocumentModel().getSections();
	    for (SectionWrapper sw : sectionWrappers) {
	    	HeaderFooterPolicy hfp = sw.getHeaderFooterPolicy();
			if (hfp.getDefaultFooter()!=null){
				this.footerPart = hfp.getDefaultFooter();
			}			
		}
	    this.template.getMainDocumentPart().convertAltChunks();
	}
	
	public MainDocumentPart getMainDocPart(){
		return this.template.getMainDocumentPart();
	}
	
	public FooterPart getFooterPart(){
		return this.footerPart;
	}

	
	public void writeDocx(){
		File f = new File(this.resultFileName);
		try {
			template.save(f);
		} catch (Docx4JException e) {
			//TODO
			e.printStackTrace();
		}
	}
	
	
	public Text findByPlaceholderText(String phText){
		for (Iterator<Object> iterator = this.allObjects.iterator(); iterator.hasNext();) {
			Object obj = iterator.next();
			List<?> textElements = getAllElementFromObject(obj, Text.class);
			for (Object text : textElements) {
				Text textElement = (Text) text;
				if (textElement.getValue() != null && textElement.getValue().equals(phText)){
					System.out.println("Текст " + phText + " нашелся");
					return textElement;
				}
			}
		}
		System.out.println("Текст " + phText + " не был найден");
		return null;
	}
	
	public Text findByPlaceholderText(List<Object> objectList, String phText){
		for (Iterator<Object> iterator = objectList.iterator(); iterator.hasNext();) {
			Object obj = iterator.next();
			List<?> textElements = getAllElementFromObject(obj, Text.class);
			for (Object text : textElements) {
				Text textElement = (Text) text;
				if (textElement.getValue() != null && textElement.getValue().equals(phText)){
					System.out.println("Текст " + phText + " нашелся");
					return textElement;
				}
			}
		}
		System.out.println("Текст " + phText + " не был найден");
		return null;
	}
	
	public P findByPlaceholderParagraph(List<Object> objectList, String phText) throws Docx4JException, JAXBException {
		for (Iterator<Object> iterator = objectList.iterator(); iterator.hasNext();) {
			Object obj = iterator.next();
			List<?> textElements = getAllElementFromObject(obj, Text.class);
			for (Object text : textElements) {
				Text textElement = (Text) text;
				if (textElement.getValue() != null && textElement.getValue().equals(phText)){
					System.out.println(("Параграф с текстом " + phText + " найден"));
					return (P) obj;
				}
			}
		}
		System.out.println(("Параграф с текстом " + phText + " не найден"));
		return null;
	}
	

	public static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
		List<Object> result = new ArrayList<Object>();
		if (obj instanceof JAXBElement)
			obj = ((JAXBElement<?>) obj).getValue();
		
		if (obj.getClass().equals(toSearch))
			result.add(obj);
		else if (obj instanceof ContentAccessor) {
			List<?> children = ((ContentAccessor) obj).getContent();
			for (Object child : children) {
				result.addAll(getAllElementFromObject(child, toSearch));
			}
		}
		return result;
	}
	
	public void addModules(ArrayList<String> moduleNames, ArrayList<String> modulePrices){
		Tbl table = (Tbl)getAllElementFromObject(template.getMainDocumentPart(), Tbl.class).get(3);
		if(moduleNames.size() == 0){
			Tr rowToDelete = (Tr) getAllElementFromObject(table, Tr.class).get(13);
			table.getContent().remove(rowToDelete);
		}else{
			Tr evenRow = (Tr) getAllElementFromObject(table, Tr.class).get(2);
			Tr oddRow = (Tr) getAllElementFromObject(table, Tr.class).get(3);
			
			Iterator<String> iNam = moduleNames.iterator();
			Iterator<String> iPri = modulePrices.iterator();
			
			int n = table.getContent().indexOf(getAllElementFromObject(table, Tr.class).get(13)) + 1;
			while(iNam.hasNext()){
				String moduleName = iNam.next();
				String modulePrice = iPri.next();
				if(n % 2 == 0){
					Tr wr = (Tr) XmlUtils.deepCopy(evenRow);
					List<Object> texts = getAllElementFromObject(wr, Text.class);
					((Text)texts.get(0)).setValue(moduleName);
					((Text)texts.get(1)).setValue(modulePrice);
					table.getContent().add(n, wr); 
				}else{
					Tr wr = (Tr) XmlUtils.deepCopy(oddRow);
					List<Object> texts = getAllElementFromObject(wr, Text.class);
					((Text)texts.get(0)).setValue(moduleName);
					((Text)texts.get(1)).setValue(modulePrice);
					table.getContent().add(n, wr); 
				}
				n++;
			}
		}	
	}

}