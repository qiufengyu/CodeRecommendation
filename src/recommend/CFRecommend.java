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

import utils.UtilConstant;
import utils.MatrixUtil;

public class CFRecommend implements UtilConstant {
	
	Map<String, Integer> userIndexMap;
	Map<String, Integer> codeIndexMap;
	Map<Integer, Vector<Double>> simMatrix;
	Map<Integer, Vector<Integer>> matrix;
	
	Map<Integer, Vector<Double>> CFScore;
	
	
	public CFRecommend() {
		super();
		userIndexMap = new TreeMap<String, Integer>();
		codeIndexMap = new TreeMap<String, Integer>();
		matrix = new TreeMap<Integer, Vector<Integer>>();
		simMatrix = new TreeMap<Integer, Vector<Double>>();
		CFScore = new TreeMap<Integer, Vector<Double>>();
	}
	
	public void CFAlg() {
		readIndexMap();
		readMatrix();
		readSimMatrix();
		runCF();
		
	}
	
	public void runCF() {
		BufferedWriter bw;
		
		// for all user	
		for(Entry<String, Integer> entryUser1 : userIndexMap.entrySet()) {
			int user1Index = entryUser1.getValue();
			Vector<Double> score = new Vector<Double>();
			score.setSize(numOfCode);
			double weight = 0.0;
			for(int i = 0; i<numOfCode; ++i) {
				score.set(i, 0.0);
			}
			for(Entry<String, Integer> entryCode: codeIndexMap.entrySet()) {
				int scoreIndex = entryCode.getValue();
				for(Entry<String, Integer> entryUser2 : userIndexMap.entrySet()) {
					int user2Index = entryUser2.getValue();
					// calculate predicted socre weighed average of similar users
					double sim = getValueSimMatrix(user1Index, user2Index);
					if(Math.abs(sim-0.0)>(1e-6)) {
						weight += sim;
						double temp = score.get(scoreIndex);
						score.set(scoreIndex, temp + sim*getValueMatrix(user2Index, scoreIndex));
					}
				}
				if(Math.abs(weight-0.0)<1e-6) {
					score.set(scoreIndex, 0.0);
				}
				else {
					double tt = score.get(scoreIndex);
					score.set(scoreIndex, tt/weight);
				}
			}
			score = MatrixUtil.normlize(score);
			CFScore.put(user1Index, score);
			System.out.println("User #"+user1Index+"\tfinished!");
			
		}
		
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cfMatrix),"UTF-8"));
			for(Entry<Integer, Vector<Double>> entry : CFScore.entrySet()) {
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
			}
			br.close();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(codeIDDic),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] sp = line.split("\t");
				codeIndexMap.put(sp[1], Integer.valueOf(sp[0]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	private void readSimMatrix() {
		// init
		for(int i = 0; i<numOfUser; ++i) {
			Vector<Double> allZeros = new Vector<Double>();
			allZeros.setSize(numOfUser);
			for(int j = 0; j<numOfUser; ++j) {
				allZeros.set(j, 0.0);
			}
			simMatrix.put(i, allZeros);
		}
		
		BufferedReader br;
		String line;		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(simMatrixFile),"UTF-8"));
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
					simMatrix.get(lineCount).set(i, Double.valueOf(sp[i]));
				}
				lineCount++;
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int getValueMatrix(int m, int n) {
		return matrix.get(m).get(n);
	}
	
	private double getValueSimMatrix(int m, int n) {
		return simMatrix.get(m).get(n);
	}

}
