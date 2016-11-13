
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import com.mysql.cj.jdbc.PreparedStatement;

import java.io.BufferedInputStream;
import java.io.IOException;
import entity.Code;
import entity.UserReputation;

public class DBTools {

	private Connection connection;
	PreparedStatement pStmtInsertCode = null;
	PreparedStatement pStmtSelectCode = null;
	PreparedStatement pStmtDeleteCode = null;
	PreparedStatement pStmtInsertCodeDev = null;
	PreparedStatement pStmtSelectCodeDev = null;
	PreparedStatement pStmtDeleteCodeDev = null;
	PreparedStatement pStmtInsertCodeTest = null;
	PreparedStatement pStmtSelectCodeTest = null;
	PreparedStatement pStmtDeleteCodeTest = null;
	PreparedStatement pStmtInsertUserRept = null;
	PreparedStatement pStmtSelectUserRept = null;
	PreparedStatement pStmtUpdatetUserRept = null;
	PreparedStatement pStmtInsertUserRel = null;
	PreparedStatement pStmtSelectUserRel = null;
	PreparedStatement pStmtUpdateUserRel = null;
	
	PreparedStatement pStmtSelectCodeAll = null;
	PreparedStatement pStmtSelectCodeDevAll = null;
	PreparedStatement pStmtSelectCodeTestAll = null;
	PreparedStatement pStmtSelectReputationAll = null;
	PreparedStatement pStmtSelectRelationAll = null;
	
	PreparedStatement pStmtInsertCodeTitle = null;
	PreparedStatement pStmtSelectCodeTitle = null;
	PreparedStatement pStmtSelectCodeTitleAll = null;
	
	
	
	
	public DBTools() {
		try {
			linkDatabase();
			System.out.println("Link database OK!");
		} catch (Exception e) {
			System.out.println(e);
			// TODO Auto-generated catch block
			System.out.println("Database initial fail");
			System.exit(0);
		}
	}
	
