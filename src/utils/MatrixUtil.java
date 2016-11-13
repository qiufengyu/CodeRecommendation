package utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

public interface MatrixUtil {
	
	public static Map<Integer, Vector<Double>> addMatrix(
			Map<Integer, Vector<Double>> matrixA, 
			Map<Integer, Vector<Double>> matrixB) 
	{
		Map<Integer, Vector<Double>> resultMatrix = new TreeMap<Integer, Vector<Double>>();
		for(Entry<Integer, Vector<Double>> entry : matrixA.entrySet()) {
			int index = entry.getKey();
			Vector<Double> tempA = entry.getValue();
			Vector<Double> tempB = matrixB.get(index);
			Vector<Double> tempR = addArray(tempA, tempB);
			resultMatrix.put(index, tempR);
		}
		return resultMatrix;		
	}
	
	public static Map<Integer, Vector<Double>> multiplyMatrixDouble(
			Map<Integer, Vector<Double>> matrixA, 
			double param) 
	{
		Map<Integer, Vector<Double>> resultMatrix = new TreeMap<Integer, Vector<Double>>();
		for(Entry<Integer, Vector<Double>> entry : matrixA.entrySet()) {
			int index = entry.getKey();
			Vector<Double> tempA = (Vector<Double>) entry.getValue(); 
			Vector<Double> tempR = new Vector<Double>();
			for(int i = 0; i<tempA.size(); ++i) {
				tempR.add(param*(double)tempA.get(i));
			}
			resultMatrix.put(index, tempR);
		}
		return resultMatrix;		
	}
	
	public static Map<Integer, Vector<Double>> multiplyMatrixInt(
			Map<Integer, Vector<Integer>> matrixA, 
			double param) 
	{
		Map<Integer, Vector<Double>> resultMatrix = new TreeMap<Integer, Vector<Double>>();
		for(Entry<Integer, Vector<Integer>> entry : matrixA.entrySet()) {
			int index = entry.getKey();
			Vector<Integer> tempA = (Vector<Integer>) entry.getValue();
			Vector<Double> tempR = new Vector<Double>();
			for(int i = 0; i<tempA.size(); ++i) {
				tempR.add(param*(double)tempA.get(i));
			}
			resultMatrix.put(index, tempR);
		}
		return resultMatrix;

	}
	
	
	static Vector<Double> addArray(
			Vector<Double> tempA,
			Vector<Double> tempB) 
	{
		if(tempA.size()!=tempB.size()) 
			return null;
		int length = tempA.size();
		Vector<Double> resultArray = new Vector<Double>();
		for(int i = 0; i<length; ++i) {
			resultArray.addElement(tempA.get(i)+tempB.get(i));
		}
		return resultArray;		
	}
	
	public static Map<Integer, Vector<Double>> biasMatrix(
			Map<Integer, Vector<Double>> matrixA, 
			Vector<Double> rept_Reduced, double param)
	{
		Map<Integer, Vector<Double>> resultMatrix = new TreeMap<Integer, Vector<Double>>();
		for(Entry<Integer, Vector<Double>> entry : matrixA.entrySet()) {
			int index = entry.getKey();
			Vector<Double> tempA = entry.getValue();
			Vector<Double> tempR = new Vector<Double>();
			for(int i = 0; i<tempA.size(); ++i) {
				tempR.addElement(tempA.get(i)-rept_Reduced.get(i)*param);
			}
			resultMatrix.put(index, tempR);
		}
		return resultMatrix;		
	}
	
	public static void dumpMatrix(Map<Integer, Vector<Double>> matrix, String fileName) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
			for(Entry<Integer, Vector<Double>> entry : matrix.entrySet()) {
				Vector<Double> inner = entry.getValue();
				int sz = inner.size();
				for(int i = 0; i<sz-1; ++i) {
					bw.write(inner.get(i)+" ");
				}
				bw.write(inner.get(sz-1)+"\n");
				bw.flush();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
	public static Vector<Double> normlize(Vector<Double> v) {
		Vector<Double> vv = new Vector<Double>();
		vv.setSize(v.size());
		double maxD = -9999.0;
		double minD = 9999.0;
		for(double x : v) {
			if(x>maxD)
				maxD = x;
			if(x<minD)
				minD = x;
		}
		
		if ((maxD - minD) > 1e-8) {
			for(int i = 0; i<v.size(); i++) {
				vv.set(i, (v.get(i)-minD)/(maxD-minD));
			}
		}
		else {
			for(int i = 0; i<v.size(); i++) {
				vv.set(i, 0.0);
			}
		}
		return vv;
		
		
	}

}
