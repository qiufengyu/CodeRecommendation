package evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import entity.UserReputation;
import utils.DBTools;
import utils.MatrixUtil;
import utils.UtilConstant;

public class RecommendEvaluation implements UtilConstant, MatrixUtil {
	
	Map<String, Integer> codeIndexMap;
	Map<String, Integer> userIndexMap;
	Map<Integer, String> codeIndexInverse;
	Map<Integer, String> userIndexInverse;
	Set<String> groundSet;
	
	DBTools db;
	
	Map<Integer, Vector<Double>> cf_Matrix;
	Map<Integer, Vector<Double>> cbf_Matrix;
	Map<Integer, Vector<Double>> tag_Matrix;
	Map<Integer, Vector<Double>> ur_Matrix;
	Vector<Double> rept_Reduced;
	
	
	public RecommendEvaluation() {
		db = new DBTools();
		userIndexMap = new TreeMap<String, Integer>();
		codeIndexMap = new TreeMap<String, Integer>();
		codeIndexInverse = new TreeMap<Integer, String>();
		userIndexInverse = new TreeMap<Integer, String>();
		groundSet = new HashSet<String>();
		cf_Matrix = new TreeMap<Integer, Vector<Double>>();
		cbf_Matrix = new TreeMap<Integer, Vector<Double>>();
		tag_Matrix = new TreeMap<Integer, Vector<Double>>();
		ur_Matrix = new TreeMap<Integer, Vector<Double>>();
		rept_Reduced = new Vector<Double>();
		
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
			br = new BufferedReader(new InputStreamReader(new FileInputStream(userReptVec),"UTF-8"));
			int count = 0;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				rept_Reduced.addElement(Double.valueOf(line));
				count++;								
			}
			br.close();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(groundList),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				groundSet.add(line);				
			}
			br.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getCode(int i) {
		return codeIndexInverse.get(i);
	}
	
	public String getUser(int i) {
		return userIndexInverse.get(i);
	}
	
	public int getUserRept(String uid) {
		try {
			return db.selectUserReputation(uid).getReputation();
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public void synthesis() {
		readAllMatrix();
		// calculation
		Map<Integer, Vector<Double>> cf_Matrix_multiply = MatrixUtil.multiplyMatrixDouble(cf_Matrix, multiply_cf);
		cf_Matrix.clear();
		Map<Integer, Vector<Double>> cbf_Matrix_multiply = MatrixUtil.multiplyMatrixDouble(cbf_Matrix, multiply_cbf);
		cbf_Matrix.clear();
		Map<Integer, Vector<Double>> score1 = MatrixUtil.addMatrix(cf_Matrix_multiply, cbf_Matrix_multiply);
		cf_Matrix_multiply.clear();
		cbf_Matrix_multiply.clear();
		
		Map<Integer, Vector<Double>> tag_Matrix_multiply = MatrixUtil.multiplyMatrixDouble(tag_Matrix, multiply_tag);
		tag_Matrix.clear();
		Map<Integer, Vector<Double>> score2 = MatrixUtil.addMatrix(tag_Matrix_multiply, score1);
		tag_Matrix_multiply.clear(); 
		score1.clear();
		
		Map<Integer, Vector<Double>> ur_Matrix_multiply = MatrixUtil.multiplyMatrixDouble(ur_Matrix, multiply_ur);
		ur_Matrix.clear();
		Map<Integer, Vector<Double>> score3 = MatrixUtil.addMatrix(score2, ur_Matrix_multiply);
		score2.clear(); 
		ur_Matrix_multiply.clear();
		
		/*
		Map<Integer, Vector<Double>> ut_Matrix_multiply = MatrixUtil.multiplyMatrixDouble(ut_Matrix, multiply_ut);
		ut_Matrix.clear();		
		
		Map<Integer, Vector<Double>> score4 = MatrixUtil.addMatrix(score3, ut_Matrix_multiply);
		score3.clear(); 
		ut_Matrix_multiply.clear();
		*/
		
		Map<Integer, Vector<Double>> finalScore = MatrixUtil.biasMatrix(score3, rept_Reduced, multiply_rept);
		score3.clear();
		// evaluation
		Set<String> synResultSet = new HashSet<String>();
		for(Entry<Integer, Vector<Double>> entry : finalScore.entrySet()) {
			Vector<Double> temp = entry.getValue();
			int userID = entry.getKey();
			String users = getUser(userID);
			int Ncount = 100;
			/*
			UserReputation ur;
			try {
				ur = db.selectUserReputation(users);
				if(ur != null) {
					Ncount = ur.getReputation()/25+1;
				}
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			
			
			int Nc = 0;
			for(int i = 0; i<numOfCode; ++i) {
				if(temp.get(i)>greatParam) {
					Nc++;
				}
			}
			//System.out.println(Ncount);
			int NN = Math.min(Nc, Ncount);
			NN = Math.min(NN, N);
			for(int n = 0; n<NN; ++n) {
				double max = -1.0;
				int maxIndex = -1;
				for(int j = 0; j<numOfCode; j++) {
				// select max				
					double x = temp.get(j);
					if(x >= max) {
						max = x;
						maxIndex = j;
					}				
				}
				temp.set(maxIndex, -1.0);
				String code = getCode(maxIndex);
				synResultSet.add(code+"\t"+users);
			}
		}
		
		dump(synResultSet);
		
//		evaluate(groundSet, synResultSet);

	}
	
	
	private void dump(Set<String> synResultSet) {
		// TODO Auto-generated method stub
		
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultDump), "UTF-8"));
			for(String x: synResultSet) {
				bw.write(x+"\n");
				bw.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
		
	}

	private void readAllMatrix() {
		// TODO Auto-generated method stub
		BufferedReader br;
		String line;
		try {
			// collaborative filtering
			br = new BufferedReader(new InputStreamReader(new FileInputStream(cfMatrix),"UTF-8"));
			int count = 0;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				Vector<Double> score = new Vector<Double>();				
				score.setSize(numOfCode);
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					score.set(i, Double.valueOf(sp[i]));
				}
				cf_Matrix.put(count, score);
				count++;				
			}
			br.close();
			
			// content-based filtering
			br = new BufferedReader(new InputStreamReader(new FileInputStream(cbfMatrix),"UTF-8"));
			count = 0;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				Vector<Double> score = new Vector<Double>();
				score.setSize(numOfCode);				
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					score.set(i, Double.valueOf(sp[i]));
				}
				cbf_Matrix.put(count, score);
				count++;				
			}
			br.close();
			
			// tag
			br = new BufferedReader(new InputStreamReader(new FileInputStream(tagMatrix),"UTF-8"));
			count = 0;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				Vector<Double> score = new Vector<Double>();
				score.setSize(numOfCode);
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					score.set(i, Double.valueOf(sp[i]));
				}
				tag_Matrix.put(count, score);
				count++;				
			}
			br.close();
			
			// user relation
			br = new BufferedReader(new InputStreamReader(new FileInputStream(urMatrix),"UTF-8"));
			count = 0;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				Vector<Double> score = new Vector<Double>();
				score.setSize(numOfCode);
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					score.set(i, Double.valueOf(sp[i]));
				}
				ur_Matrix.put(count, score);
				count++;				
			}
			br.close();
			
			// user tag similarity recommend
			/*
			br = new BufferedReader(new InputStreamReader(new FileInputStream(userTagMatrix),"UTF-8"));
			count = 0;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				Vector<Double> score = new Vector<Double>();
				score.setSize(numOfCode);
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					score.set(i, Double.valueOf(sp[i]));
				}
				ut_Matrix.put(count, score);
				count++;				
			}
			br.close();
			*/
			
			// reputation reduced
			br = new BufferedReader(new InputStreamReader(new FileInputStream(userReptVec),"UTF-8"));
			count = 0;
			Vector<Double> reptReduced = new Vector<Double>();
			reptReduced.setSize(numOfCode);
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				reptReduced.set(count, Double.valueOf(line));
				count++;
			}
			br.close();			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void cbfEval() {
		Map<Integer, Vector<Double>> cbfScore = new TreeMap<Integer, Vector<Double>>();
		Set<String> cbfResultSet = new HashSet<String>();
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(cbfMatrix),"UTF-8"));
			int count = 0;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				Vector<Double> score = new Vector<Double>();
				score.setSize(numOfCode);
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					score.set(i, Double.valueOf(sp[i]));
				}
				cbfScore.put(count, score);
				count++;				
			}			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Entry<Integer, Vector<Double>> entry : cbfScore.entrySet()) {
			Vector<Double> temp = entry.getValue();
			int userID = entry.getKey();
			String users = getUser(userID);
			int Ncount = 0;
			for(int i = 0; i<numOfCode; ++i) {
				if(temp.get(i)>cbfParam) {
					Ncount++;
				}
			}
			//System.out.println(Ncount);
			int NN = Math.min(Ncount, N);
			for(int n = 0; n<NN; ++n) {
				double max = -1.0;
				int maxIndex = -1;
				for(int j = 0; j<numOfCode; j++) {
				// select max				
					double x = temp.get(j);
					if(x >= max) {
						max = x;
						maxIndex = j;
					}				
				}
				temp.set(maxIndex, -1.0);
				String code = getCode(maxIndex);
				cbfResultSet.add(code+"\t"+users);
			}
		}

		dump(cbfResultSet);
		//evaluate(groundSet, cbfResultSet);
	}
	
	public void cfEval() {
		Map<Integer, Vector<Double>> cfScore = new TreeMap<Integer, Vector<Double>>();
		Set<String> cfResultSet = new HashSet<String>();
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(cfMatrix),"UTF-8"));
			int count = 0;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				Vector<Double> score = new Vector<Double>();
				score.setSize(numOfCode);
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					score.set(i, Double.valueOf(sp[i]));
				}
				cfScore.put(count, score);
				count++;				
			}			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Entry<Integer, Vector<Double>> entry : cfScore.entrySet()) {
			Vector<Double> temp = entry.getValue();
			int userID = entry.getKey();
			String users = getUser(userID);
			int Ncount = 0;
			for(int i = 0; i<numOfCode; ++i) {
				if(temp.get(i)>cfParam) {
					Ncount++;
				}
			}
			//System.out.println(Ncount);
			int NN = Math.min(Ncount, N);
			for(int n = 0; n<NN; ++n) {
				double max = -1.0;
				int maxIndex = -1;
				for(int j = 0; j<numOfCode; j++) {
				// select max				
					double x = temp.get(j);
					if(x >= max) {
						max = x;
						maxIndex = j;
					}				
				}
				temp.set(maxIndex, -1.0);
				String code = getCode(maxIndex);
				cfResultSet.add(code+"\t"+users);
			}
		}

		dump(cfResultSet);
		
