package recommend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import entity.Code;

import java.util.Map.Entry;

import utils.DBTools;
import utils.MatrixUtil;
import utils.UtilConstant;

public class UserRelationRecommend implements UtilConstant {

	Map<String, Integer> codeIndexMap;
	Map<String, Integer> userIndexMap;
	Map<Integer, String> codeIndexInverse;
	Map<Integer, String> userIndexInverse;
	
	DBTools dbtool;
	
	Map<Integer, Vector<Integer>> matrix;
	
	Map<Integer, Vector<Integer>> relationMap;
	Map<Integer, Vector<Double>> userRelationMatrix;
	
	public UserRelationRecommend() {
		userIndexMap = new TreeMap<String, Integer>();
		codeIndexMap = new TreeMap<String, Integer>();
		codeIndexInverse = new TreeMap<Integer, String>();
		userIndexInverse = new TreeMap<Integer, String>();
		
		relationMap = new TreeMap<Integer,  Vector<Integer>>();
		matrix = new TreeMap<Integer, Vector<Integer>>();
		userRelationMatrix = new TreeMap<Integer, Vector<Double>>();
		
		dbtool = new DBTools();
	}
	
	public void URAlg() {
		readIndexMap();
		readMatrix();
		generateRelationMap();
		readRelationMap();
		runUserRelationRecommend();
	}

	private void runUserRelationRecommend() {
		// TODO Auto-generated method stub
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(UtilConstant.urMatrix),"UTF-8"));

			for(Entry<String, Integer> entryUser : userIndexMap.entrySet()) {
				int userIndex = entryUser.getValue();
				Vector<Double> score = new Vector<Double>();
				score.setSize(numOfCode);
				for(int i = 0; i<numOfCode; ++i) {
					score.set(i, 0.0);
				}
				int weight = getSumWeight(userIndex);
				for(Entry<String, Integer> entryCode : codeIndexMap.entrySet()) {
					String codeID = entryCode.getKey();
					int codeIndex = entryCode.getValue();
					Code c = dbtool.selectCode(codeID);
					String pubID = c.getUserID();
					int pubIDint = userIndexMap.get(pubID);
					Vector<String> ansV = c.getAnswerList();
					if(getMatrix(userIndex, codeIndex)==0) {
						int rel = getRelation(userIndex, pubIDint);
						double oldv = score.get(codeIndex);
						score.set(codeIndex,((rel*2)+oldv)*getMatrix(pubIDint, codeIndex));
						for(String x : ansV) {
							int xint = userIndexMap.get(x);
							rel = getRelation(userIndex, xint);
							double oldvv = score.get(codeIndex);
							score.set(codeIndex, (oldvv + rel)*getMatrix(xint, codeIndex));
						}
						oldv = score.get(codeIndex);
						if(weight == 0) {
							score.set(codeIndex, 0.0);
							//System.out.println("!");
						} 
						else {
							score.set(codeIndex, (double)oldv/(double)weight);
						}
					}
				}
				score = MatrixUtil.normlize(score);
				userRelationMatrix.put(userIndex, score);
				System.out.println("User #"+userIndex+"\t"+entryUser.getKey()+" finished!");
				for(int i = 0; i<numOfCode-1; ++i) {
					bw.write(score.get(i)+" ");
				}
				bw.write(score.get(numOfCode-1)+"\n");
				bw.flush();
			}
			bw.close();
		// write file
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private int getSumWeight(int userIndex) {
		int sum = 0;
		Vector<Integer> v = relationMap.get(userIndex);
		for(int i = 0; i<numOfUser; ++i) {
			sum += v.get(i);
		}
		return sum;
	}
	


	private void readRelationMap() {
		// TODO Auto-generated method stub
		BufferedReader br;
		String line;
		int linecount = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(relMap),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] sp = line.split(" ");
				Vector<Integer> v = new Vector<Integer>();
				for(int i = 0; i<sp.length; ++i) {
					v.addElement(Integer.valueOf(sp[i]));
				}
				relationMap.put(linecount, v);	
				linecount++;
			}
			br.close();
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private void generateRelationMap() {
		// initialize
		for(int i = 0; i<numOfUser; ++i) {
			Vector<Integer> v = new Vector<Integer>();
			// v.setSize(numOfUser);
			for(int j = 0; j<numOfUser; ++j) {
				v.addElement(0);
			}
			relationMap.put(i, v);		
		}
		System.out.println(relationMap.size());
		
		// set values
		for(Entry<String, Integer> entryCode : codeIndexMap.entrySet()) {
			String codeID = entryCode.getKey();			
			Code c = dbtool.selectCode(codeID);
			String pubID = c.getUserID();
			int pubIndex = userIndexMap.get(pubID);
			Vector<String> ans = c.getAnswerList();
			for(String x: ans) {
				if(x.length()>=1 && (!x.equals(pubID))) {
					int ansIndex = userIndexMap.get(x);
					int oldVal = getRelation(pubIndex, ansIndex);
					if(getRelation(ansIndex, pubIndex) != oldVal) {
						System.out.println("1!");
					}
					setRelation(pubIndex, ansIndex, oldVal+1);
					setRelation(ansIndex, pubIndex, oldVal+1);
				}
			}
			System.out.println("#"+codeID+" over");
		}
		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(UtilConstant.relMap),"UTF-8"));
			for(Entry<Integer, Vector<Integer>> entry : relationMap.entrySet()) {
				Vector<Integer> inner = entry.getValue();
				for(int i = 0; i<numOfUser-1; ++i) {
					bw.write(inner.get(i)+" ");
				}
				bw.write(inner.get(numOfUser-1)+"\n");
				bw.flush();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Gen");
	}
	
	private int getRelation(int i, int j) {
		return relationMap.get(i).get(j);
	}
	
	private void setRelation(int i, int j, int val) {
		relationMap.get(i).set(j, val);
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
