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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import entity.Code;
import preparation.Dictionary;
import utils.DBTools;
import utils.UtilConstant;

public class CFPreparation implements UtilConstant {
	
	Dictionary dict;
	Map<String, Integer> userIndexMap;
	Map<String, Integer> codeIndexMap;
	Map<Integer, Vector<Integer>> matrix;
	DBTools dbtool;
	int count;
	Map<Integer, Integer> checkMap;
	
	Map<Integer, Vector<Double>> simMatrix;
	
	public CFPreparation() {
		super();
		dict = new Dictionary();
		dbtool = new DBTools();
		userIndexMap = new TreeMap<String, Integer>();
		codeIndexMap = new TreeMap<String, Integer>();
		matrix = new TreeMap<Integer, Vector<Integer>>();
		simMatrix = new TreeMap<Integer, Vector<Double>>();
		checkMap = new TreeMap<Integer, Integer>();
		count = 0;
		
	}
	
	public void algPre() {
				
		/*  0. 
		 *  generate dictionary for user and code, with index + id
		 */
//		dict.generateIDMap();
		readIndexMap();
		/*  1. 
		 *  read Map information into program, then construct the matrix
		 */
		constructMatrix();
		// readMatrix();
		/*  2. 
		 *  generate similarity Matrix
		 */
		constructSimMatrix();		
		
	}
	
	public void constructSimMatrix() {
		// TODO Auto-generated method stub
		for(int i = 0; i<numOfUser; ++i) {
			Vector<Double> allZeros = new Vector<Double>(numOfUser);
			for(int j = 0; j<numOfUser; ++j) {
				allZeros.add(0.0);
			}
			simMatrix.put(i, allZeros);
		}
		
		for(int i = 0; i<numOfUser; ++i) {
			for(int j = i; j<numOfUser; ++j) {
				double tempVal = getCosineSim(matrix.get(i), matrix.get(j));
				setSimValue(i, j, tempVal);
				setSimValue(j, i, tempVal);				
			}
			System.out.println(i);
		}
		
		// write Similarity Matrix
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(simMatrixFile),"UTF-8"));
			for(Entry<Integer, Vector<Double>> entry: simMatrix.entrySet()) {
				Vector<Double> inner = entry.getValue();
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
	}
	
	public void readSimMatrix() {
		// init
		for(int i = 0; i<numOfUser; ++i) {
			Vector<Double> allZeros = new Vector<Double>(numOfUser);
			for(int j = 0; j<numOfUser; ++j) {
				allZeros.add(0.0);;
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

	public void readMatrix() {
		// init
		matrix.clear();
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
					matrix.get(lineCount).set(i, Integer.valueOf(sp[i]));
				}
				System.out.println(lineCount);
				lineCount++;
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public void constructMatrix() {
		// initial matrix with all 0s
		for(int i = 0; i<numOfUser; ++i) {
			Vector<Integer> allZeros = new Vector<Integer>();
			allZeros.setSize(numOfCode);
			for(int j = 0; j<numOfCode; ++j) {
				allZeros.set(j, 0);
			}
			matrix.put(i, allZeros);
		}
		// from database of training data, set 2 and 1
		for(Entry<String, Integer> entry: codeIndexMap.entrySet()) {
			String tempId = entry.getKey();
			int n = entry.getValue();
			Code c;
			c = dbtool.selectCode(tempId);
			int mPublisher = userIndexMap.get(c.getUserID());
			setValue(mPublisher, n, 2);
			Vector<String> followList = c.getAnswerList();
			if(followList.size()>=1) {
				for(String x: followList) {	
					if(x.equals(c.getUserID())) {
						setValue(mPublisher, n, 3);
					}
					else {
					int mFollow = userIndexMap.get(x);
					setValue(mFollow, n, 1);	
					}
				}
			}	
		}
		
		// write matrix
		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(matrixFile),"UTF-8"));
			for(Entry<Integer, Vector<Integer>> entry: matrix.entrySet()) {
				Vector<Integer> inner = entry.getValue();
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
		
		// System.out.println(count);
		
	} 
	
	
	private void setValue(int m, int n, int val) {
		/*
		if(val == 2) {
			++count;
			System.out.println(m+" "+n+" :"+val);
			checkMap.put(m, n);
		}
		*/
		Vector<Integer> temp = matrix.get(m);
//		System.out.println(temp);
		temp.set(n, val);
		matrix.put(m, temp);		
	}
	
	private void setSimValue(int m, int n, double val) {
		Vector<Double> temp = simMatrix.get(m);
		temp.set(n, val);
		simMatrix.put(m, temp);		
	}
	
	private int getValueMN(int m, int n) {
		return matrix.get(m).get(n);
	}
	
	@SuppressWarnings("unused")
	private void check() {
		int c = 0;
		for(Entry<Integer, Integer> entry: checkMap.entrySet()) {
			int x = getValueMN(entry.getKey(), entry.getValue());
			c++;
			if(x!=2) {
				System.out.println(entry.getKey()+" "+entry.getValue()+": "+x);
			}
		} 
		System.out.println(c);
	} 
	
	private double getCosineSim(Vector<Integer> vector, Vector<Integer> vector2) {
		if(vector.size() != vector2.size())
			return -0.0;
		else {
			int sz = vector.size();
			int sum = 0;
			int suma = 0;
			int sumb = 0;
			for(int i = 0; i<sz; i++) {
				if(vector.get(i) != 0) {
					sum+=vector.get(i)*vector2.get(i);
					suma+=vector.get(i)*vector.get(i);
				}
				if(vector2.get(i) != 0) {
					sumb+=vector2.get(i)*vector2.get(i);
				}
			}
			if(suma==0||sumb==0||sum==0)
				return 0.0;
			else
				return (double) sum/(double) Math.sqrt((double)suma)/ (double) Math.sqrt((double)sumb);
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
	
	public Map<String, Vector<String> > preInit() {
		BufferedReader br;
		HashMap<String, Vector<String>> mapRet = new HashMap<String, Vector<String> >();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(groundList),"UTF-8"));
			String line;
			while(true) {
				line = br.readLine();
				if(line == null) 
					break;
				String[] sp = line.split("\t");
				String questionID = sp[0];
				String userID = sp[1];
				if(mapRet.containsKey(questionID)) {
					Vector<String> temp = mapRet.get(questionID);
					temp.add(userID);
					mapRet.put(questionID, temp);
				}
				else {
					Vector<String> temp2 = new Vector<String>();
					temp2.add(userID);
					mapRet.put(questionID, temp2);
				}
				
			}
		
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
		return mapRet;
	}
	
	
	

}