//		evaluate(groundSet, cfResultSet);
		
	}	
	
	
	public void tagEval() {
		Map<Integer, Vector<Double>> tagScore = new TreeMap<Integer, Vector<Double>>();
		Set<String> tagResultSet = new HashSet<String>();
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(tagMatrix),"UTF-8"));
			int count = 0;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				Vector<Double> score = new Vector<Double>();
				score.setSize(numOfCode);
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					score.set(i, Double.valueOf(sp[i]));
				}
				tagScore.put(count, score);
				count++;				
			}			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Entry<Integer, Vector<Double>> entry : tagScore.entrySet()) {
			Vector<Double> temp = entry.getValue();
			int userID = entry.getKey();
			String users = getUser(userID);
			int Ncount = 0;
			for(int i = 0; i<numOfCode; ++i) {
				if(temp.get(i)>tagParam) {
					Ncount++;
				}
			}
			//System.out.println(Ncount);
			int NN = Math.min(Ncount, N);
			for(int n = 0; n<NN; ++n) {
				double max = -1.0;
				int maxIndex = -1;
				for(int j = 0; j<numOfCode; j++) {
				// select max				
					double x = temp.get(j);
					if(x >= max) {
						max = x;
						maxIndex = j;
					}				
				}
				temp.set(maxIndex, -1.0);
				String code = getCode(maxIndex);
				tagResultSet.add(code+"\t"+users);
			}
		}

		dump(tagResultSet);
		evaluate(groundSet, tagResultSet);
	}
	
	public void urEval() {
		Map<Integer, Vector<Double>> urScore = new TreeMap<Integer, Vector<Double>>();
		Set<String> urResultSet = new HashSet<String>();
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(urMatrix),"UTF-8"));
			int count = 0;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				Vector<Double> score = new Vector<Double>();
				score.setSize(numOfCode);
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					score.set(i, Double.valueOf(sp[i]));
				}
				urScore.put(count, score);
				count++;				
			}			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Entry<Integer, Vector<Double>> entry : urScore.entrySet()) {
			Vector<Double> temp = entry.getValue();
			int userID = entry.getKey();
			String users = getUser(userID);
			int Ncount = 0;
			for(int i = 0; i<numOfCode; ++i) {
				if(temp.get(i)>urParam) {
					Ncount++;
				}
			}
			//System.out.println(Ncount);
			int NN = Math.min(Ncount, N);
			for(int n = 0; n<NN; ++n) {
				double max = -1.0;
				int maxIndex = -1;
				for(int j = 0; j<numOfCode; j++) {
				// select max				
					double x = temp.get(j);
					if(x >= max) {
						max = x;
						maxIndex = j;
					}				
				}
				temp.set(maxIndex, -1.0);
				String code = getCode(maxIndex);
				urResultSet.add(code+"\t"+users);
			}
		}

		dump(urResultSet);

		//evaluate(groundSet, urResultSet);
	}
	
	public void evaluate(String ground, String result) {
		Set<String> groundSet = new HashSet<String>();
		Set<String> resultSet = new HashSet<String>();
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(ground),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				groundSet.add(line);				
			}			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(result),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				resultSet.add(line);				
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int count = 0;
		for(String x: resultSet) {
			if(groundSet.contains(x))
				count++;
		}
		
		System.out.println("Precision: "+(double)count/resultSet.size()*100.0+"%");
		System.out.println("Recall: "+(double)count/groundSet.size()*100.0+"%");
	}
	
	public void evaluate(Set<String> groundSet, Set<String> resultSet) {
		int count = 0;
		for(String x: resultSet) {
			if(groundSet.contains(x))
				count++;
		}
		System.out.println("#prediction = "+resultSet.size());
		System.out.println("#correct = "+count);
		double precision = (double)count/resultSet.size()*100.0;
		double recall = (double)count/groundSet.size()*100.0;
		System.out.println("Precision: "+precision+"%");
		System.out.println("Recall: "+recall+"%");
		double f1 = 2*precision*recall/(precision+recall);
		System.out.println("F1-Score: "+f1+"%");
	}
	
	

}
