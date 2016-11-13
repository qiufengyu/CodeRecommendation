package entity;

public class UserRelation {
	
	String user1;
	String user2;
	int value;
		
	public UserRelation() {
		super();
		// TODO Auto-generated constructor stub
		user1="";
		user2="";
		value = 0;
	}
	public UserRelation(String user1, String user2, int value) {
		super();
		this.user1 = user1;
		this.user2 = user2;
		this.value = value;
	}
	public String getUser1() {
		return user1;
	}
	public void setUser1(String user1) {
		this.user1 = user1;
	}
	public String getUser2() {
		return user2;
	}
	public void setUser2(String user2) {
		this.user2 = user2;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	

}
