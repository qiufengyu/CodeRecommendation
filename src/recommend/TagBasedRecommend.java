package recommend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import entity.UserReputation;
import utils.DBTools;
import utils.MatrixUtil;
import utils.UtilConstant;

public class TagBasedRecommend implements UtilConstant {
	
	Map<String, Integer> codeIndexMap;
	Map<String, Integer> userIndexMap;
	Map<Integer, String> codeIndexInverse;
	Map<Integer, String> userIndexInverse;
	
	DBTools dbtool;
	
	Map<Integer, Vector<Integer>> matrix;
	
	Map<Integer, Vector<Double>> tagMatrix;
	
	
	public void TagBasedAlg() {
		readIndexMap();
		readMatrix();
		runTagBasedRecommend();
	}
	

	public TagBasedRecommend() {
		userIndexMap = new TreeMap<String, Integer>();
		codeIndexMap = new TreeMap<String, Integer>();
		codeIndexInverse = new TreeMap<Integer, String>();
		userIndexInverse = new TreeMap<Integer, String>();
		
		matrix = new TreeMap<Integer, Vector<Integer>>();
		tagMatrix = new TreeMap<Integer, Vector<Double>>();
		
		dbtool = new DBTools();
	}
	
	private void runTagBasedRecommend() {
		for(Entry<String, Integer> entryUser : userIndexMap.entrySet()) {
			String userID = entryUser.getKey();
			int userIndex = entryUser.getValue();
			Vector<Double> score = new Vector<Double>();
			score.setSize(numOfCode);
			for(int i = 0; i<numOfCode; ++i) {
				score.set(i, 0.0);
			}
			try {
				UserReputation uu = dbtool.selectUserReputation(userID);
				if(uu == null) {
					tagMatrix.put(userIndex, score);
					System.err.println("User #"+userIndex+"\t"+entryUser.getKey()+" finished! and null user");
					continue;
				}
				Vector<String> userTag = uu.getUserTags();
				if(userTag==null || userTag.size()==0) {
					tagMatrix.put(userIndex, score);
					System.err.println("User #"+userIndex+"\t"+entryUser.getKey()+" finished! and null tags");
					continue;
				}
				for(Entry<String, Integer> entryCode : codeIndexMap.entrySet()) {
					String codeID = entryCode.getKey();
					int codeIndex = entryCode.getValue();
					if(getMatrix(userIndex, codeIndex)==0) {
						Vector<String> codeTag = dbtool.selectCode(codeID).getTags();
						int matchCount = 0;
						for(String x : codeTag) {
							if (userTag.contains(x)) {
								matchCount++;
							}
						}
						score.set(codeIndex, (double)matchCount/1.0);
					}
				}
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			score = MatrixUtil.normlize(score);
			tagMatrix.put(userIndex, score);
			System.out.println("User #"+userIndex+"\t"+entryUser.getKey()+" finished!");
		}
		
		//write file tag matrix
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(UtilConstant.tagMatrix),"UTF-8"));
			for(Entry<Integer, Vector<Double>> entry : tagMatrix.entrySet()) {
				Vector<Double> inner = entry.getValue();
				for(int i = 0; i<numOfCode-1; ++i) {
					bw.write(inner.get(i)+" ");
				}
				bw.write(inner.get(numOfCode-1)+"\n");
				bw.flush();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void readIndexMap() {
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(userIDDic),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] sp = line.split("\t");
				userIndexMap.put(sp[1], Integer.valueOf(sp[0]));
				userIndexInverse.put(Integer.valueOf(sp[0]), sp[1]);
			}
			br.close();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(codeIDDic),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] sp = line.split("\t");
				codeIndexMap.put(sp[1], Integer.valueOf(sp[0]));
				codeIndexInverse.put(Integer.valueOf(sp[0]), sp[1]);
				
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private int getMatrix(int i, int j) {
		return matrix.get(i).get(j);
	}
	
	
	private void readMatrix() {
		// init
		for(int i = 0; i<numOfUser; ++i) {
			Vector<Integer> allZeros = new Vector<Integer>();
			allZeros.setSize(numOfCode);
			for(int j = 0; j<numOfCode; ++j) {
				allZeros.set(j, 0);
			}
			matrix.put(i, allZeros);
		}
		
		BufferedReader br;
		String line;		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(matrixFile),"UTF-8"));
			int lineCount = 0;			
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					/*
					int[] temp = matrix.get(lineCount);
					temp[i] = Integer.valueOf(sp[i]);
					matrix.put(lineCount, temp);
					*/
					matrix.get(lineCount).set(i, Integer.valueOf(sp[i]));
				}
				lineCount++;
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
