import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.wltea.analyzer.lucene.IKQueryParser;
import org.wltea.analyzer.lucene.IKSimilarity;

import java.util.*;
import java.math.*;
import java.net.*;
import java.io.*;

public class WebServer extends HttpServlet{
	
	public static final int PAGE_RESULT=10;
	public static final String indexDir="C:/Program Files/Apache Software Foundation/Tomcat 6.0/bin/forIndex";
	public static final String picDir="";
	private ImageSearcher search=null;
	public WebServer(){
		super();
		search=new ImageSearcher(new String(indexDir+"/index"));
		search.loadGlobals(new String(indexDir+"/global.txt"));
		
	}
	
	public ScoreDoc[] showList(ScoreDoc[] results,int page){
		if(results==null || results.length<(page-1)*PAGE_RESULT){
			return null;
		}
		int start=Math.max((page-1)*PAGE_RESULT, 0);
		int docnum=Math.min(results.length-start,PAGE_RESULT);
		ScoreDoc[] ret=new ScoreDoc[docnum];
		for(int i=0;i<docnum;i++){
			ret[i]=results[start+i];
		}
		return ret;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String queryString=request.getParameter("query");
		//hotList.add(queryString);
		//加入搜索热词榜
		FileWriter writer = null;  
		 try {     
	            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件     
	            writer = new FileWriter("C:/Program Files/Apache Software Foundation/Tomcat 6.0/bin/forIndex/hot.txt", true);     
	            writer.write(queryString+"\n");       
	        } catch (IOException e) {     
	            e.printStackTrace();     
	        } finally {     
	            try {     
	                if(writer != null){  
	                    writer.close();     
	                }  
	            } catch (IOException e) {     
	                e.printStackTrace();     
	            }     
	        }
		String pageString=request.getParameter("page");
		int page=1;
		if(pageString!=null){
			page=Integer.parseInt(pageString);
		}
		if(queryString==null){
			System.out.println("null query");
			//request.getRequestDispatcher("/Image.jsp").forward(request, response);
		}else{
			System.out.println(queryString);
			System.out.println(URLDecoder.decode(queryString,"utf-8"));
			System.out.println(URLDecoder.decode(queryString,"gb2312"));
			String[] tags=null;
			String[] paths=null;
			String[] titles=null;
			Query q=IKQueryParser.parseMultiField(new String[] {"Title","Content"}, queryString);//parseMul("Content",queryString);
			TopDocs results=search.searchMulQuery(q, 100);
			//TopDocs results=search.searchQuery(queryString, "abstract", 100);
			if (results != null) {
				System.out.print(results);
				ScoreDoc[] hits = showList(results.scoreDocs, page);
				if (hits != null) {
					tags = new String[hits.length];
					paths = new String[hits.length];
					titles = new String[hits.length];
					for (int i = 0; i < hits.length && i < PAGE_RESULT; i++) {
						Document doc = search.getDoc(hits[i].doc);
						tags[i] = doc.get("Content");
						titles[i] = doc.get("Title");
						paths[i] = picDir + doc.get("Path");
						System.out.println("paths:"+paths[i]);
					}
				} else {
					System.out.println("page null");
				}
			}else{
				System.out.println("result null");
			}
			request.setAttribute("currentQuery",queryString);
			request.setAttribute("currentPage", page);
			request.setAttribute("Tags", tags);
			request.setAttribute("Paths", paths);
			request.setAttribute("Titles", titles);
			request.getRequestDispatcher("/webshow.jsp").forward(request,
					response);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//request.setAttribute("hot", hotList);
		this.doGet(request, response);
	}
}
