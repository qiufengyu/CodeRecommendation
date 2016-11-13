/**
 * how to evaluate recommend system?
 * predict if a user will visit or see the code, from result one by one
 * First step: match tag of code and tag of user, if match value is high, then recommend
 * Second step: relation, if a code publish by a friend of the user, then he may visit
 */

package crawler;

public class MainCrawler {
	public void runCrawler(int choice, int start, int end) {
//		String a = "3.42k";
//		int len = a.length();
//		String b = a.substring(0, len-1);
//		System.out.println(b);
		GetStackExchange gse = new GetStackExchange();
		gse.runCrawler(choice, start, end);
		
//		for(int i = 220; i<230; ++i) {
//			gse.deleteOnePageCode(i);
//			System.out.println("Delete page "+i);
//		}
		
		
	} 
}
