package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.Code;
import entity.UserReputation;
import utils.DBTools;

public class GetStackExchange {
	
	static String urlHead="http://codereview.stackexchange.com";
	static String urlLogin="https://codereview.stackexchange.com/users/login";
	String cookieVal;
	URL indexPage;
	
	BufferedReader br;
	InputStreamReader inputStream;
	String temp;
	String buf;
	int num;
	int count;
	
	DBTools dbtools;
	
	Pattern patternURL = Pattern.compile("onclick=\"window\\.location\\.href='(.+?)'\"");
	Pattern patternSUBURL = Pattern.compile("<h3><a href=\"(.+?)\" class=\"question-hyperlink\">");
	Pattern patternQuestionID = Pattern.compile("/questions/([0-9]+?)/(.+?)");
	Pattern patternQuestionID2 = Pattern.compile("/questions/([0-9]+?)$");
	Pattern patternTitle = Pattern.compile("<a.+>(.+?)</a>");
	Pattern patternContext = Pattern.compile("<p>(.+?)</p>");
	Pattern patternCode = Pattern.compile("<pre><code>(.+?)</code></pre>");
	Pattern patternTag = Pattern.compile("<a.+>(.+?)<");
	Pattern patternUser = Pattern.compile("users/([0-9]+?)/.+\">");
	
	Pattern patternUserID = Pattern.compile("users/([0-9]+?)/.+\">.+</a>");
	Pattern patternUserLocation = Pattern.compile("<span class=\"user-location\">(.+?)</span>");
	Pattern patternUserReputation = Pattern.compile("([0-9]+?)\" dir=\"ltr\">(.+?)</span>");
	Pattern patternUserRept2 = Pattern.compile("dir=\"ltr\">([0-9,]+?)</span>");
	Pattern patternUserTag = Pattern.compile("<a href=\"questions/tagged/(.+?)\">(.+?)");
	
	Pattern patternViewed = Pattern.compile("<b>([0-9]+?)\\stimes</b>");
	Pattern patternAsktime = Pattern.compile("title=\"([0-9:\\-\\s]+?)Z\"><b>(.+?)</b></p>");
	
	Pattern patternSingleRept = Pattern.compile("(.+?)<span(.+?)</span>");
	Pattern patternTime  = Pattern.compile("answered <span title=\"(.+?)\" class=\"relativetime\">(.*)</span>");
	
	Pattern patternCodeTitle = Pattern.compile("<h3><a href=\"/questions/([0-9]+?)/([a-z-]+?)\" class=\"question-hyperlink\">(.+?)</a></h3>");
	
	
	public GetStackExchange() {
		temp = "";
		buf = "";
		num = 0;
		dbtools = new DBTools();
		count = 0;
		cookieLogin();
	}
		
	public GetStackExchange(URL indexPage, BufferedReader br, InputStreamReader inputStream, String temp, String buf) {
		this.indexPage = indexPage;
		this.br = br;
		this.inputStream = inputStream;
		this.temp = temp;
		this.buf = buf;
		dbtools = new DBTools();
	}

