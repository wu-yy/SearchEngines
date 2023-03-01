package search;
import java.io.*;
import java.util.*;

import org.w3c.dom.*;   
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.Similarity;
import org.apache.pdfbox.pdmodel.PDDocument;  
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;  
import org.apache.pdfbox.pdmodel.PDDocumentInformation;  
import org.apache.pdfbox.pdmodel.PDPage;  
import org.apache.pdfbox.pdmodel.PDResources;  
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;  
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.Parser;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.*;

public class WebIndexer {
	private Analyzer analyzer; 
    private IndexWriter indexWriter;
    private float averageLength=1.0f;
    
    public WebIndexer(String indexDir){
    	analyzer = new IKAnalyzer();
    	try{
    		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, analyzer);
    		Directory dir = FSDirectory.open(new File(indexDir));
    		indexWriter = new IndexWriter(dir,iwc);
    		indexWriter.setSimilarity(new SimpleSimilarity());
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
    
    public void saveGlobals(String filename){
    	try{
    		PrintWriter pw=new PrintWriter(new File(filename));
    		pw.println(averageLength);
    		pw.close();
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
     
    public static String getContent(String fileString) throws Exception
    {
    	BufferedReader reader = new BufferedReader(  
    			new InputStreamReader(new FileInputStream( new File(fileString)), "utf-8"));  
    	String line = "";  
    	StringBuilder sb = new StringBuilder();  
    	while ((line = reader.readLine()) != null) {  
    		sb.append(line + "\r\n");  
    	}  
    	return sb.toString();
    }
    
    public void indexSpecialFile(List<String> filename){
    	try{
    		for(int j = 0;j<filename.size();j++){
    			System.out.println("filename index:"+j);
    			String htmlcontent = new String();
    			String title = new String();
    			
    			if (filename.get(j).endsWith("html")){
    				String content = getContent(filename.get(j));  
            		//title  
            		StringBuilder tsb = new StringBuilder();  
            		Parser tparser = Parser.createParser(content, "utf-8");  
            		NodeFilter tfilter = new TagNameFilter("title");
            		Node tnode = null;  
            		org.htmlparser.util.NodeList tnodeList = tparser.extractAllNodesThatMatch(tfilter);  
            		for (int i = 0; i < tnodeList.size(); ++i) {  
            		    tnode = tnodeList.elementAt(i);  
            		    tsb.append(tnode.toPlainTextString());  
            		}
            		title = tsb.toString();
            		//System.out.println("titlecontent: "+title);
            		
            		//content
            		StringBuilder csb = new StringBuilder();  
            		Parser cparser = Parser.createParser(content, "utf-8");  
            		NodeFilter cfilter = new TagNameFilter("p");
            		Node cnode = null;  
            		org.htmlparser.util.NodeList cnodeList = cparser.extractAllNodesThatMatch(cfilter);  
            		for (int i = 0; i < cnodeList.size(); ++i) {  
            		    cnode = cnodeList.elementAt(i);  
            		    csb.append(cnode.toPlainTextString());  
            		}
            		htmlcontent = title + csb.toString();
            		//System.out.println("htmlcontent: "+htmlcontent);
    			}
    			else if(filename.get(j).endsWith("pdf")){
    				//System.out.println(filename.get(j));
    				InputStream input = null;
    				File pdfFile = new File( filename.get(j) );  
    		        PDDocument document = null;
    		        input = new FileInputStream(pdfFile);
    		        document = PDDocument.load( input ); 
    		        PDDocumentInformation info = document.getDocumentInformation();
    		        title = info.getTitle();
    		        if(title == null){
    		        	File pdffile = new File(filename.get(j));
    		        	title = pdffile.getName();
    		        }
    		        //System.out.println( "title:" + title);	
    		      
    	            PDFTextStripper pts = new PDFTextStripper();  
    	            htmlcontent = pts.getText( document );  
    	            //System.out.println( "htmlcontent:" + htmlcontent );
    			}
    			else if(filename.get(j).endsWith("doc")){
    				File docfile = new File(filename.get(j));
    				title = docfile.getName();
    				//System.out.println(title);
    				try{
    				FileInputStream fis = new FileInputStream(docfile);
    			    WordExtractor wordExtractor = new WordExtractor(fis);
    			    htmlcontent = wordExtractor.getText();}
    			    //System.out.println(htmlcontent);}
    				catch (IOException e){
    					htmlcontent = " ";
    				}
    			}
    			Document document  =   new  Document();
    			Field TitleField  =   new  Field( "Title" ,title,Field.Store.YES, Field.Index.ANALYZED);
    			Field ContentField  =   new  Field( "Content" ,htmlcontent,Field.Store.YES, Field.Index.ANALYZED);
    			TitleField.setBoost(100f);
    			ContentField.setBoost(50);
    			Field PathField = new Field("Path",filename.get(j),Field.Store.YES, Field.Index.NO);
    			averageLength += htmlcontent.length();
    			document.add(TitleField);
    			document.add(ContentField);
    			document.add(PathField);
    			indexWriter.addDocument(document);
    		}
			averageLength /= indexWriter.numDocs();
			System.out.println("average length = "+averageLength);
			System.out.println("total "+indexWriter.numDocs()+" documents");
			indexWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    
    public void findHtml(List<String> htmllist,String path){
    	File file = new File(path);
    	File[] files = file.listFiles();
    	for(File tfile:files){     
    	    if(tfile.isDirectory()){
    	        findHtml(htmllist,tfile.toString());
    	    }
    	    else{
    	    	if(tfile.getAbsolutePath().endsWith("html") || tfile.getAbsolutePath().endsWith("pdf") || tfile.getAbsolutePath().endsWith("doc"))
    	    	{
    	    		htmllist.add(path+'\\'+tfile.getName());
        	    	System.out.println(path+" filename: "+tfile.getName());
    	    	}
    	    }     
    	}
    }
    
    public static void main(String[] args) {
    	System.out.println("WebIndexer ");
    	List<String> htmllist=new ArrayList<String>();
    	String path = "C:\\Program Files\\Apache Software Foundation\\Tomcat 6.0\\webapps\\news.tsinghua.edu.cn\\mirror\\news.tsinghua.edu.cn";
    	String path2="C:\\Program Files\\Apache Software Foundation\\Tomcat 6.0\\webapps\\news.tsinghua.edu.cn\\mirror\\pkunews.pku.edu.cn";
    	WebIndexer indexer=new WebIndexer("forIndex/index");
    	indexer.findHtml(htmllist,path);
    	//indexer.findHtml(htmllist,path2);
    	System.out.println("find html finish");
    	//System.out.println(htmllist.size());
    	//for(int i = 0; i< 3;i++){
    		//System.out.println(htmllist.get(i));
    	indexer.indexSpecialFile(htmllist);
    	//}
		//indexer.indexSpecialFile("input/sogou-utf8.xml");
		indexer.saveGlobals("forIndex/global.txt");
		System.out.println("indexer finish");
	}
}
