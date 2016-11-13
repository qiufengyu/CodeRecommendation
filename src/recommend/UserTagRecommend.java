package recommend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import entity.UserReputation;
import utils.DBTools;
import utils.UtilConstant;

public class UserTagRecommend implements UtilConstant {

	Map<Integer, Vector<Double>> tagSimMatrix;
	
	Map<String, Integer> tagIndexMap;
	Map<Integer, String> tagIndexInverse;
	Map<String, Integer> userIndexMap;
	Map<Integer, String> userIndexInverse;
	Map<String, Integer> codeIndexMap;
	
	Map<Integer, Vector<Double>> userTagSimMatrix;
	Map<Integer, Vector<Double>> UTScore;
	Map<Integer, Vector<Integer>> matrix;
	
	DBTools dbt;
	
	public UserTagRecommend() {
		tagSimMatrix = new TreeMap<Integer, Vector<Double>>();
		userTagSimMatrix = new TreeMap<Integer, Vector<Double>>();
		tagIndexMap = new TreeMap<String, Integer>();
		tagIndexInverse = new TreeMap<Integer, String>();
		userIndexMap = new TreeMap<String, Integer>();
		userIndexInverse = new TreeMap<Integer, String>();
		codeIndexMap = new TreeMap<String, Integer>();
		UTScore = new TreeMap<Integer, Vector<Double>>();
		
		matrix = new TreeMap<Integer, Vector<Integer>>();
				
		dbt = new DBTools();
	}
	
	public void runUserTagRecommend() {
		
		readIndexMap();
		readMatrix();
//		constructTagSimMatrix();
		
		readTagSimMatrix();
		
		// User Similarity calculated by tags
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
					double sim = getUserTagSimMatrix(user1Index, user2Index);
					if(Math.abs(sim-0.0)>(1e-6)) {
						weight += sim;
						double temp = score.get(scoreIndex);
						score.set(scoreIndex, temp + sim*getMatrix(user2Index, scoreIndex));
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
			UTScore.put(user1Index, score);
			System.out.println("User #"+user1Index+"\tfinished!");
			
		}
		
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userTagMatrix),"UTF-8"));
			for(Entry<Integer, Vector<Double>> entry : UTScore.entrySet()) {
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
	
	
	private double getMatrix(int m, int n) {
		return matrix.get(m).get(n);
	}

	private void readTagSimMatrix() {
		
		for(int i = 0; i<numOfUser; ++i) {
			Vector<Double> allZeros = new Vector<Double>();
			allZeros.setSize(numOfUser);
			for(int j = 0; j<numOfUser; ++j) {
				allZeros.set(j, 0.0);
			}
			userTagSimMatrix.put(i, allZeros);
		}
		
		BufferedReader br;
		String line;		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(userTagSim),"UTF-8"));
			int lineCount = 0;			
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					userTagSimMatrix.get(lineCount).set(i, Double.valueOf(sp[i]));
				}
				lineCount++;
			}
			br.close();
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
	
	public void constructTagSimMatrix() {
		
		/* init
		 *   read map and index
		 */
		readTagIndex();
		readIndexMap();
		readTagSim();		
		
		for(int i = 0; i<numOfUser; ++i) {
			Vector<Double> allZeros = new Vector<Double>(numOfUser);
			for(int j = 0; j<numOfUser; ++j) {
				allZeros.add(0.0);
			}
			// setSimValue(i,i,1.0);
			userTagSimMatrix.put(i, allZeros);
		}
		
		for(int i = 0; i<numOfUser; ++i) {
			String uid1 = userIndexInverse.get(i);
			try {
				UserReputation ur1 = dbt.selectUserReputation(uid1);
				if( null != ur1) {
					Vector<String> t1 = ur1.getUserTags();
					// System.out.println(t1);
					if(null != t1) {
						for(int j = 0; j<numOfUser; ++j) {
							String uid2 = userIndexInverse.get(j);
							UserReputation ur2 = dbt.selectUserReputation(uid2);
							if(ur2 != null) {
								Vector<String> t2 = ur2.getUserTags();
								if(t2 != null) {
									double sum = 0.0;
									for(String x1 : t1) {
										for(String x2: t2) {
											double temp = tagSimMatrix.get(tagIndexMap.get(x1)).get(tagIndexMap.get(x2));
											sum += temp;
											// System.out.println(temp);
										}
									}
									sum = sum/Math.sqrt((double)t1.size()*t2.size());
									setSimValue(i, j, sum);
								}	
							}
						}
					}
				}
				System.out.println("User #"+i+"\tfinished!");
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			
		}		

		// write file
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userTagSim),"UTF-8"));
			for(Entry<Integer, Vector<Double>> entry : userTagSimMatrix.entrySet()) {
				Vector<Double> v = entry.getValue();
				for(int i = 0; i<v.size()-1; ++i) {
					double x = v.get(i);
					if(x<1e-6) {
						bw.write("0.0 ");
					}
					else {
						bw.write(x+" ");
					}
				}
				double e = v.get(v.size()-1);
				if(e<1e-6) {
					bw.write("0.0\n");
				} else {
					bw.write(e+"\n");
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void readTagIndex() {
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(tagDictionary),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] sp = line.split("\t");
				tagIndexMap.put(sp[0], Integer.valueOf(sp[1]));
				tagIndexInverse.put(Integer.valueOf(sp[1]), sp[0]);				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void readTagSim() {
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(UtilConstant.tagSim),"UTF-8"));
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
				tagSimMatrix.put(lineCount, v);
				lineCount++;
			}
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
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Read Index Map over!");
		
	}
	
	private void setSimValue(int m, int n, double val) {
		Vector<Double> temp = userTagSimMatrix.get(m);
		temp.set(n, val);
		userTagSimMatrix.put(m, temp);		
	}
	
	
	private double getUserTagSimMatrix(int m, int n) {
		return userTagSimMatrix.get(m).get(n);
	}
	
	private double getTagSim(int i, int j) {
		return tagSimMatrix.get(i).get(j);
	}
	
	
	
	
}
