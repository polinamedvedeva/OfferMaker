package Logic;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pojo.Line;

public class CSVReader {
	
private BufferedReader bReader;
private Pattern p = Pattern.compile("(.*?)[,|;](.*?)$");
private Matcher m;

	public CSVReader(String readFileName) {
		try {
			FileInputStream fis = new FileInputStream(readFileName);
			InputStreamReader isr = new InputStreamReader(fis, "windows-1251");
			bReader = new BufferedReader(isr);
		} catch (IOException e) {
			//TODO
		}
				
	}
	
	//считывает строку CSV и возвращает уже объект класса Line
	public Line readLine() {
		String line = null;
		try {
			line = bReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(line == null) return null;
		m = p.matcher(line);
		m.find();
		int value = (m.group(2).equals("")? 0 : Integer.valueOf(m.group(2)));
		Line result = new Line(m.group(1), value);
		return result;
	}
	
	public String skipLine(){
		String line = null;
		try {
			line = bReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return line;
	}
	
	
	public void close()
	{
		try {
			bReader.close();
		} catch (IOException e) {
			//TODO
		}
	}

	
}