	public void runCrawler(int choice, int start, int end) {
		cookieLogin();
		// init(type); // get all items
		// dealItem(choice);
		// dealReputation();
		
		
		for(int i = start; i<=end; i++) {
			//getOnePageTitle(i);
			// deleteOnePageCode(i);
			
			getOnePageCode(i, choice);
			System.err.println("Get page "+ i +" finished!");
			double x = Math.random();
			double y = x*3456;
			long z = (int) y;
			try {
				Thread.sleep(z);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void deleteOnePageCode(int i) {
		String url1 = "http://codereview.stackexchange.com/questions?page=";
		String url2 = "&sort=active";
		String url = url1+i+url2;
		try {
			indexPage = new URL(url);
			HttpURLConnection resumeConnection = (HttpURLConnection) indexPage.openConnection(); 
			resumeConnection.setConnectTimeout(20000);
			resumeConnection.setReadTimeout(20000);
			resumeConnection.setUseCaches(false);
			resumeConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
			if (cookieVal == null) {
				cookieLogin();
			    //锟斤拷锟斤拷cookie锟斤拷息锟斤拷去锟斤拷锟皆憋拷锟斤拷锟皆硷拷锟斤拷锟斤拷荩锟斤拷锟斤拷锟结被锟斤拷为没锟斤拷权锟斤拷  
			}  
			resumeConnection.setRequestProperty("Cookie", cookieVal);  
	
			resumeConnection.connect();  
			InputStream urlStream = resumeConnection.getInputStream();  
			br = new BufferedReader(new InputStreamReader(urlStream, "utf-8"));
			String line;
			while(true) {
				line = br.readLine();
				if(line.contains("<div id=\"questions\">") || line == null)
					break;				
			}
			String buf = "";
			while(true) {
				line = br.readLine();
				if(line.contains("<div class=\"page-sizer fr\">") || line == null)
					break;
				buf += line;
			}
			String[] sps = buf.split("<div class=\"summary\">");
			for(String x: sps) {
				Matcher matcher1 = patternSUBURL.matcher(x);
				String suburl = "";
				if(matcher1.find()) {
					suburl = matcher1.group(1);					
				}
				Matcher matcher2 = patternQuestionID.matcher(suburl);
				if(matcher2.find()) {
					suburl = matcher2.group(1);
				}
				dbtools.deleteCode(suburl);
			}
			double x = Math.random();
			double y = x*363;
			long z = (int) y;
			Thread.sleep(z);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void getOnePageCode(int i, int choice) {
		// TODO Auto-generated method stub
		String url1 = "http://codereview.stackexchange.com/questions?page=";
		String url2 = "&sort=active";
		String url = url1+i+url2;
		try {
			indexPage = new URL(url);
			HttpURLConnection resumeConnection = (HttpURLConnection) indexPage.openConnection(); 
			resumeConnection.setConnectTimeout(20000);
			resumeConnection.setReadTimeout(20000);
			resumeConnection.setUseCaches(false);
			resumeConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
			if (cookieVal == null) {
				// cookieLogin();
			    //锟斤拷锟斤拷cookie锟斤拷息锟斤拷去锟斤拷锟皆憋拷锟斤拷锟皆硷拷锟斤拷锟斤拷荩锟斤拷锟斤拷锟结被锟斤拷为没锟斤拷权锟斤拷  
			}  
			resumeConnection.setRequestProperty("Cookie", cookieVal);  
	
			resumeConnection.connect();  
			InputStream urlStream = resumeConnection.getInputStream();  
			br = new BufferedReader(new InputStreamReader(urlStream, "utf-8"));
			String line;
			while(true) {
				line = br.readLine();
				if(line.contains("<div id=\"questions\">") || line == null)
					break;				
			}
			String buf = "";
			while(true) {
				line = br.readLine();
				if(line.contains("<div class=\"page-sizer fr\">") || line == null)
					break;
				buf += line;
			}
			String[] sps = buf.split("<div class=\"summary\">");
			for(String x: sps) {
				Matcher matcher1 = patternSUBURL.matcher(x);
				String suburl = "";
				String userid = "";
				if(matcher1.find()) {
					suburl = matcher1.group(1);					
				}
				Matcher matcher2 = patternUser.matcher(x);
				if(matcher2.find()) {
					userid = matcher2.group(1);
				}
				dealSingleItem(suburl, userid, choice);
				double xx = Math.random();
				double y = xx*4321.9876;
				long z = (int) y;
				Thread.sleep(z);
			}			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dealSingleItem(String subURL, String userID, int choice) {
		if(subURL.equals(""))
			return;
		if(userID.equals(""))
			return;
		Code c = new Code();
		// TODO Auto-generated method stub
		try {
			indexPage = new URL(urlHead.trim()+subURL);
			HttpURLConnection resumeConnection = (HttpURLConnection) indexPage.openConnection();  
			resumeConnection.setConnectTimeout(20000);
			resumeConnection.setReadTimeout(20000);
			resumeConnection.setUseCaches(false);
			resumeConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
			if (cookieVal != null) {  
			    resumeConnection.setRequestProperty("Cookie", cookieVal);  
			}
			resumeConnection.connect();
			InputStream urlStream = resumeConnection.getInputStream();  
			br = new BufferedReader(new InputStreamReader(urlStream)); 
			buf = "";
			
			// Question ID
			Matcher questionIDMatcher = patternQuestionID.matcher(subURL);
			if(questionIDMatcher.find()) {
				c.setQuestionID(questionIDMatcher.group(1));
			}
			else {
				Matcher questionIDMatcher2 = patternQuestionID2.matcher(subURL);
				if(questionIDMatcher2.find())
					c.setQuestionID(questionIDMatcher2.group(1));
			}
			// c.setQuestionID(subURL);

			
			// Title / Header
			while((temp = br.readLine()) != null) {
			//	System.out.println(temp);
				if(temp.trim().contains("<div id=\"question-header\">")) {
					break;
				}
			}
			while((temp = br.readLine())!= null) {
				buf += temp.trim();
				if(temp.trim().contains("</div>"))
					break;
				
			}
			
			Matcher matcherTitle = patternTitle.matcher(buf);
			if(matcherTitle.find()) {
				String s = replaceCodeHTML(matcherTitle.group(1));
				c.setTitle(s);
			}
			buf = new String("");
			
			// Context
			while((temp = br.readLine()) != null) {
				if(temp.trim().contains("<div class=\"post-text\" itemprop=\"text\">")) {
					break;
				}
			}
			while((temp = br.readLine())!= null) {
				buf += (temp+"#_#");
				if(temp.trim().contains("</div>"))
					break;				
			}
			
			Matcher matcherContext = patternContext.matcher(buf);
			while(matcherContext.find()) {
				String cc = matcherContext.group(1);
				cc = replaceContextHTML(cc);
				cc = cc.replaceAll("#_#", "\n");
				c.setContext(c.getContext()+"\n"+cc);
			}
			
			// Code Area
			Matcher matcherCode = patternCode.matcher(buf);
			while(matcherCode.find()) {
				String t = replaceCodeHTML(matcherCode.group(1));
				// t = t.replaceAll(";", ";\n");
				t = t.replaceAll("#_#", "\n");
				c.setCodeEntity(c.getCodeEntity()+"\n"+t);
			}
			
			buf = new String("");
			// Tags
			while((temp = br.readLine()) != null) {
				if(temp.trim().contains("<div class=\"post-taglist\">")) {
					break;
				}
			}
			while((temp = br.readLine())!= null) {
				buf += temp.trim();
				if(temp.trim().contains("</div>"))
					break;				
			}
			
			Vector<String> v = new Vector<String>();
			String[] tagList = buf.split("/a>");
			for(String x: tagList) {				
				Matcher matcherTags = patternTag.matcher(x);
				if(matcherTags.find()) {				
					v.add(matcherTags.group(1));
				}				
			}
			
			c.setTags(v);
			
			buf = new String("");			
			// User ID
			c.setUserID(userID);
			
			while((temp = br.readLine()) != null) {
				if(temp.trim().contains("<div class=\"user-details\">")) {
					buf += temp.trim();
					break;
				}
			}
			// System.out.println(buf);
			while((temp = br.readLine())!= null) {
				buf += temp.trim();
				if(temp.trim().contains("votecell"))
					break;				
			}
			// System.out.println(buf);
			Matcher matcherUser = patternUser.matcher(buf);
			if(matcherUser.find()) {			
				c.setUserID(matcherUser.group(1));
				// System.out.println(c.getUserID()+"\t"+userID);
			}
									
			// AnswerList
			Vector<String> answerV = new Vector<String>();
			buf = "";
			while((temp = br.readLine()) != null) {
				buf += temp.trim();
				if(temp.contains("question-stats"))
					break;
				
			}
			
			String[] answercell = buf.split("answercell");
			if(answercell.length>1) {			
				for(int i = 1; i<answercell.length; ++i) {
					Matcher answerUsermatcher = patternUser.matcher(answercell[i]);
					Matcher timeMatcher = patternTime.matcher(answercell[i]);
					if(answerUsermatcher.find()) {
						if(timeMatcher.find()){
							String timestamp = timeMatcher.group(1);
							if(choice == 1) { // training data
								if( (!(timestamp.startsWith("2016"))) || // before 2016
										timestamp.startsWith("2016-01") || timestamp.startsWith("2016-02") || 
										timestamp.startsWith("2016-03")) { // in 2016 1 2 3 
									answerV.add(answerUsermatcher.group(1));
								}
							}
							else if(choice == 2 ) { // dev data
								if(!(timestamp.startsWith("2016-07") || timestamp.startsWith("2016-08") || 
										timestamp.startsWith("2016-09") || timestamp.startsWith("2016-10"))) {
									answerV.add(answerUsermatcher.group(1));
								}
							}
							else { //test data
								answerV.add(answerUsermatcher.group(1));
							}
						}
					}	
				}
			}
			
			c.setAnswerList(answerV);
			
			while((temp = br.readLine())!= null) {
				buf += temp.trim();
				if(temp.trim().contains("community-bulletin"))
					break;				
			}
			
			Matcher matcherViewed = patternViewed.matcher(buf);
			if(matcherViewed.find()) {
				// System.out.println(buf);
				String x = matcherViewed.group(1).trim();
				c.setViewed(Integer.valueOf(x));
			}
			else 
				c.setViewed(0);
			
			Matcher matcherAsktime = patternAsktime.matcher(buf);
			if(matcherAsktime.find()) {
				c.setTimestamp(matcherAsktime.group(1));
			}
			
//			c.showCode();
			if(c.getViewed()>=50) {
				// c.showCode();
				if(choice == 1) {
					try {
						// dbtools.insertRelation(c); // Relation insert first!
						dbtools.insertCode(c);
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(choice == 2) {
					try {					
						// c.showCode();
						dbtools.insertCodeDev(c);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
				else if(choice == 3) {
					try {					
						dbtools.insertCodeTest(c);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String x = new String("");// the id to be deleted!
			Matcher questionIDMatcher2 = patternQuestionID2.matcher(subURL);
			if(questionIDMatcher2.find())
				x = questionIDMatcher2.group(1);			
			dbtools.deleteCode(x); // delete it from training set
			dbtools.deleteCodeDev(x);
			dbtools.deleteCodeTest(x);
			//System.exit(-1);
		}		
	}

	public void dealItem(int choice) {
		String[] items = buf.split("<div class=\"question-summary narrow\"");
		String subURL="";
		String userID="";
		num = items.length;
		for(int i = 1; i<num; i++) {
			String t = items[i];
			Matcher matcherURL = patternURL.matcher(t);
			if(matcherURL.find()) {
				subURL = matcherURL.group(1);
				// System.out.println(subURL);
				// Deal with single item, open new page and get information of the code
			}
			Matcher matcherUserID = patternUser.matcher(t);
			if(matcherUserID.find())
				userID = matcherUserID.group(1);
			dealSingleItem(subURL, userID, choice);
			try {
			    Thread.sleep(2000);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
	}
	
	private void cookieLogin() {
		/** 
		 * java.net.URL and //java.net.URLConnection 
		 */  
		URL url;
		try {
			url = new URL(urlLogin);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);  
			  
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");  
			  
			out.write("email=978208608@qq.com&password=NJUNLP2016"); // post锟侥关硷拷锟斤拷锟节ｏ拷  
			// remember to clean up  
			out.flush();
			out.close(); 
			cookieVal = connection.getHeaderField("Set-Cookie");  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
				
	}

	public void init(String t) {
		cookieLogin();
		String type = "/?tab="+t;		
		try {
			indexPage = new URL(urlHead.trim()+type);
			HttpURLConnection resumeConnection = (HttpURLConnection) indexPage.openConnection();  
			if (cookieVal != null) {
			    resumeConnection.setRequestProperty("Cookie", cookieVal);  
			}  
			resumeConnection.connect();  
			InputStream urlStream = resumeConnection.getInputStream();  
			br = new BufferedReader(new InputStreamReader(urlStream));  
			
			buf = "";
			while((temp = br.readLine()) != null) {
				if(temp.trim().contains("<div id=\"qlist-wrapper\">")) {
					break;
				}
			}
			while((temp = br.readLine())!= null) {
				if(temp.trim().contains("bottom-notice"))
					break;
				buf += temp.trim();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void dealReputation() {
		// TODO Auto-generated method stub
		String type = "/users?page=";
		String tail = "&tab=reputation&filter=all";
		for(int i = 400; i<2000; i++) { // 400
			System.out.println("Page "+i);
			try {
				indexPage = new URL(urlHead.trim()+type+String.valueOf(i)+tail);
				HttpURLConnection resumeConnection = (HttpURLConnection) indexPage.openConnection();  
				if (cookieVal != null) {  
				    //锟斤拷锟斤拷cookie锟斤拷息锟斤拷去锟斤拷锟皆憋拷锟斤拷锟皆硷拷锟斤拷锟斤拷荩锟斤拷锟斤拷锟结被锟斤拷为没锟斤拷权锟斤拷  
				    resumeConnection.setRequestProperty("Cookie", cookieVal);  
				}  
				resumeConnection.connect();  
				InputStream urlStream = resumeConnection.getInputStream();  
				br = new BufferedReader(new InputStreamReader(urlStream, "utf-8"));  
				
				while(true) {
					temp = br.readLine();
					if(temp.length()<=2)
						continue;
					if(temp.trim().contains("user-info"))
						break;
				}
				
				while(true) {
					UserReputation ui = new UserReputation();
					while(true) {
						temp = br.readLine();
						if(temp== null)
							continue;
						if(temp.contains("user-info"))
							break;
						else if(temp.contains("user-details")) { // get id
							temp = br.readLine();
							// System.out.println(temp);
							Matcher idMatcher = patternUserID.matcher(temp);
							if(idMatcher.find()) {
								ui.setUserID(idMatcher.group(1));
							}
						}		
						else if(temp.contains("user-location")) { // get location
							Matcher locationMatcher = patternUserLocation.matcher(temp);
							if(locationMatcher.find()) {
								ui.setLocation(locationMatcher.group(1));
							}
						}
						
						else if(temp.contains("reputation")) { // get reputation
							Matcher reputationMatcher = patternUserReputation.matcher(temp);
							if(reputationMatcher.find()) {								 
								int relReputation = Integer.valueOf(reputationMatcher.group(1));
//								int len = t.length();
//								if(t.endsWith("k")) {
//									String tt = t.substring(0, len-1);
//									double doublett = Double.valueOf(tt);
//									relReputation = (int)(doublett*1000);
//								}
								ui.setReputation(relReputation);
							}
							if(ui.getReputation()<1) {
								Matcher rept2 = patternUserRept2.matcher(temp);
								if(rept2.find()) {
									String rel2 = rept2.group(1);
									rel2 = rel2.replaceAll(",", "");
									int rel2Val = Integer.valueOf(rel2);
									ui.setReputation(rel2Val);
								}
								
							}
						}
						else if(temp.contains("user-tags")) { // get tags of a user
//							temp = br.readLine();
//							Vector<String> tag = new Vector<String>();							
//							String[] l = temp.split("</a>");
//							for(String x: l) {
//								Matcher tagMatcher = patternUserTag.matcher(x);
//								if(tagMatcher.find()) {
//									tag.add(tagMatcher.group(2));
//								}
//							}
							ui.setUserTags(getSingleUserTag(ui.getUserID()));
						}
						if(temp.trim().contains("pager fr")) {
							break;
						}
					}
					
					// ui.show();
					try {
						dbtools.insertUserReputation(ui);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					if(temp.trim().contains("pager fr")) {
						break;
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			
		}
	}
	
	public void getOnePageTitle(int pagei) {
		String url = "http://codereview.stackexchange.com/questions?page="+String.valueOf(pagei)+"&sort=newest";
		try {
			indexPage = new URL(url);
			HttpURLConnection resumeConnection = (HttpURLConnection) indexPage.openConnection(); 
			resumeConnection.setConnectTimeout(20000);
			resumeConnection.setReadTimeout(20000);
			resumeConnection.setUseCaches(false);
			resumeConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
			if (cookieVal == null) {
				cookieLogin();
			    //锟斤拷锟斤拷cookie锟斤拷息锟斤拷去锟斤拷锟皆憋拷锟斤拷锟皆硷拷锟斤拷锟斤拷荩锟斤拷锟斤拷锟结被锟斤拷为没锟斤拷权锟斤拷  
			}  
			resumeConnection.setRequestProperty("Cookie", cookieVal);  
	
			resumeConnection.connect();  
			InputStream urlStream = resumeConnection.getInputStream();  
			br = new BufferedReader(new InputStreamReader(urlStream, "utf-8"));
			String line;
			while(true) {
				line = br.readLine();
				if(line.contains("<div id=\"questions\">") || line == null)
					break;				
			}
			String buf = "";
			while(true) {
				line = br.readLine();
				if(line.contains("<div class=\"page-sizer fr\">") || line == null)
					break;
				buf += line;
			}
			String[] sps = buf.split("<div class=\"summary\">");
			for(String x: sps) {
				Matcher matcher1 = patternCodeTitle.matcher(x);
				if(matcher1.find()) {
					String id1 = matcher1.group(1);
					String title1 = matcher1.group(3);
					dbtools.insertCodeTitle(id1, title1);
				}
			}
			double x = Math.random();
			double y = x*1333;
			long z = (int) y;
			Thread.sleep(z);
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private Vector<String> getSingleUserTag(String userID) {
		Vector<String> s = new Vector<String>();
		String url = "http://codereview.stackexchange.com/users/"+userID;
		Pattern patternSingleUserTag = Pattern.compile(">(.+?)</a>");
		try {
			URL userPage = new URL(url.trim());
			HttpURLConnection resumeConnection = (HttpURLConnection) userPage.openConnection();  
			if (cookieVal != null) {  
			    //锟斤拷锟斤拷cookie锟斤拷息锟斤拷去锟斤拷锟皆憋拷锟斤拷锟皆硷拷锟斤拷锟斤拷荩锟斤拷锟斤拷锟结被锟斤拷为没锟斤拷权锟斤拷  
			    resumeConnection.setRequestProperty("Cookie", cookieVal);  
			}  
			resumeConnection.connect();  
			InputStream urlStream = resumeConnection.getInputStream();  
			BufferedReader br = new BufferedReader(new InputStreamReader(urlStream, "utf-8"));
			String line;
			while(true) {
				line = br.readLine();
				if(line == null || line.contains("Top Tags"))
					break;
			}
			String buf = "";
			while(true) {
				line = br.readLine();
				if(line == null || line.contains("top-posts"))
					break;
				buf = buf + line;
			}
			String[] l = buf.split("rel=\"tag\"");
			// System.out.println(l.length);
			for(int i = 1; i<l.length; i++) {
				Matcher m = patternSingleUserTag.matcher(l[i]);
				if(m.find()) {
					s.add(m.group(1));
				}
			}			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
		    Thread.sleep(50);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		return s;
		
	}
	
	public UserReputation getSingleUserReputation(String userID) {
		UserReputation ur = new UserReputation();
		if(userID.length()<1) {
			System.out.println("ID Invalid, "+userID);
			return null;
		}
		Pattern patternSingleUserTag = Pattern.compile(">(.+?)</a>");
		ur.setUserID(userID);
		
		String url = "http://codereview.stackexchange.com/users/"+userID;
		try {
			indexPage = new URL(url);
			HttpURLConnection resumeConnection = (HttpURLConnection) indexPage.openConnection();  
			resumeConnection.setConnectTimeout(20000);
			resumeConnection.setReadTimeout(20000);
			resumeConnection.setUseCaches(false);
			resumeConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
			if (cookieVal != null) {  
			    //锟斤拷锟斤拷cookie锟斤拷息锟斤拷去锟斤拷锟皆憋拷锟斤拷锟皆硷拷锟斤拷锟斤拷荩锟斤拷锟斤拷锟结被锟斤拷为没锟斤拷权锟斤拷  
			    resumeConnection.setRequestProperty("Cookie", cookieVal);  
			}  
			
			resumeConnection.connect();  
			InputStream urlStream = resumeConnection.getInputStream();  
			br = new BufferedReader(new InputStreamReader(urlStream, "utf-8"));
			String line;
			while(true) {
				line = br.readLine();
				if(line == null || line.contains("label-uppercase")) {
					// this line contains reputation
					break;
				}
			}
			Matcher reptm = patternSingleRept.matcher(line);
			String s1 = "";
			if(reptm.find()) {
				s1 = reptm.group(1);
			}
			s1 = s1.trim();
			s1 = s1.replaceAll(",", "");
			int val = Integer.valueOf(s1);
			ur.setReputation(val);
				
			// Location
			while(true) {
				line = br.readLine();
				if(line == null || line.contains("icon-location")) {
					break;
				}
			}
			if(line == null) {
				ur.setLocation(null);
				indexPage = new URL(url);
				resumeConnection = (HttpURLConnection) indexPage.openConnection();  
				if (cookieVal != null) {  
				    //锟斤拷锟斤拷cookie锟斤拷息锟斤拷去锟斤拷锟皆憋拷锟斤拷锟皆硷拷锟斤拷锟斤拷荩锟斤拷锟斤拷锟结被锟斤拷为没锟斤拷权锟斤拷  
				    resumeConnection.setRequestProperty("Cookie", cookieVal);  
				}  
				resumeConnection.connect();  
				urlStream = resumeConnection.getInputStream();
				br = new BufferedReader(new InputStreamReader(urlStream, "utf-8"));
			}
			else {
				line = br.readLine();
				ur.setLocation(line.trim());
			}
			
			// User Tags
			Vector<String> s = new Vector<String>();
			while(true) {
				line = br.readLine();
				if(line == null || line.contains("Top Tags"))
					break;
			}
			String buf = "";
			while(true) {
				line = br.readLine();
				if(line == null || line.contains("top-posts"))
					break;
				buf = buf + line;
			}
			String[] l = buf.split("rel=\"tag\"");
			// System.out.println(l.length);
			for(int i = 1; i<l.length; i++) {
				// System.out.println(l[i]);
				Matcher m = patternSingleUserTag.matcher(l[i]);
				if(m.find()) {
					s.add(m.group(1));
				}
			}
			ur.setUserTags(s);
			double x = Math.random()*583;
			int y = (int) x;
			try {
				Thread.sleep((long)y);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			++count;
			System.out.print("#"+(count)+"\tuser, ");
			dbtools.insertUserReputation(ur);
			
			// line = br.readLine().trim();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			++count;
			System.err.print("#"+count+"\tuser"+userID+", not find!\t to stackoverflow");
			// try stackoverflow
			getSingleUserReputationStack(userID);
			// e.printStackTrace();
		}
		
		return ur;
	}
	
	
	public UserReputation getSingleUserReputationStack(String userID) {
		UserReputation ur = new UserReputation();
		String cookieVal2=null;
		if(userID.length()<1) {
			System.out.println("ID Invalid, "+userID);
			return null;
		}
		Pattern patternSingleUserTag = Pattern.compile(">(.+?)</a>");
		ur.setUserID(userID);
		
		String url = "http://stackoverflow.com/users/"+userID;
		try {
			indexPage = new URL(url);
			HttpURLConnection resumeConnection = (HttpURLConnection) indexPage.openConnection();  
			resumeConnection.setConnectTimeout(20000);
			resumeConnection.setReadTimeout(20000);
			resumeConnection.setUseCaches(false);
			resumeConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
			cookieVal2 = cookieLogin2();
			if (cookieVal2 != null) {  
			    //锟斤拷锟斤拷cookie锟斤拷息锟斤拷去锟斤拷锟皆憋拷锟斤拷锟皆硷拷锟斤拷锟斤拷荩锟斤拷锟斤拷锟结被锟斤拷为没锟斤拷权锟斤拷  
			    resumeConnection.setRequestProperty("Cookie", cookieVal2);  
			}  
			
			resumeConnection.connect();  
			InputStream urlStream = resumeConnection.getInputStream();  
			br = new BufferedReader(new InputStreamReader(urlStream, "utf-8"));
			String line;
			while(true) {
				line = br.readLine();
				if(line == null || line.contains("label-uppercase")) {
					// this line contains reputation
					break;
				}
			}
			Matcher reptm = patternSingleRept.matcher(line);
			String s1 = "";
			if(reptm.find()) {
				s1 = reptm.group(1);
			}
			s1 = s1.trim();
			s1 = s1.replaceAll(",", "");
			int val = Integer.valueOf(s1);
			ur.setReputation(val);
				
			// Location
			while(true) {
				line = br.readLine();
				if(line == null || line.contains("icon-location")) {
					break;
				}
			}
			if(line == null) {
				ur.setLocation(null);
				indexPage = new URL(url);
				resumeConnection = (HttpURLConnection) indexPage.openConnection();  
				if (cookieVal2 != null) {  
				    //锟斤拷锟斤拷cookie锟斤拷息锟斤拷去锟斤拷锟皆憋拷锟斤拷锟皆硷拷锟斤拷锟斤拷荩锟斤拷锟斤拷锟结被锟斤拷为没锟斤拷权锟斤拷  
				    resumeConnection.setRequestProperty("Cookie", cookieVal2);  
				}  
				resumeConnection.connect();  
				urlStream = resumeConnection.getInputStream();
				br = new BufferedReader(new InputStreamReader(urlStream, "utf-8"));
			}
			else {
				line = br.readLine();
				ur.setLocation(line.trim());
			}
			
			// User Tags
			Vector<String> s = new Vector<String>();
			while(true) {
				line = br.readLine();
				if(line == null || line.contains("Top Tags"))
					break;
			}
			String buf = "";
			while(true) {
				line = br.readLine();
				if(line == null || line.contains("top-posts"))
					break;
				buf = buf + line;
			}
			String[] l = buf.split("rel=\"tag\"");
			// System.out.println(l.length);
			for(int i = 1; i<l.length; i++) {
				// System.out.println(l[i]);
				Matcher m = patternSingleUserTag.matcher(l[i]);
				if(m.find()) {
					s.add(m.group(1));
				}
			}
			ur.setUserTags(s);
			double x = Math.random()*583;
			int y = (int) x;
			try {
				Thread.sleep((long)y);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("#"+(count)+"\tuser, ");
			dbtools.insertUserReputation(ur);
			
			// line = br.readLine().trim();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			System.err.println("user "+userID+", not find!");
			// e.printStackTrace();
		}
		
		return ur;
	}
	
	// re-construct data
	public void transfer(String fileName) throws IOException {
		File file = new File(fileName);
		String lines = new String();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		while(true) {
			lines = br.readLine();
			if(lines == null)
				break;
			String[] sp = lines.split("\t");
			String tempID = sp[0];
			String tempUser = sp[1];
			dealSingleItem(tempID, tempUser, 2);		
		}
		
		br.close();
	}

	private String replaceContextHTML(String cc) {
		// TODO Auto-generated method stub
		String plain = cc.replaceAll("<code>", "");
		plain = plain.replaceAll("</code>", "");
		return plain;
	}

	private String replaceCodeHTML(String group) {
		// TODO Auto-generated method stub
		String t = group.replaceAll("&lt;", "<");
		t = t.replaceAll("&gt;", ">");
		t = t.replaceAll("&amp;", "&");
		t = t.replaceAll("&ldquo;", "\"");
		t = t.replaceAll("&rdquo;", "\"");	
		return t;
	}
	
	private String cookieLogin2() {
		/** 
		 * 锟斤拷锟斤拷要锟斤拷URL锟铰碉拷URLConnection锟皆伙拷锟斤拷 URLConnection锟斤拷锟皆猴拷锟斤拷锟阶的达拷URL锟矫碉拷锟斤拷锟斤拷锟界： // Using 
		 * java.net.URL and //java.net.URLConnection 
		 */  
		URL url;
		String cookie;
		try {
			url = new URL("https://stackoverflow.com/users/login");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);  
			/** 
			 * 锟斤拷锟轿拷说玫锟絆utputStream锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟皆硷拷锟斤拷锟絎riter锟斤拷锟揭凤拷锟斤拷POST锟斤拷息锟叫ｏ拷锟斤拷锟界： ... 
			 */  
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");  
			              //锟斤拷锟叫碉拷memberName锟斤拷password也锟斤拷锟侥讹拷html锟斤拷锟斤拷锟街拷模锟斤拷锟轿拷锟斤拷卸锟接︼拷牟锟斤拷锟斤拷锟斤拷锟�  
			out.write("email=qiufengyu1024@gmail.com&password=qfy647293qfy"); // post锟侥关硷拷锟斤拷锟节ｏ拷  
			// remember to clean up  
			out.flush();
			out.close();  
			  
			// 取锟斤拷cookie锟斤拷锟洁当锟节硷拷录锟斤拷锟斤拷荩锟斤拷锟斤拷麓畏锟斤拷锟绞笔癸拷锟�  
			cookie = connection.getHeaderField("Set-Cookie");  
			return cookie;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;  
				
	}
	
}