	private void linkDatabase() throws ClassNotFoundException, SQLException {
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/excode?useSSL=false&serverTimezone=UTC","root","root");
		pStmtInsertCode = (PreparedStatement) connection.prepareStatement("INSERT INTO `code` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
		pStmtSelectCode = (PreparedStatement) connection.prepareStatement("SELECT * FROM `code` WHERE `id`=?");
		pStmtDeleteCode = (PreparedStatement) connection.prepareStatement("DELETE FROM `code` WHERE `id`=?");
		
		pStmtInsertCodeDev = (PreparedStatement) connection.prepareStatement("INSERT INTO `code_dev` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
		pStmtSelectCodeDev = (PreparedStatement) connection.prepareStatement("SELECT * FROM `code_dev` WHERE `id`=?");
		pStmtDeleteCodeDev = (PreparedStatement) connection.prepareStatement("DELETE FROM `code_dev` WHERE `id`=?");
		
		pStmtInsertCodeTest = (PreparedStatement) connection.prepareStatement("INSERT INTO `code_test` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
		pStmtSelectCodeTest = (PreparedStatement) connection.prepareStatement("SELECT * FROM `code_test` WHERE `id`=?");
		pStmtDeleteCodeTest = (PreparedStatement) connection.prepareStatement("DELETE FROM `code_test` WHERE `id`=?");

		pStmtInsertUserRept = (PreparedStatement) connection.prepareStatement("INSERT INTO `user_reputation` VALUES (?,?,?,?,?,?,?,?,?)");
		pStmtSelectUserRept = (PreparedStatement) connection.prepareStatement("SELECT * FROM `user_reputation` WHERE `user`=?");
		pStmtUpdatetUserRept = (PreparedStatement) connection.prepareStatement("UPDATE `user_reputation` SET `reputation`=? WHERE `user`=?");
		
		pStmtInsertUserRel = (PreparedStatement) connection.prepareStatement("INSERT INTO `user_relation` VALUES (?,?,?)");
		pStmtSelectUserRel = (PreparedStatement) connection.prepareStatement("SELECT `relation` FROM `user_relation` WHERE `user1`=? AND `user2`=?");
		pStmtUpdateUserRel = (PreparedStatement) connection.prepareStatement("UPDATE `user_relation` SET `relation`=? WHERE `user1`=? AND `user2`=?");
	
		pStmtSelectCodeAll = (PreparedStatement) connection.prepareStatement("SELECT * FROM `code`");
		pStmtSelectCodeDevAll = (PreparedStatement) connection.prepareStatement("SELECT * FROM `code_dev`");
		pStmtSelectCodeTestAll = (PreparedStatement) connection.prepareStatement("SELECT * FROM `code_test`");
		pStmtSelectReputationAll = (PreparedStatement) connection.prepareStatement("SELECT * FROM `user_reputation`");
		pStmtSelectRelationAll = (PreparedStatement) connection.prepareStatement("SELECT * FROM `user_relation`");
	
		pStmtInsertCodeTitle = (PreparedStatement) connection.prepareStatement("INSERT INTO `code_title` VALUES (?,?)");
		pStmtSelectCodeTitle = (PreparedStatement) connection.prepareStatement("SELECT * FROM `code_title` WHERE `id`=?");
		pStmtSelectCodeTitleAll = (PreparedStatement) connection.prepareStatement("SELECT * FROM `code_title`");
	}
	
	public void insertCode(Code c) throws SQLException {
		String id = c.getQuestionID();
		pStmtSelectCode.setString(1, id);
		ResultSet rs = pStmtSelectCode.executeQuery();
		if(rs.next()) {
			System.out.println("Update Repeated Info!");
			/**
			 * Better not exit, if sometimes fail, then i can repair!
			 */
			pStmtDeleteCode.setString(1, id);
			if(pStmtDeleteCode.executeUpdate()<=0) {
				System.out.println("Delete error!");
				return;
			}
			// return;
		}
		pStmtInsertCode.setString(1, c.getQuestionID());
		pStmtInsertCode.setString(2, c.getTitle());
		pStmtInsertCode.setString(3, c.getUserID());
		Vector<String> tempTags = c.getTags();
		for(int i = 4; i<4+tempTags.size(); i++) {
			pStmtInsertCode.setString(i, tempTags.get(i-4));
		}
		for(int j = 4+tempTags.size(); j<9; j++) {
			pStmtInsertCode.setString(j, null);
		}
		//Check if existed!
		pStmtInsertCode.setString(9, c.getContext());
		pStmtInsertCode.setString(10, c.getCodeEntity());
		pStmtInsertCode.setString(11, c.getAnswerListString());
		pStmtInsertCode.setInt(12, c.getViewed());
		pStmtInsertCode.setString(13, c.getTimestamp());
		int i = pStmtInsertCode.executeUpdate();
		if(i<=0) {
			System.out.println("Insert Code Fail!");
			System.exit(0);
		}
		else {
			System.out.println("Insert Code OK!");
		}
	}	
	
	public void deleteCode(String id) {
		try {
			pStmtDeleteCode.setString(1, id);
			if(pStmtDeleteCode.executeUpdate()<=0) {
				System.out.println("Delete error!");
				return;
			}
			else {
				System.out.println("Delete from code where id = "+id);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void insertCodeDev(Code c) throws SQLException {
		String id = c.getQuestionID();
		pStmtSelectCodeDev.setString(1, id);
		ResultSet rs = pStmtSelectCodeDev.executeQuery();
		if(rs.next()) {
			System.out.println("Update Repeated Info in Dev!");
			/**
			 * Better not exit, if sometimes fail, then i can repair!
			 */
			pStmtDeleteCodeDev.setString(1, id);
			if(pStmtDeleteCodeDev.executeUpdate()<=0) {
				System.out.println("Delete error!");
				return;
			}
			// return;
		}
		pStmtInsertCodeDev.setString(1, c.getQuestionID());
		pStmtInsertCodeDev.setString(2, c.getTitle());
		pStmtInsertCodeDev.setString(3, c.getUserID());
		Vector<String> tempTags = c.getTags();
		for(int i = 4; i<4+tempTags.size(); i++) {
			pStmtInsertCodeDev.setString(i, tempTags.get(i-4));
		}
		for(int j = 4 + tempTags.size(); j<9; j++) {
			pStmtInsertCodeDev.setString(j, null);
		}
		//Check if existed!
		pStmtInsertCodeDev.setString(9, c.getContext());
		pStmtInsertCodeDev.setString(10, c.getCodeEntity());
		pStmtInsertCodeDev.setString(11, c.getAnswerListString());
		pStmtInsertCodeDev.setInt(12, c.getViewed());
		pStmtInsertCodeDev.setString(13, c.getTimestamp());
		int i = pStmtInsertCodeDev.executeUpdate();
		if(i<=0) {
			System.out.println("Insert Code Fail!");
			System.exit(0);
		}
		else {
			System.out.println("Insert Code OK!");
		}
	}	
	
	public void deleteCodeDev(String id) {
		try {
			pStmtDeleteCodeDev.setString(1, id);
			if(pStmtDeleteCodeDev.executeUpdate()<=0) {
				System.out.println("Delete error!");
				return;
			}
			else {
				System.out.println("Delete from code_dev where id = "+id);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void deleteCodeTest(String id) {
		try {
			pStmtDeleteCodeTest.setString(1, id);
			if(pStmtDeleteCodeTest.executeUpdate()<=0) {
				System.out.println("Delete error!");
				return;
			}
			else {
				System.out.println("Delete from code_test where id = "+id);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void insertCodeTest(Code c) throws SQLException {
		String id = c.getQuestionID();
		pStmtSelectCodeTest.setString(1, id);
		ResultSet rs = pStmtSelectCodeTest.executeQuery();
		if(rs.next()) {
			System.out.println("Update Repeated code_test Info!");
			/**
			 * Better not exit, if sometimes fail, then i can repair!
			 * update the code test
			 */
			pStmtDeleteCodeTest.setString(1, id);
			if(pStmtDeleteCodeTest.executeUpdate()<=0) {
				System.out.println("Delete from code_test error!");
			}
			else {
				insertCodeTest(c);
			}
			return;
		}
		pStmtInsertCodeTest.setString(1, c.getQuestionID());
		pStmtInsertCodeTest.setString(2, c.getTitle());
		pStmtInsertCodeTest.setString(3, c.getUserID());
		Vector<String> tempTags = c.getTags();
		for(int i = 4; i<4+tempTags.size(); i++) {
			pStmtInsertCodeTest.setString(i, tempTags.get(i-4));
		}
		for(int j = 4+tempTags.size(); j<9; j++) {
			pStmtInsertCodeTest.setString(j, null);
		}
		//Check if existed!
		pStmtInsertCodeTest.setString(9, c.getContext());
		pStmtInsertCodeTest.setString(10, c.getCodeEntity());
		pStmtInsertCodeTest.setString(11, c.getAnswerListString());
		pStmtInsertCodeTest.setInt(12, c.getViewed());		
		pStmtInsertCodeTest.setString(13, c.getTimestamp());
		int i = pStmtInsertCodeTest.executeUpdate();
		if(i<=0) {
			System.out.println("Insert Code Test Fail!");
			System.exit(0);
		}
		else {
			System.out.println("Insert Test New Code OK!");
		}

		
	}
	
	/**
	 * @param t
	 * @throws SQLException
	 * @throws IOException
	 */
	public Code selectCode(String t) {
		Code c = new Code();
		try {
			pStmtSelectCode.setString(1, t);
			c.setQuestionID(t);
			ResultSet rs = pStmtSelectCode.executeQuery();
			if(rs.next()) {	
				// rs.get
				c.setTitle(rs.getString(2));
				c.setUserID(rs.getString(3));
				// Get all tags of the code and then add to a vector
				Vector<String> codeTags = new Vector<String>();
				for(int i = 4; i<=8; ++i) {// 5 tags max
					String tag = rs.getString(i);
					if(tag != null)
						codeTags.addElement(tag);
					else 
						break; // we can jump out of the in advance
				}
				c.setTags(codeTags);
				// context or document, blob type
				java.sql.Blob contentBlob = rs.getBlob("context");
				BufferedInputStream contentData = new BufferedInputStream (contentBlob.getBinaryStream());
				byte [] buf = new byte [contentData.available()];
				contentData.read(buf, 0, buf.length);
				String context = new String(buf, "UTF-8");
				c.setContext(context);
				// code area get, blob type
				java.sql.Blob codeAreaBlob = rs.getBlob("codeArea");
				BufferedInputStream codeAreaData = new BufferedInputStream (codeAreaBlob.getBinaryStream());
				byte [] buf2 = new byte [codeAreaData.available()];
				codeAreaData.read(buf2, 0, buf2.length);
				String codeArea = new String(buf2, "UTF-8");
				c.setContext(codeArea);
				// get answer list, split by '&'
				String answerlist = rs.getString(11);
				Vector<String> aList = new Vector<String>();
				String[] answerUser = answerlist.split("&");
				for(String x: answerUser) {
					if(x.length()>=2)
						aList.addElement(x);
				}
				c.setAnswerList(aList);		
				c.setViewed(rs.getInt(12));
				c.setTimestamp(rs.getString(13));
			}
			else {
				System.out.println("Select Code "+t+" Error");
				return null;
			}
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return c;
	}
	
	public Code selectCodeDev(String t) {
		Code c = new Code();
		try {
			pStmtSelectCodeDev.setString(1, t);
			c.setQuestionID(t);
			ResultSet rs = pStmtSelectCodeDev.executeQuery();
			if(rs.next()) {	
				// rs.get
				c.setTitle(rs.getString(2));
				c.setUserID(rs.getString(3));
				// Get all tags of the code and then add to a vector
				Vector<String> codeTags = new Vector<String>();
				for(int i = 4; i<=8; ++i) {// 5 tags max
					String tag = rs.getString(i);
					if(tag != null)
						codeTags.addElement(tag);
					else 
						break; // we can jump out of the in advance
				}
				c.setTags(codeTags);
				// context or document, blob type
				java.sql.Blob contentBlob = rs.getBlob("context");
				BufferedInputStream contentData = new BufferedInputStream (contentBlob.getBinaryStream());
				byte [] buf = new byte [contentData.available()];
				contentData.read(buf, 0, buf.length);
				String context = new String(buf, "UTF-8");
				c.setContext(context);
				// code area get, blob type
				java.sql.Blob codeAreaBlob = rs.getBlob("codeArea");
				BufferedInputStream codeAreaData = new BufferedInputStream (codeAreaBlob.getBinaryStream());
				byte [] buf2 = new byte [codeAreaData.available()];
				codeAreaData.read(buf2, 0, buf2.length);
				String codeArea = new String(buf2, "UTF-8");
				c.setContext(codeArea);
				// get answer list, split by '&'
				String answerlist = rs.getString(11);
				Vector<String> aList = new Vector<String>();
				String[] answerUser = answerlist.split("&");
				for(String x: answerUser) {
					if(x.length()>=2)
						aList.addElement(x);
				}
				c.setAnswerList(aList);		
				c.setViewed(rs.getInt(12));
				c.setTimestamp(rs.getString(13));
			}
			else {
				System.out.println("Select CodeDev "+t+" Error");
				return null;
			}
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return c;
	}
	
	public Code selectCodeTest(String t) throws SQLException, IOException {
		Code c = new Code();
		pStmtSelectCodeTest.setString(1, t);
		c.setQuestionID(t);
		ResultSet rs = pStmtSelectCodeTest.executeQuery();
		if(rs.next()) {	
			// rs.get
			c.setTitle(rs.getString(2));
			c.setUserID(rs.getString(3));
			// Get all tags of the code and then add to a vector
			Vector<String> codeTags = new Vector<String>();
			for(int i = 4; i<=8; ++i) {// 5 tags max
				String tag = rs.getString(i);
				if(tag != null)
					codeTags.addElement(tag);
				else 
					break; // we can jump out of the in advance
			}
			c.setTags(codeTags);
			// context or document, blob type
			java.sql.Blob contentBlob = rs.getBlob("context");
			BufferedInputStream contentData = new BufferedInputStream (contentBlob.getBinaryStream());
			byte [] buf = new byte [contentData.available()];
			contentData.read(buf, 0, buf.length);
			String context = new String(buf, "UTF-8");
			c.setContext(context);
			// code area get, blob type
			java.sql.Blob codeAreaBlob = rs.getBlob("codeArea");
			BufferedInputStream codeAreaData = new BufferedInputStream (codeAreaBlob.getBinaryStream());
			byte [] buf2 = new byte [codeAreaData.available()];
			codeAreaData.read(buf2, 0, buf2.length);
			String codeArea = new String(buf2, "UTF-8");
			c.setContext(codeArea);
			// get answer list, split by '&'
			String answerlist = rs.getString(11);
			Vector<String> aList = new Vector<String>();
			String[] answerUser = answerlist.split("&");
			for(String x: answerUser) {
				aList.addElement(x);
			}
			c.setAnswerList(aList);	
			c.setViewed(rs.getInt(12));
			c.setTimestamp(rs.getString(13));
		}
		else {
			// System.out.println("Select Code Test Error");
			return null;
			// System.exit(0);
		}
		return c;
	}
	
	public void insertUserReputation(UserReputation ui) throws SQLException {
		if(ui==null)
			return;
		String id = ui.getUserID();
		pStmtSelectUserRept.setString(1, id);
		ResultSet rs = pStmtSelectUserRept.executeQuery();
		if(rs.next()) {
			System.out.println("Update Reputation old value = "+ui.getReputation());
			pStmtUpdatetUserRept.setInt(1, ui.getReputation());
			pStmtUpdatetUserRept.setString(2, id);
			pStmtUpdatetUserRept.executeUpdate();
			return;
		}
		pStmtInsertUserRept.setString(1, ui.getUserID());
		pStmtInsertUserRept.setInt(2, ui.getReputation());
		pStmtInsertUserRept.setString(3, ui.getLocation());

		Vector<String> tags = ui.getUserTags();
		for(int i = 4; i<tags.size()+4; i++) {
			pStmtInsertUserRept.setString(i, tags.get(i-4));
		}
		for(int i = tags.size()+4; i<=9; ++i) {
			pStmtInsertUserRept.setString(i, null);
		}
		int i = pStmtInsertUserRept.executeUpdate();
		if(i<=0) {
			System.out.println("Insert Reputation Fail!");
			System.exit(0);
		}
		else {
			System.out.println("Insert Reputation OK!");
		}
	}	
	
	public UserReputation selectUserReputation(String t) throws SQLException, IOException {
		UserReputation ur = new UserReputation();
		pStmtSelectUserRept.setString(1, t);
		ur.setUserID(t);
		ResultSet rs = pStmtSelectUserRept.executeQuery();
		if(rs.next()) {
			// System.out.println("Select Reputation OK");
			ur.setReputation(rs.getInt(2));
			ur.setLocation(rs.getString(3));
			Vector<String> userTag = new Vector<String>();
			for(int start = 4; start<=9; start++) {
				String tag = rs.getString(start);
				if(tag != null)
					userTag.addElement(tag);
			}
			ur.setUserTags(userTag);
		}
		else {
		//	System.out.println("Select Reputation Error");
			return null;
			//System.exit(0);
		}
		return ur;
	}
	
	public void insertRelation(Code c) throws SQLException {
		// String questionID = c.getQuestionID();
		/*
		pStmtSelectCode.setString(1, questionID);
		ResultSet rs = pStmtSelectCode.executeQuery();
		if(rs.next()) {
			// System.out.println("We have deal with this question!");
			// return;
		}*/
		String user1 = c.getUserID();
		Vector<String> user2List = c.getAnswerList();
		for(String s: user2List) {
			if(user1.equals(s)) {
				continue;
			}
			pStmtSelectUserRel.setString(1, user1);
			pStmtSelectUserRel.setString(2, s);
			pStmtUpdateUserRel.setString(2, user1);
			pStmtUpdateUserRel.setString(3, s);
			ResultSet rs1 = pStmtSelectUserRel.executeQuery();
			int tempVal = 0;
			if(rs1.next()) {
				tempVal = rs1.getInt(1)+1;
				pStmtUpdateUserRel.setInt(1, tempVal);
				if(pStmtUpdateUserRel.executeUpdate()<=0) {
					System.out.println("Update Relation 1 Failed");
				}
			}	
			
			pStmtSelectUserRel.setString(2, user1);
			pStmtSelectUserRel.setString(1, s);
			pStmtUpdateUserRel.setString(3, user1);
			pStmtUpdateUserRel.setString(2, s);
			ResultSet rs2 = pStmtSelectUserRel.executeQuery();
			tempVal = 0;
			if(rs2.next()) {
				tempVal = rs2.getInt(1)+1;
				pStmtUpdateUserRel.setInt(1, tempVal);
				if(pStmtUpdateUserRel.executeUpdate()<=0) {
					System.out.println("Update Relation 2 Failed");
				}
			}
			
			pStmtInsertUserRel.setString(1,user1);
			pStmtInsertUserRel.setString(2, s);
			pStmtInsertUserRel.setInt(3, 1);
			if(pStmtInsertUserRel.executeUpdate()<=0) {
				System.out.println("Insert Relation 1 Failed!");
			}
			
			pStmtInsertUserRel.setString(2,user1);
			pStmtInsertUserRel.setString(1, s);
			pStmtInsertUserRel.setInt(3, 1);
			if(pStmtInsertUserRel.executeUpdate()<=0) {
				System.out.println("Insert Relation 2 Failed!");
			}			
		}
		System.out.println("Insert Relation OK!");
	}
	
	public int selectUserRelation(String u1, String u2) {
		try {
			pStmtSelectUserRel.setString(1, u1);
			pStmtSelectUserRel.setString(2, u2);
			ResultSet rs = pStmtSelectUserRel.executeQuery();
			if(rs.next())
				return rs.getInt(1);
			else
				return 0;// param in relation embedded model, or -0.5, 0
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;		
	}
	
	public boolean judgeCodeExist(String id) {
		try {
			pStmtSelectCode.setString(1, id);
			ResultSet rs = pStmtSelectCode.executeQuery();
			if(rs.next()) {
				return true;
			}
			else 
				return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;		
	}
	
	public void insertCodeTitle(String id, String title) {
		try {
			pStmtSelectCodeTitle.setString(1, id);
			ResultSet rs = pStmtSelectCodeTitle.executeQuery();
			if(rs.next()) {
				System.out.println("Repeated Question Title!");
				return;
			}
			pStmtInsertCodeTitle.setString(1, id);
			pStmtInsertCodeTitle.setString(2, title);
			int i = pStmtInsertCodeTitle.executeUpdate();
			if(i <= 0) {
				System.out.println("Insert Code Title Error!");
			}
			else {
				System.out.println("Insert Code Title OK!");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public ResultSet selectCodeTitleALL() throws SQLException {
		return pStmtSelectCodeTitleAll.executeQuery();
	}
	
	public ResultSet selectReputationALL() throws SQLException {
		return pStmtSelectReputationAll.executeQuery();
	}
	
	public ResultSet selectCodeALL() throws SQLException {
		return pStmtSelectCodeAll.executeQuery();
	}
	
	public ResultSet selectCodeDevALL() throws SQLException {
		return pStmtSelectCodeDevAll.executeQuery();
	}
	
	public ResultSet selectCodeTestALL() throws SQLException {
		return pStmtSelectCodeTestAll.executeQuery();
	}
	
	
}
