package preparation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import crawler.MainCrawler;
import entity.Code;
import utils.DBTools;
import utils.UtilConstant;

public class DataPreparation implements UtilConstant {
	
	MainCrawler crawler;
	GenerateTrainData gtrain;
	GenerateTestData gtest;
	Dictionary dict;
	DBTools dbt;
	
	Vector<String> generateD;
	
	
	public void dataPre() {
		

//		gtrain.deleteFromDoc();
		
		/*  0.
		 *  Craweler for code - update training data
		 */		
//		crawler.runCrawler("week", 1);
		
		
		/*  1. 
		 *  genenate code+userid for generate test ids
		 */
//		gtrain.genCodeIDList();
		
		/*  2. 
		 *  generate dev and test code dataset
		 */		
//		gtest.generateTestCode(codeIDList, 2);
//		gtest.generateTestCode(codeIDList, 3);
		
		/*  3. 
		 *  generate user id for preparing reputation information
		 */		
//		gtrain.genUserIDList();
		
//		dict.generateIDMap();

		/*  4. 
		 *  get all user's information in train and test set
		 */
//		gtrain.generateUserReptDataset();		
		
		/*  5. 
		 *  generate ground set: questionID + "\t" + userID
		 */
		gtest.genGroundSet();
//		gtest.genDevSet();
		
		/*  6. 
		 *  generate all code title word vector
		 */
		// gtrain.trainWord2Vec();
//		gtrain.generateTrainTitles();
		
		/*  7. 
		 *  generate relation table
		 */
//		gtrain.createUserRelation();
		
//		 filt();
		
	}
	
	public DataPreparation() {
		super();
		crawler = new MainCrawler();
		gtrain = new GenerateTrainData();
		gtest = new GenerateTestData();
		dict = new Dictionary();
//		generateD = new Vector<String>();
//		generateDD();
//		dbt = new DBTools();
	}
	
	public void generateDD() {
		
	}
	
	public void filt() {
		ResultSet rs;
		try {
			rs = dbt.selectCodeALL();
			while(rs.next()) {
				String id = rs.getString(1);
				Code c = dbt.selectCode(id);
				if(generateD.contains(c.getUserID())) {
					System.err.println(id+"\t"+c.getUserID()+"\tUser ID");
				}
				Vector<String> ca = c.getAnswerList();
				for(String x : ca) {
					if(generateD.contains(x)) {
						System.err.println(id+"\t"+x+"\tAnswer ID");
					}
				}
			}
			
			System.out.println("========================");
			
			rs = dbt.selectCodeTestALL();
			while(rs.next()) {
				String id = rs.getString(1);
				Code c = dbt.selectCode(id);
				if(generateD.contains(c.getUserID())) {
					System.err.println(id+"\t"+c.getUserID()+"\tUser ID");
				}
				Vector<String> ca = c.getAnswerList();
				for(String x : ca) {
					if(generateD.contains(x)) {
						System.err.println(id+"\t"+x+"\tAnswer ID");
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
