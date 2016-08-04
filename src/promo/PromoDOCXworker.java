package promo;
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
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;

import pojo.Line;


public class PromoDOCXworker {
	
	private WordprocessingMLPackage template;
	private String resultFileName;
	
	public Tbl templateTable;
	private Tr templateRowOdd;
	private Tr templateRowEven;
	private Tr templateLastRow;
	
	private FooterPart footerPart;
	
	private List<Object> allObjects;
	
	public PromoDOCXworker(String sourceFileName, String resultFileName) throws Docx4JException, JAXBException{
		this.resultFileName = resultFileName;
		
		try {
			this.template = WordprocessingMLPackage.load(new FileInputStream(new File(sourceFileName)));
		} catch (FileNotFoundException e) {
			//TODO 
			e.printStackTrace();
		}
		
		this.allObjects = getAllElementFromObject(template.getMainDocumentPart(), Text.class);
		
		this.templateTable = (Tbl) getAllElementFromObject(this.template.getMainDocumentPart(), Tbl.class).get(1);
				
		List<Object> objs = getAllElementFromObject(this.templateTable, Tr.class);
		
		this.templateRowOdd = (Tr) XmlUtils.deepCopy(objs.get(1));
		this.templateRowEven = (Tr) XmlUtils.deepCopy(objs.get(2));
		this.templateLastRow = (Tr) XmlUtils.deepCopy(objs.get(3));
		this.removeTemplateRows();
		
		
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

	
	//сохранить документ в ФС
	public void writeDocx(){
		File f = new File(this.resultFileName);
		try {
			template.save(f);
		} catch (Docx4JException e) {
			//TODO
			e.printStackTrace();
		}
	}
	
	
	//добавить строку в таблицу
	public void addRowToTable(Line line, boolean isOdd) {
		Tr workingRow = (Tr) XmlUtils.deepCopy(isOdd ? this.templateRowOdd : this.templateRowEven);

		List<Object> textElements = getAllElementFromObject(workingRow, Text.class);
		
		Text text = (Text) textElements.get(0);
		text.setValue(line.getPhrase());
		
		text = (Text) textElements.get(1);
		text.setValue("" + line.getFreq());
		
		List<Object> objs = this.templateTable.getContent();
		
		objs.add(workingRow);
	}	
	
	public void addLastRowToTable(Line line) {
		Tr workingRow = (Tr) XmlUtils.deepCopy(this.templateLastRow);

		List<Object> textElements = getAllElementFromObject(workingRow, Text.class);
		
		Text text = (Text) textElements.get(0);
		text.setValue("");
		
		text = (Text) textElements.get(1);
		text.setValue("" + line.getFreq());
		
		List<Object> objs = this.templateTable.getContent();
		
		objs.add(workingRow);
	}	
	
	
	//удаляет первые две и последнюю строку таблицы
	public void removeTemplateRows(){
		List<Object> content = this.templateTable.getContent();
		
		List<Object> objs = getAllElementFromObject(this.templateTable.getParent(), Tr.class);
		
		content.remove(objs.get(1));
		content.remove(objs.get(2));
		content.remove(objs.get(3));
	}
	
	public Text findByPlaceholderText(String phText){
		for (Iterator<Object> iterator = this.allObjects.iterator(); iterator.hasNext();) {
			Object obj = iterator.next();
			List<?> textElements = getAllElementFromObject(obj, Text.class);
			for (Object text : textElements) {
				Text textElement = (Text) text;
				if (textElement.getValue() != null && textElement.getValue().equals(phText)){
					System.out.println("Текст с плейсхолдером " + phText + " нашелся");
					return textElement;
				}
			}
		}
		System.out.println("Текст с плейсхолдером " + phText + " НЕ БЫЛ НАЙДЕН");
		return null;
	}
	
	public Text findByPlaceholderText(List<Object> objectList, String phText){
		for (Iterator<Object> iterator = objectList.iterator(); iterator.hasNext();) {
			Object obj = iterator.next();
			List<?> textElements = getAllElementFromObject(obj, Text.class);
			for (Object text : textElements) {
				Text textElement = (Text) text;
				if (textElement.getValue() != null && textElement.getValue().equals(phText)){
					System.out.println("Текст с плейсхолдером " + phText + " нашелся");
					return textElement;
				}
			}
		}
		System.out.println("Текст с плейсхолдером " + phText + " НЕ БЫЛ НАЙДЕН");
		return null;
	}
	
	public P findByPlaceholderParagraph(List<Object> objectList, String phText) throws Docx4JException, JAXBException {
		for (Iterator<Object> iterator = objectList.iterator(); iterator.hasNext();) {
			Object obj = iterator.next();
			List<?> textElements = getAllElementFromObject(obj, Text.class);
			for (Object text : textElements) {
				Text textElement = (Text) text;
				if (textElement.getValue() != null && textElement.getValue().equals(phText)){
					System.out.println(("Параграф с плейсхолдером " + phText + " нашелся"));
					return (P) obj;
				}
			}
		}
		System.out.println(("Параграф с плейсхолдером " + phText + " не нашелся"));
		return null;
	}
	

	//получить все элементы данного класса из объекта
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

}