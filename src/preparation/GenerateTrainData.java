package preparation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawler.GetStackExchange;
import entity.Code;
import entity.UserReputation;
import utils.DBTools;
import utils.UtilConstant;

public class GenerateTrainData implements UtilConstant{
	DBTools dbt;
	GetStackExchange getse;
	Set<String> userIDSet;
	
	private static Logger log = LoggerFactory.getLogger(GenerateTrainData.class);
	
	public GenerateTrainData() {
		super();
		// TODO Auto-generated constructor stub
		dbt = new DBTools();
		getse = new GetStackExchange();
		userIDSet = new TreeSet<String>();
	}
	
	
	public void generateUserReptDataset() {
		int count = 0;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(userIDList),"UTF-8"));
			String lines = "";
			
			while(true) {
				lines = br.readLine();
				if(lines == null)
					break;
				count++;	
				if(dbt.selectUserReputation(lines.trim()) == null)
					getse.getSingleUserReputation(lines.trim());
				/*
				double x = Math.random()*1783;
				int y = (int) x;
				try {
					Thread.sleep((long)y);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
			br.close();
		} catch (IOException | SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
	}
	
	
	public void genCodeIDList() {
		ResultSet rs;
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(codeIDList),"UTF-8"));
			rs = dbt.selectCodeALL();
			while(rs.next()) {
				bw.write(rs.getString(1)+"\t"+rs.getString(3)+"\n");
				bw.flush();
			}
			bw.close();
				
		} catch (SQLException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void genCodeNameSentences() {
		ResultSet rs;
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(titleSentences),"UTF-8"));
			rs = dbt.selectCodeTitleALL();
			while(rs.next()) {
				String temp = rs.getString(2);
				temp = UtilConstant.sentencePre(temp);				
				bw.write(temp+"\n");
				bw.flush();
			}
			bw.close();
				
		} catch (SQLException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public void trainWord2Vec() {
		genCodeNameSentences();
		String filePath;
		try {
			filePath = titleSentences;
			log.info("Load & Vectorize Sentences....");
	        // Strip white space before and after for each line
	        SentenceIterator iter = new BasicLineIterator(filePath);
	        // Split on white spaces in the line to get words
	        TokenizerFactory t = new DefaultTokenizerFactory();
	        t.setTokenPreProcessor(new CommonPreprocessor());

	        log.info("Building model....");
	        Word2Vec vec = new Word2Vec.Builder()
	                .minWordFrequency(2)
	                .iterations(100)
	                .layerSize(100)
	                .seed(42)
	                .windowSize(5)
	                .iterate(iter)
	                .tokenizerFactory(t)
	                .build();

	        log.info("Fitting Word2Vec model....");
	        vec.fit();

	        log.info("Writing word vectors to text file....");

	        // Write word vectors
	        WordVectorSerializer.writeWordVectors(vec, titleWord2Vec);

	        log.info("Closest Words: ");
	        Collection<String> lst = vec.wordsNearest("number", 10);
	        System.out.println(lst);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
        // UiServer server = UiServer.getInstance();
        // System.out.println("Started on port " + server.getPort());
    }
	
	public void genUserIDList() {
		ResultSet rs;
		try {
			rs = dbt.selectCodeALL();
			// user id and answerList
			while(rs.next()) {
				String pubID = rs.getString(3);
				if(pubID.length()>=1)
					userIDSet.add(pubID);
				String ansList = rs.getString(11);
				String[] ansArray = ansList.split("&");
				for(String x: ansArray) {
					if(x.length()>=1) {
						userIDSet.add(x);
					}
				}
			}
			// include test set
			
			rs = dbt.selectCodeTestALL();
			while(rs.next()) {
				userIDSet.add(rs.getString(3));
				String ansList = rs.getString(11);
				String[] ansArray = ansList.split("&");
				for(String x: ansArray) {
					if(x.length()>=1) {
						userIDSet.add(x);
					}
				}
			}
			
		System.err.println(userIDSet.size());	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// show userList
		BufferedWriter bw;		
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userIDList),"UTF-8"));
			for(String u: userIDSet) {
				bw.write(u+"\n");
				bw.flush();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public void generateTrainTitles() {
		ResultSet rs;
		BufferedWriter bw;		
		try {			
			rs = dbt.selectCodeALL();
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(titleTrain),"UTF-8"));
			while(rs.next()) {
				bw.write(rs.getString(1)+"\t"+rs.getString(2)+"\n");
				bw.flush();				
			}
			bw.close();
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void deleteFromDoc() {
		try {
			ResultSet rs = dbt.selectCodeTestALL();
			while(rs.next()) {
				String id = rs.getString(1);
				Code code = dbt.selectCode(id);
				if(code == null) {
					dbt.deleteCodeTest(id);
				}
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createUserRelation() {
		try {
			ResultSet rs = dbt.selectCodeALL();
			while(rs.next()) {
				Code c = dbt.selectCode(rs.getString(1));
				dbt.insertRelation(c);				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
}
