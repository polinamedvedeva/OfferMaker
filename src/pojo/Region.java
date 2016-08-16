package pojo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Region {
	private String nick;
	private String name;
	private String address;
	private String phones;
	private String site;
	private List<Manager> managers;
	
	public Region() {
		super();
		this.managers = new ArrayList<>();
	}
	
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String toString(){
		return this.name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getPhones() {
		return phones;
	}

	public void setPhones(String phones) {
		this.phones = phones;
	}

	public void setManagers(List<Manager> managers) {
		this.managers = managers;
	}
	
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}
	
	public List<Manager> getManagers() {
		return managers;
	}
	
	public static HashMap<String, Region> loadRegions(String dirPath) throws IOException{
		File folder = new File(dirPath);
		if(!folder.exists()){
			return null;
		}
		HashMap<String, Region> result = new HashMap<String, Region>();
		
		String fileName;
		
		String buf;
		
		for (File fileEntry : folder.listFiles()){
			if(fileEntry.isDirectory()){
			}else{
				if (fileEntry.isFile()) {
					fileName = fileEntry.getPath();
					if(fileName.endsWith(".txt")){
						FileInputStream fis = new FileInputStream(new File(fileName));
						InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
						BufferedReader bReader = new BufferedReader(isr);
						
						Region region = new Region();
						region.setNick(fileEntry.getName().replace(".txt", ""));
						region.setName(bReader.readLine()); //
						region.setAddress(bReader.readLine());
						region.setPhones(bReader.readLine());
						region.setSite(bReader.readLine());
						
						List<Manager> managers = new ArrayList<>();
						
						while( (buf = bReader.readLine()) != null){
							Manager mng = new Manager(buf);
							mng.setName(bReader.readLine());
							mng.setPosition(bReader.readLine());
							mng.setEmail(bReader.readLine());
							mng.setPhonenumber(bReader.readLine());
							
							managers.add(mng);
						}
						
						bReader.close();
						
						region.setManagers(managers);
						
						result.put(region.getNick(), region);
					}
				}
			}
		}
		return result;
	}
	
	public static void updateRegion(Region r, String dirPath){
		
		File writeFile = new File(dirPath + File.separator + r.getNick() + ".txt");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(writeFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(fos, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(osw);
		
		try {
			bw.write(r.getName() + "\n");
			bw.write(r.getAddress() + "\n");
			bw.write(r.getPhones() + "\n");
			bw.write(r.getSite() + "\n");
			
			for(Manager m : r.getManagers()){
				bw.write(m.getNick() + "\n");
				bw.write(m.getName() + "\n");
				bw.write(m.getPosition() + "\n");
				bw.write(m.getEmail() + "\n");
				bw.write(m.getPhonenumber() + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean deleteRegion(String nick, String dirPath){
		File f = new File(dirPath + File.separator + nick + ".txt");
		return f.delete();
	}

}
