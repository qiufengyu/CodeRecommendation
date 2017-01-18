package recommend;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import crawler.GetStackExchange;
import crawler.MainCrawler;
import evaluation.MergeEvaluation;
import evaluation.RecommendEvaluation;
import preparation.DataPreparation;
import preparation.GenerateTestData;
import preparation.TagExtract;
import utils.DBTools;
import utils.MatrixUtil;
import utils.UtilConstant;

public class Recommend implements UtilConstant, MatrixUtil {
	

	public static void main(String[] args) {
		
		long t1 = System.currentTimeMillis();		

		
//		MainCrawler mc = new MainCrawler();
		// get from page 1 to 550
//		mc.runCrawler(1, 590, 732);
		
//		revise();
		
		testPreparatiion();
		
		RecommendEvaluation re = new RecommendEvaluation();
//		re.cfEval();
//		re.cbfEval();
//		re.urEval();
//		re.tagEval();
//		re.utEval();
		re.synthesis();
		
	
//		RecommendClean rc = new RecommendClean();
//		rc.evaluate();
		
		MergeEvaluation me = new MergeEvaluation();
		me.mergeEval();
//		calAverage();		

		long t2 = System.currentTimeMillis();
		System.out.println("Run Time: " + (t2-t1)/1000 + " seconds");
		
	}
	
	public static void calAverage() {
		Map<Integer, Vector<Integer>> matrix = new TreeMap<Integer, Vector<Integer>>();
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
				lineCount++;
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int allc = 0;
		for(Entry<Integer, Vector<Integer>> entry: matrix.entrySet()) {
			for(int x: entry.getValue()) {
				if(x != 0) allc++;
			}
		}
		
		System.out.println("average = "+(double)allc/matrix.size());
	}

	@SuppressWarnings("unused")
	private static void revise() {
		Map<Integer, Vector<Double>> interMap = new TreeMap<Integer, Vector<Double>>();
		// TODO Auto-generated method stub
		BufferedReader br;
		String line;
		// user tag similarity recommend
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(userTagMatrix),"UTF-8"));
			int count = 0;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;	
				Vector<Double> score = new Vector<Double>();
				score.setSize(numOfCode);
				String[] sp = line.split(" ");
				for(int i = 0; i<sp.length; ++i) {
					double x = Double.valueOf(sp[i]);
					if(x<1e-4) {
						score.set(i, 0.0); 
					}
					else {
						score.set(i, x); 
					}
				}
				interMap.put(count, score);
				count++;				
			}
			MatrixUtil.dumpMatrix(interMap, userTagMatrix);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private static void testPreparatiion() {
		// TODO Auto-generated method stub
		
//		long t1 = System.currentTimeMillis();	

		// datasetClean();
		
		/* 0. 
		 *  Data for train and test ready
		 *  Generate tag files
		 */
//		DataPreparation dp = new DataPreparation();
//		dp.dataPre();
		
//		TagExtract te = new TagExtract();
//		te.generateTagSim();
//		UserTagRecommend utr = new UserTagRecommend();
//		utr.constructTagSimMatrix();
		

		/* 1.
		 *  Collaborative Filtering Algorithm
		 */
//		CFPreparation cfpre = new CFPreparation();
//		cfpre.algPre();
		
//		CFRecommend cfRec = new CFRecommend();
//		cfRec.CFAlg();
		
		/*  2. 
		 *  Content-based Filtering
		 */
//		CBFPreparation cbfpre = new CBFPreparation();
//		cbfpre.CBFPre();
		
//		CBFRecommend cbfRec = new CBFRecommend();
//		cbfRec.CBFAlg();
		
		/*  2.5
		 *  Content-based Filtering, using code documents
		 */
		
//		ContentRecommend cntRec = new ContentRecommend();
//		cntRec.CBFAlg();
		
		
		/*  3. 
		 *  Tag Similarity compare
		 */
//		TagBasedRecommend tagbasedRec = new TagBasedRecommend();
//		tagbasedRec.TagBasedAlg();
		
		/*  4.
		 *  CF by user tag similarity
		 */
//		UserTagRecommend usertagRec = new UserTagRecommend();
//		usertagRec.constructTagSimMatrix();
//    	usertagRec.runUserTagRecommend();
		
		/*  5. 
		 *  User Relation Recommend
		 */
		
//		UserRelationRecommend urRec = new UserRelationRecommend();
//		urRec.URAlg();
		
		/*  6. 
		 *  User Reputation Reduced Recommend
		 */
//		UserReptRecommend userReptRec = new UserReptRecommend();
//		userReptRec.UserReptAlg();
		
//		long t2 = System.currentTimeMillis();
//		System.out.println("Run Time: " + (t2-t1)/1000 + " seconds");
		
	}

	private static void datasetClean() {
		// TODO Auto-generated method stub
		GetStackExchange gg = new GetStackExchange();
		DBTools dbt = new DBTools();
		ResultSet rs;
		try {
			rs = dbt.selectCodeALL();
			while(rs.next()) {				
				if(rs.getString(3).length()<=2) {
					String id = rs.getString(1);
					dbt.deleteCode(id);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
