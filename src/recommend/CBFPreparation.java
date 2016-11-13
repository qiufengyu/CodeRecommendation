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

public class CBFPreparation implements UtilConstant {
	
	Map<String, Vector<Double>> docMap;
	Map<String, Vector<Double>> wordMap;
	
	Map<Integer, Vector<Double>> simMap;
	Map<String, Integer> codeIndexMap;
	
	public CBFPreparation() {
		super();
		docMap = new TreeMap<String, Vector<Double>>();
		wordMap = new TreeMap<String, Vector<Double>>();
		simMap = new TreeMap<Integer, Vector<Double>>();
		codeIndexMap = new TreeMap<String, Integer>();
		readWordMap();
	}
	
	public void CBFPre() {
		readCodeIndexMap();
		
		readWordMap();
		generateDocMap();
		readDocMap();
		
		calculateCodeSim();
	}
	
	private void calculateCodeSim() {
		for(Entry<String, Vector<Double>> entry : docMap.entrySet()) {
			String x = entry.getKey();
			int index = codeIndexMap.get(x);
			Vector<Double> vout = entry.getValue();
			Vector<Double> simv = new Vector<Double>();
			// initial simv for code index
			for(int i = 0; i<numOfCode; i++) {
				simv.addElement(0.0);
			}
			simMap.put(index, simv);
			for(Entry<String, Vector<Double>> entry2 : docMap.entrySet()) {
				String y = entry2.getKey();
				// System.out.println(y);
				int index2 = codeIndexMap.get(y);
				Vector<Double> vin = entry2.getValue();
				setSimMap(index, index2, contentSimilarity(vout, vin));				
			}
		}
		
		BufferedWriter bw;
		BufferedWriter bwpart;
		int count = 0;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(titleSimMatrix),"UTF-8"));
			bwpart = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(titleSimMatrixpart),"UTF-8"));
			for(Entry<Integer, Vector<Double>> entry3: simMap.entrySet()) {
				Vector<Double> inner = entry3.getValue();
				if(count<=50) {
					for(int j = 0; j<inner.size()-1; ++j) {
						bw.write(inner.get(j)+" ");
						bwpart.write(inner.get(j)+" ");
					}
					bw.write(inner.get(inner.size()-1)+"\n");				
					bwpart.write(inner.get(inner.size()-1)+"\n");
				}
				else {
					for(int j = 0; j<inner.size()-1; ++j) {
						bw.write(inner.get(j)+" ");
					}
					bw.write(inner.get(inner.size()-1)+"\n");
				}
				bw.flush();
				bwpart.flush();
				count++;
			}			
			bw.close();
			bwpart.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	private void setSimMap(int i, int j, double val) {
		Vector<Double> x = simMap.get(i);
		x.set(j, val);
		simMap.put(i, x);
	}

	private void readDocMap() {
		// TODO Auto-generated method stub
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(titleVector),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null) {
					break;
				}
				String[] sp = line.split("\t");
				String id = sp[0];
				String rawVec = sp[1];
				String[] spvec = rawVec.split(" ");
				Vector<Double> vec = new Vector<Double>();
				for(String x: spvec) {
					vec.addElement(Double.valueOf(x));
				}
				docMap.put(id, vec);				
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private double contentSimilarity(Vector<Double> v1, Vector<Double> v2) {
		int sz = v1.size();
		if(v1.size() != v2.size())
			return 0.0;
		else {
			double sumv1 = 0.0;
			double sumv2 = 0.0;
			double sumv12 = 0.0;
			for(int i = 0; i<sz; ++i) {
				double x = v1.get(i);
				double y = v2.get(i);
				sumv12 += (x*y);
				sumv1 += (x*x);
				sumv2 += (y*y);
			}
			return sumv12/Math.sqrt(sumv1)/Math.sqrt(sumv2);
		}
	}
	
	private void readCodeIndexMap() {
		BufferedReader br;
		String line;
		try {
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
	
	public void generateDocMap() {
		// add all word vector
		BufferedReader br;
		BufferedWriter bw;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(titleTrain),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null) {
					break;
				}
				String[] sp = line.split("\t");
				String id = sp[0];
				String sen = UtilConstant.sentencePre(sp[1]);
				String[] sensp = sen.split(" ");
				int count = 0;
				Vector<Double> v = new Vector<Double>();
				for(int j = 0; j<100; ++j) {
					v.addElement(0.0);
				}
				for(String x: sensp) {
					if(x.length()>=1) {
						++count;
						if(wordMap.get(x) != null)
							v = addVector(v, wordMap.get(x));
					}
				}
				Vector<Double> v1 = new Vector<Double>();
				for(int j = 0; j<100; ++j) {
					v1.addElement(v.get(j)/(double) count);
				}
				docMap.put(id, v1);
			}
			br.close();
			
			// write into file
			
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(titleVector),"UTF-8"));
			for(Entry<String, Vector<Double>> entry : docMap.entrySet()) {
				bw.write(entry.getKey()+"\t");
				Vector<Double> docv = entry.getValue();
				for(int i = 0; i<99; ++i) {
					bw.write(docv.get(i)+" ");
				}
				bw.write(docv.get(99)+"\n");
				bw.flush();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readWordMap() {
		// TODO Auto-generated method stub
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(titleWord2Vec),"UTF-8"));
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				String[] sp = line.split(" ");
				int sz = sp.length;
				String word = sp[0];
				Vector<Double> v = new Vector<Double>();
				for(int i = 1; i<sz; i++) {
					v.addElement(Double.valueOf(sp[i]));
				}
				wordMap.put(word, v);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
		
	private Vector<Double> addVector(Vector<Double> v1, Vector<Double> v2) {
		Vector<Double> v = new Vector<Double>();
		int sz = v1.size();
		if(v1.size()!=v2.size()) 
			return null;		
		else {
			for(int i = 0; i<sz; ++i) {
				v.addElement(v1.get(i)+v2.get(i));
			}
			return v;
		}
	}
	

}
