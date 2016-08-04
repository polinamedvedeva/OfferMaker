package pojo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Region {
	private static HashMap<String, Region> regionList;
	
	private String name;
	private String address;
	private String phones;
	private String site;
	private List<Manager> managers;
	
	public Region() {
		super();
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
		if(regionList != null){
			return regionList;
		}
		
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
						region.setName(bReader.readLine()); //fileEntry.getName().replace(".txt", "")
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
						
						result.put(region.getName(), region);
					}
				}
			}
		}
		regionList = result;
		return regionList;
	}


}
