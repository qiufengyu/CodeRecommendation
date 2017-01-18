package utils;

public interface UtilConstant {
	
	public final static String userTagDictionary = ".\\resources\\userTagDic.txt";
	public final static String codeTagDictionary = ".\\resources\\codeTagDic.txt";
	public final static String tagDictionary = ".\\resources\\tagDic.txt";
	public final static String codeIDList = ".\\resources\\codeIDList.txt";
	public final static String userIDList = ".\\resources\\userIDList.txt";
	
	public final static String codeIDDic = ".\\resources\\codeIDMap.txt";
	public final static String userIDDic = ".\\resources\\userIDMap.txt";
	public final static String matrixFile = ".\\resources\\matrix.txt";
	
	public final static String titleSentences = ".\\resources\\titles.txt";
	public final static String titleTrain = ".\\resources\\traintitles.txt";
	public final static String titleVector = ".\\resources\\titledocvec.txt";
	public final static String titleWord2Vec = ".\\resources\\word2vec.txt";
	public final static String titleSimMatrix = ".\\resources\\titlesimmatrix.txt";
	
	public final static String codeContentSimMatrix = ".\\resources\\itemsim.txt";
	
	
	public final static String titleSimMatrixpart = ".\\resources\\titlesimmatrixpart.txt";
	
	public final static String simMatrixFile = ".\\resources\\simmatrix.txt";
	
	/*
	public final static String cfMatrix = ".\\resources\\cfmatrix.txt";	
	public final static String cbfMatrix = ".\\resources\\cbfmatrix.txt";	
	public static final String tagMatrix = ".\\resources\\tagmatrix.txt";
	public static final String tagSim = ".\\resources\\tagsim.txt";
	*/
	public static final String userTagSim = ".\\resources\\usertagsim.txt";
	public static final String userTagMatrix = ".\\resources\\usertagmatrix.txt";
	
	public final static String cfMatrix = ".\\resources\\cfmatrix.txt";	
	public final static String cbfMatrix = ".\\resources\\cbfmatrix.txt";	
	public static final String tagMatrix = ".\\resources\\tagmatrix.txt";
	public static final String tagSim = ".\\resources\\tagsim.txt";
	
	public final static String cbfcontentMatrix = ".\\resources\\contentmatrix.txt";
	
	public static final String urMatrix = ".\\resources\\urmatrix.txt";
	public static final String relMap = ".\\resources\\relmap.txt";
	
	public static final String userReptVec = ".\\resources\\userreptvec.txt";
	
	public static final String resultDump = ".\\resources\\resultdump.txt";
	
	public static final String groundClean = ".\\resources\\groundclean.txt";
	
	public final static int numOfUser = 3675;//524;
	public final static int numOfCode = 6151;//653;
	public final static int numOfTag = 756;
	
	public final static double reptReduced = 100000.0;
	
	public final static double cfParam = 0.45081657;
	
	public final static double cbfParam = 3.54907703;
	
	public final static double tagParam = 7.7585584;
	
	public final static double urParam = 0.31862626;
	
	/*
	
	public final static double cfParam = 0.00001;
	
	public final static double cbfParam = 0.5;
	
	public final static double tagParam = 0.3;
	
	public final static double urParam = 0.01;
	
	public final static double utParam = 0.0001;
	*/
	
	public final static int N = 5;
	
	public final static String groundList = ".\\resources\\dev.txt";
	public final static String devList = ".\\resources\\dev.txt";
	
	// Regression:??
	public final static double multiply_cf   = 0.0;//0.02;
	public final static double multiply_cbf  = 0.0;//0.1;
	public final static double multiply_tag  = 0.0;//1.0;
	public final static double multiply_ur   = 1.0;//
	public final static double multiply_content = 0.0;//0.1;
	public final static double multiply_rept = 0.0;//0.2
	public final static double greatParam    = 0.5;
	
	/*
	public final static double multiply_cf = 0.05;
	public final static double multiply_cbf = 0.3;
	public final static double multiply_tag = 0.6;
	public final static double multiply_ur = 0.05;
	public final static double multiply_rept = -0.01;
	public final static double multiply_ut = 0.0;
	public final static double greatParam = 1.25;
	*/
	
	/*
	public final static double multiply_cf = 0.001;
	public final static double multiply_cbf = 0.001;
	public final static double multiply_tag = 0.1;
	public final static double multiply_ur = 0.001;
	public final static double multiply_rept = -2.0;
	public final static double multiply_ut = 0.000;
	public final static double greatParam = 0.205;
	*/
	
	
	public final static String dumpFile = ".\\resources\\dump.txt";
	
	
	public static String sentencePre(String x) {
		String temp = x.replaceAll("&#39;", "'");
		temp = temp.replaceAll("&rdquo;", "");
		temp = temp.replaceAll("&ldquo;", "");
		temp = temp.replaceAll("\\(", " ");
		temp = temp.replaceAll("\\)", " ");
		temp = temp.replaceAll("/", " ");
		temp = temp.replaceAll("\"", " ");
		temp = temp.replaceAll("\\[", " ");
		temp = temp.replaceAll("\\]", " ");
		temp = temp.replaceAll("\\&", " ");
		temp = temp.replaceAll("\\@", " ");
		temp = temp.replaceAll("&", " ");
		temp = temp.replaceAll("@", " ");
		temp = temp.replaceAll("\\?", " ");
		temp = temp.replaceAll("<", " ");
		temp = temp.replaceAll(">", " ");
		temp = temp.replaceAll(",", " , ");
		temp = temp.replaceAll("\\.$", " ");
		temp = temp.toLowerCase();
		return temp;
	}
	
}
