/**
 * generate tag word list for user and code
 */

package preparation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import utils.DBTools;
import utils.UtilConstant;

public class Dictionary implements UtilConstant{
	DBTools dbtool;
	// Tag related	
	Set<String> userTagSet;
	Map<String, Integer> userTagMap;
	Set<String> codeTagSet;
	Map<String, Integer> codeTagMap;
	// ID related
	Set<String> userIDSet;
	Map<Integer, String> userIDMap;
	Set<String> codeIDSet;
	Map<Integer, String> codeIDMap;
	

	public Dictionary() {
		super();
		dbtool = new DBTools();
		userTagSet = new TreeSet<String>();
		codeTagSet = new TreeSet<String>();
		userTagMap = new TreeMap<String, Integer>();
		codeTagMap = new TreeMap<String, Integer>();
		userIDSet = new TreeSet<String>();
		codeIDSet = new TreeSet<String>();
		userIDMap = new TreeMap<Integer, String>();
		codeIDMap = new TreeMap<Integer, String>();
		
	}

	public Dictionary(DBTools dbtool) {
		super();
		this.dbtool = dbtool;
	}
	
	/*
	 * generate a matrix, m*n, id should look in dictionary
	 * m=528 #users, n=662: #items,2 means he is the pulisher, 1 means interested followers, 0 means not
	 */
	public void generateIDMap() {
		generateCodeIDMap();
		generateUserIDMap();
	}
	
	private void generateCodeIDMap() {
		ResultSet rs;
		try {
			rs = dbtool.selectCodeALL();
			while(rs.next()) {
				codeIDSet.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(codeIDDic),"UTF-8"));
			int sz = 0;
			for(String x: codeIDSet) {
				bw.write(sz+"\t"+x+"\n");
				bw.flush();
				sz++;
			}	
			bw.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}
	
	private void generateUserIDMap() {
		BufferedReader br;
		BufferedWriter bw;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(userIDList),"UTF-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userIDDic),"UTF-8"));
			int sz = 0;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				bw.write(sz+"\t"+line+"\n");
				sz++;
				bw.flush();
			}
			bw.flush();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		int sz = 0;
		for(String x: codeIDSet) {
			codeIDMap.put(sz, x);
			sz++;
		}		
		
	}
	

	
	private void codeTagSetConstruct() {
		ResultSet rs;
		try {
			rs = dbtool.selectCodeALL();
			while(rs.next()) {
				for(int i = 4; i<=8; ++i) {
					String temp = rs.getString(i);
					if(temp != null)
						codeTagSet.add(temp);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void genCodeTagDic() {
		codeTagSetConstruct();
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(codeTagDictionary),"UTF-8"));
			int index = 1;
			for(String x: codeTagSet) {
				codeTagMap.put(x, index);
				bw.write(x+" "+index+"\n");
				index++;
				bw.flush();
			}
			System.out.println("In all "+codeTagSet.size()+" code tags.");
			bw.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void userTagSetConstruct() {
		ResultSet rs;
		try {
			rs = dbtool.selectReputationALL();
			while(rs.next()) {
				for(int i = 4; i<=9; ++i) {
					String x = rs.getString(i);
					if(x != null)
						userTagSet.add(x);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void genUserTagDic() {
		userTagSetConstruct();
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userTagDictionary),"UTF-8"));
			int index = 1;
			for(String x: userTagSet) {
				userTagMap.put(x, index);
				bw.write(x+" "+index+"\n");
				index++;
				bw.flush();
			}
			System.out.println("In all "+userTagSet.size()+" user tags.");
			bw.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void genTagDic() {
		genCodeTagDic();
		genUserTagDic();
	}

	public DBTools getDbtool() {
		return dbtool;
	}

	public Set<String> getUserTagSet() {
		return userTagSet;
	}

	public Map<String, Integer> getUserTagMap() {
		return userTagMap;
	}

	public Set<String> getCodeTagSet() {
		return codeTagSet;
	}

	public Map<String, Integer> getCodeTagMap() {
		return codeTagMap;
	}
	
	
}
