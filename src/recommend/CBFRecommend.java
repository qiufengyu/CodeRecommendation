package recommend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import utils.MatrixUtil;
import utils.UtilConstant;

public class CBFRecommend implements UtilConstant {
	Map<String, Vector<Double>> docMap;
	Map<Integer, Vector<Double>> contentSimMatrix;
	Map<String, Integer> codeIndexMap;
	Map<String, Integer> userIndexMap;
	Map<Integer, String> codeIndexInverse;
	Map<Integer, String> userIndexInverse;
	
	Map<Integer, Vector<Integer>> matrix;
	
	Map<Integer, Vector<Double>> CBFScore;


	public CBFRecommend() {
		super();
		// TODO Auto-generated constructor stub
		docMap = new TreeMap<String, Vector<Double>>();
		contentSimMatrix = new TreeMap<Integer, Vector<Double>>();
		
		userIndexMap = new TreeMap<String, Integer>();
		codeIndexMap = new TreeMap<String, Integer>();
		matrix = new TreeMap<Integer, Vector<Integer>>();
		CBFScore = new TreeMap<Integer, Vector<Double>>();
		
		codeIndexInverse = new TreeMap<Integer, String>();
		userIndexInverse = new TreeMap<Integer, String>();
	}
	
	public void CBFAlg() {
		readIndexMap();
		readMatrix();
		readContentSimMatrix();
		runCBF();
	}
	
	private void runCBF() {
		BufferedWriter bw;
		Vector<Double> score = new Vector<Double>();
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cbfMatrix),"UTF-8"));
			// for all user	
			for(Entry<String, Integer> entryUser : userIndexMap.entrySet()) {
				int userIndex = entryUser.getValue();
				score.clear();
				for(int i = 0; i<numOfCode; ++i) {
					score.add(0.0);
				}
				for(Entry<String, Integer> entryCode1 : codeIndexMap.entrySet()) {
					int codeIndex1 = entryCode1.getValue();
					int oldVal = getMatrix(userIndex, codeIndex1);
					if(oldVal == 0) {
						double weight = 0.0;
						double sumV = 0.0;
						int cnt = 0;
						for(Entry<String, Integer> entryCode2 : codeIndexMap.entrySet()) {
							int codeIndex2 = entryCode2.getValue();
							if(codeIndex2 != codeIndex1) {
								int innerVal = getMatrix(userIndex, codeIndex2);
								if(innerVal > 0) {
									double codeSim = getContentSimMatrix(codeIndex1, codeIndex2);
									if(codeSim > 0) {
										cnt++;
										weight += codeSim;
										sumV += (innerVal*codeSim);
										// System.out.println(codeSim+"\t"+innerVal);
									}
								}	
							}
						}
						
						if(Math.abs(weight-0.0)<1e-8|| cnt<=2) {
							score.set(codeIndex1, 0.0);
						}
						else {
							double val = sumV / weight;
							if(val==2.0) {
								score.set(codeIndex1, 0.0);
							}
							else 
								score.set(codeIndex1, val);
						}
					}
				}
				// CBFScore.put(userIndex, score);
				score = MatrixUtil.normlize(score);
				System.out.println("User #"+userIndex+"\t"+entryUser.getKey()+" finished!");
				
				for(int i = 0; i<numOfCode-1; ++i) {
					bw.write(score.get(i)+" ");
				}
				bw.write(score.get(numOfCode-1)+"\n");
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Read Index Map over!");
		
	}
	
	private void readMatrix() {
		// init
		for(int i = 0; i<numOfUser; ++i) {
			Vector<Integer> allZeros = new Vector<Integer>();
			for(int j = 0; j<numOfCode; ++j) {
				allZeros.add(0);
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
	
	private void readContentSimMatrix() {
		// init
		BufferedReader br;
		String line;		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(titleSimMatrix),"UTF-8"));
			int lineCount = 0;			
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				Vector<Double> v = new Vector<Double>();
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					v.add(Double.valueOf(sp[i]));
				}
				contentSimMatrix.put(lineCount, v);
				lineCount++;
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
	
	private double getContentSimMatrix(int i, int j) {
		return contentSimMatrix.get(i).get(j);
	}
	
	
	
	
	
	
}
