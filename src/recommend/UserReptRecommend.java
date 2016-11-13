package recommend;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import entity.UserReputation;
import utils.DBTools;
import utils.UtilConstant;

/*
 * user reputation used to reduce the celebrity influences of some user
 * divided by reputation  
 */
public class UserReptRecommend implements UtilConstant {
	DBTools dbtool;
	
	public UserReptRecommend() {
		dbtool = new DBTools();
	}
	
	public void UserReptAlg() {
		UserReputationReduced();
	}
	
	private void UserReputationReduced() {
		
		ResultSet rs;
		Vector<String> userIDVec = new Vector<String>();
		Vector<Integer> userReptVec = new Vector<Integer>();
		int max = 0;
		try {
			rs = dbtool.selectCodeALL();			
			while(rs.next()) {
				String temp = rs.getString(3);
				userIDVec.addElement(temp);
			}			
			for(String x : userIDVec) {
				UserReputation ur = dbtool.selectUserReputation(x);
				if(ur == null) {
					userReptVec.addElement(0);
				}
				else {
					int rept = dbtool.selectUserReputation(x).getReputation();
					if(rept>max) {
						max = rept;
					}
					userReptVec.addElement(rept);
				}
			}
		} catch (SQLException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(UtilConstant.userReptVec),"UTF-8"));
			for(int x : userReptVec) {
				double inversex = (double)x/(double)max;
				bw.write(inversex+"\n");
				bw.flush();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(max);
	}
}
