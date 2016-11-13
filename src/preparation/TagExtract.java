package preparation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import entity.Code;
import entity.UserReputation;

import java.util.Map.Entry;

import utils.DBTools;
import utils.UtilConstant;

public class TagExtract implements UtilConstant {
	
	Set<String> codeTagSet;
	Set<String> userTagSet;	
	Set<String> allTagSet;
	DBTools data;
	
	Map<String, Integer> codeIndexMap;
	Map<String, Integer> userIndexMap;
	Map<Integer, String> codeIndexInverse;
	Map<Integer, String> userIndexInverse;
	
	Map<String, Integer> tagIndexMap;
	Map<Integer, String> tagIndexInverse;
	
	Map<Integer, Vector<Integer>> userTagMatrix;
	Map<Integer, Vector<Double>> userTagSim;
	
	Map<Integer, Vector<Integer>> codeTagMatrix;
	Map<Integer, Vector<Double>> codeTagSim;
	
	public TagExtract() {
		super();
		// TODO Auto-generated constructor stub
		codeTagSet = new TreeSet<String>();
		userTagSet = new TreeSet<String>();
		allTagSet = new TreeSet<String>();
		
		userIndexMap = new TreeMap<String, Integer>();
		codeIndexMap = new TreeMap<String, Integer>();
		codeIndexInverse = new TreeMap<Integer, String>();
		userIndexInverse = new TreeMap<Integer, String>();
		tagIndexMap = new TreeMap<String, Integer>();
		tagIndexInverse = new TreeMap<Integer, String>();

		data = new DBTools();
		
		userTagMatrix = new TreeMap<Integer, Vector<Integer>>();
		codeTagMatrix = new TreeMap<Integer, Vector<Integer>>();
		userTagSim = new TreeMap<Integer, Vector<Double>>();
		codeTagSim = new TreeMap<Integer, Vector<Double>>();		
		
		readIndexMap();
		readTags();
		
	}
	
	
	public void generateTagSim() {
		for(int i = 0; i<numOfTag; i++) {
			Vector<Integer> vl = new Vector<Integer>();
			vl.setSize(numOfCode);
			for(int j = 0; j<numOfCode; ++j)
				vl.set(j, 0);
			codeTagMatrix.put(i, vl);
		}
		try {
			for(Entry<String, Integer> entryCode : codeIndexMap.entrySet()) {
				String codeID = entryCode.getKey();
				// System.out.println(userID);
				Code c = data.selectCode(codeID);						
				if(null != c) {
					int indexj = codeIndexMap.get(c.getQuestionID());
					Vector<String> v = c.getTags();
					for(String x: v) {
						int indexi = tagIndexMap.get(x);
						codeTagMatrix.get(indexi).set(indexj, 1);
					}					
				}
			}
			
			// initial
			for(int i = 0; i<numOfTag; ++i) {
				Vector<Double> allZeros = new Vector<Double>(numOfTag);
				for(int j = 0; j<numOfTag; ++j) {
					allZeros.add(0.0);
				}				
				codeTagSim.put(i, allZeros);
			}
			
			// calculate
			for(int i = 0; i<numOfTag; ++i) {
				for(int j = i; j<numOfTag; ++j) {
					double tempVal = getCosineSim(codeTagMatrix.get(i), codeTagMatrix.get(j));
					setSimValue(i, j, tempVal);
					setSimValue(j, i, tempVal);				
				}
				System.out.println("tag " +(1+i));
			}
			
			// write Similarity Matrix
			BufferedWriter bw;
			
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tagSim),"UTF-8"));
			for(Entry<Integer, Vector<Double>> entry: codeTagSim.entrySet()) {
				Vector<Double> inner = entry.getValue();
				for(int i = 0; i<numOfTag-1; ++i) {
					bw.write(inner.get(i)+" ");
				}
				bw.write(inner.get(numOfTag-1)+"\n");
				bw.flush();
			}
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	
	private void readTags() {
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
	
	private void generateTags() {
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(userTagDictionary),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] sp = line.split("\t");
				allTagSet.add(sp[0]);				
			}
			br.close();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(codeTagDictionary),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] sp = line.split("\t");
				allTagSet.add(sp[0]);				
			}
			br.close();
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tagDictionary), "utf-8"));
			int i = 0;
			for(String x : allTagSet) {
				bw.write(x+"\t"+i+"\n");
				i++;			
				bw.flush();
			}
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void genUserTagList() {
		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userTagDictionary), "utf-8"));
			
			for(Entry<String, Integer> entryUser : userIndexMap.entrySet()) {
				String userID = entryUser.getKey();
				// System.out.println(userID);
				UserReputation u = data.selectUserReputation(userID);
				if(null != u) {
					Vector<String> v = u.getUserTags();
					userTagSet.addAll(v);
					allTagSet.addAll(v);
				}
			}
			int i = 0;
			for(String x : userTagSet) {
				bw.write(x+"\t"+i+"\n");
				i++;
				bw.flush();
			}
			bw.close();
			
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.err.println("#userTags = "+userTagSet.size());
			System.err.println("#allTags = "+allTagSet.size());
		}
		
	}
	
	private void genCodeTagList() {
		
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(codeTagDictionary), "utf-8"));
			
			for(Entry<String, Integer> entryUser : codeIndexMap.entrySet()) {
				String codeID = entryUser.getKey();
				Vector<String> v = data.selectCode(codeID).getTags();
				codeTagSet.addAll(v);
				allTagSet.addAll(v);
			}
			int i = 0;
			for(String x : codeTagSet) {
				bw.write(x+"\t"+i+"\n");
				i++;
				bw.flush();
			}
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.err.println("#codeTags = "+codeTagSet.size());
			System.err.println("#allTags = "+allTagSet.size());
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
	
	
		
	public Set<String> getCodeTagSet() {
		return codeTagSet;
	}
	public void setCodeTagSet(Set<String> codeTagSet) {
		this.codeTagSet = codeTagSet;
	}
	public Set<String> getUserTagSet() {
		return userTagSet;
	}
	public void setUserTagSet(Set<String> userTagSet) {
		this.userTagSet = userTagSet;
	}
	

	private void setSimValue(int m, int n, double val) {
		Vector<Double> temp = codeTagSim.get(m);
		temp.set(n, val);
		codeTagSim.put(m, temp);		
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
	
	
	

}
