package preparation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import crawler.GetStackExchange;
import entity.Code;
import utils.DBTools;
import utils.UtilConstant;

public class GenerateTestData implements UtilConstant {
	
	DBTools dbt;
	GetStackExchange getTest;
	Set<String> userSet;
	Set<String> questionSet;

	// when question id file exists!
	public void generateTestCode(String fileName, int choice) {
		File file = new File(fileName);
		if(!file.exists()) {
			System.out.println("No file read in");
			return;
		}
		String lines = new String();
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			while(true) {
				lines = br.readLine();
				if(lines == null)
					break;
				String[] sp = lines.split("\t");
				if(dbt.selectCodeTest(sp[0])!=null) {
					System.out.println("Ignore repeated test...");
					continue;
				}
				String tempID = "/questions/"+sp[0];
				String tempUser = sp[1];
				getTest.dealSingleItem(tempID, tempUser, choice);
				double x = Math.random();
				double y = x*5439;
				long z = (int) y;
				try {
					Thread.sleep(z);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			br.close();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			
		}
	}
	
	public void genDevSet() {
		BufferedReader br;
		BufferedWriter bw;
		int devNum = 0;
		Code cTrain;
		Code cDev;
		Vector<String> vTrain;
		Vector<String> vDev;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(codeIDList),"UTF-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(devList),"UTF-8"));
			String lines;
			while(true) {
				lines = br.readLine();
				//System.out.println("numCount = " + nullCount);
				if(lines == null)
					break;
				String[] sp = lines.split("\t");
				cTrain = dbt.selectCode(sp[0].trim());
				cDev = dbt.selectCodeDev(sp[0].trim());
				vTrain = cTrain.getAnswerList();
				vDev = cDev.getAnswerList();
//				System.out.println(vTrain);
//				System.err.println(vDev);
				if(vDev==null) {
					// nullCount++;
					// System.out.println("numCount = " + nullCount);
				}
				for(String x: vDev) {
					if(x.length()>=1 && !vTrain.contains(x)) {
						if(!x.equals(cTrain.getUserID()))
						bw.write(sp[0]+"\t"+x+"\n");
						++devNum;
					}
				}
				bw.flush();
			}
			System.out.println("#dev = "+devNum);
			br.close();
			bw.close();
			
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
	
	
	public void genGroundSet() {
		// traverse and compare train and test set
		BufferedReader br;
		BufferedWriter bw;
		int groundNum = 0;
		Code cDev;
		Code cTest;
		Vector<String> vDev;
		Vector<String> vTest;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(codeIDList),"UTF-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(groundList),"UTF-8"));
			String lines;
			while(true) {
				lines = br.readLine();
				//System.out.println("numCount = " + nullCount);
				if(lines == null)
					break;
				String[] sp = lines.split("\t");
				cDev = dbt.selectCodeDev(sp[0].trim());
				cTest = dbt.selectCodeTest(sp[0].trim());
				vDev = cDev.getAnswerList();
				vTest = cTest.getAnswerList();
//				System.out.println(vTrain);
//				System.err.println(vTest);
				if(vTest==null) {
					// nullCount++;
					// System.out.println("numCount = " + nullCount);
				}
				for(String x: vTest) {
					if(x.length()>=1 && !vDev.contains(x)) {
						if(!x.equals(cDev.getUserID()))
						bw.write(sp[0]+"\t"+x+"\n");
						++groundNum;
					}
				}
				bw.flush();
			}
			System.out.println("#ground = "+groundNum);
			br.close();
			bw.close();
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	public GenerateTestData() {
		super();
		dbt = new DBTools();
		getTest = new GetStackExchange();
		userSet = new HashSet<String>();
		questionSet = new HashSet<String>();
	}


}
