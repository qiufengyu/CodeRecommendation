package entity;
import java.util.Calendar;
import java.util.Vector;

public class Code {
	
	String questionID; // key
	String title;
	String userID;
	Vector<String> tags;
	String context;
	String codeEntity;
	int viewed;
	String timestamp;

	Vector<String> answerList;
	
	public Code() {
		this.questionID = "";
		this.title = "";
		this.userID = "";
		this.tags = new Vector<String>();
		this.context = "";
		this.codeEntity = "";
		this.viewed=0;
		this.answerList = new Vector<String>();
		this.timestamp="";
	}
	

	public Code(String questionID, String title, String userID, Vector<String> tags, String context, String codeEntity, int viewed, Vector<String> answerList, String timestamp) {
		this.questionID = questionID;
		this.title = title;
		this.userID = userID;
		this.tags = tags;
		this.context = context;
		this.codeEntity = codeEntity;
		this.viewed = viewed;
		this.answerList = answerList;
		this.timestamp = timestamp;
	}

	public String getQuestionID() {
		return questionID;
	}

	public void setQuestionID(String questionID) {
		this.questionID = questionID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Vector<String> getTags() {
		return tags;
	}

	public void setTags(Vector<String> tags) {
		this.tags = tags;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getCodeEntity() {
		return codeEntity;
	}

	public void setCodeEntity(String codeEntity) {
		this.codeEntity = codeEntity;
	}
	
	public int getViewed() {
		return viewed;
	}

	public void setViewed(int viewed) {
		this.viewed = viewed;
	}
	
	public void showCode() {
		//System.out.println("Question ID: "+ questionID);
		System.out.println("Title: " + title);
		System.out.println("Asked: " + userID);
		System.out.println("Tags: " + tags.toString());
		System.out.println("Answer: " + answerList.toString());
		//System.out.println("Context: " + context);		
		// System.out.println("Code: " + codeEntity);
		System.out.println("Viewed: " + viewed);
		System.out.println("==================");
	}

	public Vector<String> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(Vector<String> answerList) {
		this.answerList = answerList;
	}
	
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getAnswerListString() {
		String s = new String();
		for(String x: answerList) {
			s = s + x;
			s = s +"&";
		}
		return s;
	}
	
	
	
	

}
