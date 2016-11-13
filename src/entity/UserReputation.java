package entity;

import java.util.Vector;

public class UserReputation {

	String userID;
	int reputation;
	String location;
	Vector<String> userTags;
	
	public UserReputation() {
		super();
		// TODO Auto-generated constructor stub
		userID="";
		reputation = 0;
		location="";
		userTags = new Vector<String>();
	}
	
	public UserReputation(String userID) {
		super();
		this.userID = userID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getReputation() {
		return reputation;
	}

	public void setReputation(int reputation) {
		this.reputation = reputation;
	}

	public Vector<String> getUserTags() {
		return userTags;
	}

	public void setUserTags(Vector<String> userTags) {
		this.userTags = userTags;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void show() {
		// TODO Auto-generated method stub
		System.out.println("User ID: "+ userID);
		System.out.println("Reputation: "+ reputation);
		System.out.println("Location: "+ location);
		System.out.println("Tags: "+ userTags.toString());
		System.out.println("=================================");
	}
	
	
	
	
}
