package pojo;
import java.util.ArrayList;

public class Table {
	
	private ArrayList<Line> lines;
	
	
	public Table(){
		this.lines = new ArrayList<Line>();
	}
	
	public void addLine(Line line){
		this.lines.add(line);
	}
	
	
	public Line getLine(int num){
		return this.lines.get(num);
	}
	
	public int getSize(){
		return this.lines.size();
	}
	
	public ArrayList<Line> getLines(){
		return this.lines;
	}
	
}
