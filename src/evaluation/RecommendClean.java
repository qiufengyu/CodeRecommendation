package evaluation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import entity.UserReputation;
import utils.DBTools;
import utils.UtilConstant;

public class RecommendClean implements UtilConstant {
	
	DBTools db;
	
	Set<String> cGround;
	Set<String> cPredicted;
	
	public RecommendClean() {
		db = new DBTools();
		
		cGround = new HashSet<String>();
		cPredicted = new HashSet<String>();
	}
	
	private void cleanGround() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(groundList), "UTF-8"));
			String line;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] tuple = line.split("\t");
				String uid = tuple[1];
				UserReputation ur = db.selectUserReputation(uid);
				if(ur != null && ur.getReputation() >= 10) {
					cGround.add(line);
				}
			}
			br.close();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void cleanPredicted() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(resultDump), "UTF-8"));
			String line;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] tuple = line.split("\t");
				String uid = tuple[1];
				int rept = db.selectUserReputation(uid).getReputation();
				if(rept >= 10) {
					cPredicted.add(line);
				}
			}
			br.close();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void evaluate() {
		cleanGround();
		cleanPredicted();
		System.out.println("#ground = "+cGround.size());
		System.out.println("#predicted = "+cPredicted.size());
		
		int count = 0;
		for(String x : cPredicted) {
			if(cGround.contains(x))
				count++;
		}
		
		double p = (double)count / cPredicted.size();
		double r = (double)count / cGround.size();
		double f = 2*r*p/(p+r);
		
		System.out.println("Precision = "+p*100+"%");
		System.out.println("Recall = "+r*100+"%");
		System.out.println("F-Sore = "+f*100+"%");
	}
	
}
