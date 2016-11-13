package evaluation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import entity.Code;
import entity.UserReputation;
import utils.DBTools;
import utils.UtilConstant;

public class MergeEvaluation implements UtilConstant {
	
	Map<String, ArrayList<String>> gMerge;
	Map<String, ArrayList<String>> pMerge;
	DBTools dbt;
	
	public MergeEvaluation() {
		gMerge = new HashMap<String, ArrayList<String>>();
		pMerge = new HashMap<String, ArrayList<String>>();
		dbt = new DBTools();
		
	}	
	
	private void readGroundFile() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(groundList), "UTF-8"));
			String line;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] tuple = line.split("\t");
				String qid = tuple[0];
				String uid = tuple[1];
				if(gMerge.containsKey(uid)) {
					gMerge.get(uid).add(qid);
				}
				else {
					ArrayList<String> tempList = new ArrayList<String>();
					tempList.add(qid);
					gMerge.put(uid, tempList);
				}
				
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readPredictedFile() {
		String timeline = "2016-04-01";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(resultDump), "UTF-8"));
			String line;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] tuple = line.split("\t");
				String qid = tuple[0];
				String uid = tuple[1];
//				System.out.println(qid);
				if(gMerge.containsKey(uid)) {
					Code c = dbt.selectCode(qid);
					if(true) {
						if(pMerge.containsKey(uid)) {
							pMerge.get(uid).add(qid);
						}
						else {
							ArrayList<String> tempList = new ArrayList<String>();
							tempList.add(qid);
							pMerge.put(uid, tempList);
						}
					}
				}
				
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void mergeEval() {
		readGroundFile();
		readPredictedFile();
		
		int gsize = gMerge.size();
		int psize = pMerge.size();
		
		int correct = 0;
		
		for(Entry<String, ArrayList<String>> entry : gMerge.entrySet()) {
			String uid = entry.getKey();
			if(pMerge.containsKey(uid)) {
				ArrayList<String> glist = entry.getValue();
				ArrayList<String> plist = pMerge.get(uid);
				boolean flag = false;
				for(String x : glist) {
					if(plist.contains(x))
						flag = true;
				}
				if(flag) {
					correct++;
				}
			}			
		}
		System.out.println("#ground = "+gsize);
		System.out.println("#prediction = "+psize);
		System.out.println("#correct = "+correct);
		double precision = (double)correct/psize*100.0;
		double recall = (double)correct/gsize*100.0;
		System.out.println("Precision: "+precision+"%");
		System.out.println("Recall: "+recall+"%");
		double f1 = 2*precision*recall/(precision+recall);
		System.out.println("F1-Score: "+f1+"%");
		
	}

}
