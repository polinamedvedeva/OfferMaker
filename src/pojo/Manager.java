package pojo;

public class Manager {
	
	private String nick;
	private String name;
	private String email;
	private String phonenumber;
	private String position;
	
	public Manager(String nick) {
		super();
		this.nick = nick;
	}
	
	public Manager(Manager m) {
		super();
		this.nick = m.getNick();
		this.name = m.getName();
		this.email = m.getEmail();
		this.phonenumber = m.getPhonenumber();
	}
	
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	
	public String toString(){
		return this.name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

}
