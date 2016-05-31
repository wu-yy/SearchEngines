<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="java.io.File" %>
<%@page import="java.io.PrintStream" %>
<%@page import="java.io.FileOutputStream" %>
<%@page import="java.io.FileInputStream" %>

<%
request.setCharacterEncoding("utf-8");
System.out.println(request.getCharacterEncoding());
response.setCharacterEncoding("utf-8");
System.out.println(response.getCharacterEncoding());
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
System.out.println(path);
System.out.println(basePath);
%>

<!-- <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> -->
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>搜索</title>

<link rel="stylesheet" href="speech-input.css">
<style>
.si-wrapper input {
	font-size: 1.5em;
	padding: .1em;
}
</style>

<style type="text/css">
#Layer1 {
	position:absolute;
	left:489px;
	top:326px;
	width:404px;
	height:29px;
	z-index:1;
}
#Layer2 {
	position:absolute;
	left:1100px;
	top:68px;
	width:446px;
	height:152px;
	z-index:2;
}
#body1{
  background:url("img/bg.jpg");
 background-repeat:no-repeat;
 background-size:1368px 700px;         
 background-position-x:50%;
  }
</style>
</head>
<body id="body1">
<div class="si-wrapper" style="top: 230px; left: 400px; width: 450px;">
	
	<form id="form1" name="form1" method="get" action="servlet/WebServer">
    <label>
      <input name="query" type="text" class="si-input" placeholder="What's search?"/>     
      <button class="si-btn">
		speech input
		<span class="si-mic"></span>
		<span class="si-holder"></span>
		</button>		
    </label>
    <label>
    <input type="submit" name="Submit"  value="搜索" />
    </label>
  </form>
</div>
<script src="speech-input.js"></script>
<!-- 
<div id="Layer1" style="top: 230px; left: 400px; width: 441px;">
  <form id="form1" name="form1" method="get" action="servlet/WebServer">
    <label>
      <input name="query" type="text" size="50"/>
    </label>
    <label>
      <input name="query2" type="text" x-webkit-speech x-webkit-grammar="builtin:search" onclick="onChange()"/>
		<script type="text/javascript">
    	function onChange() {
        	if (document.createElement("input").webkitSpeech === undefined) {
    				// Not supported
    				alert("not support");
				} else {
   			 // Supported!
				}
    	}
		</script>
    </label>
    <label>
    <input type="submit" name="Submit" value="搜索" />
    </label>
  </form>
</div>
 -->
<div id="Layer2" style="top: 22px; height: 585px;">
  <div id="imagediv"><font size="5" color="red">搜索热榜：</font>
  <br>
   <Table style="left: 0px; width: 594px;">
  <% 
  	ArrayList<String>hot =new ArrayList<String>();
  	Scanner scan=new Scanner(new FileInputStream("C:/Program Files/Apache Software Foundation/Tomcat 6.0/bin/forIndex/hot.txt"));
  	scan.useDelimiter("\n");
  	StringBuffer buffer=new StringBuffer();
  	while(scan.hasNext())
  	{
  		String s=scan.next();
  		hot.add(s);
  		//System.out.println(s);
  	}
  	scan.close();
  	ArrayList<String>hot1=new ArrayList<String>();
  	int[] hot2=new int[hot.size()];
  	for(int i=0;i<hot.size();i++)
  	{
  		hot2[i]=0;
  	}
    for(int i=0;i<hot.size();i++)
    {
    	boolean  x=false;
    	for(int j=0;j<hot1.size();j++)
    	{
    		if(hot.get(i).equals(hot1.get(j)))
    		{
    			x=true;
    			hot2[j]++;
    		}
    	}
    	if(!x)
    	{
    		hot1.add(hot.get(i));
    		//System.out.println("新加");
    	}
    
    
    }
    int Max=-1;
    int index=-1;
    int num=0;
    String[] imgTags=new String[hot1.size()];
    for(int k=0;k<hot1.size();k++)
    {
    	Max=-1;
  		index=-1;
  		for(int i=0;i<hot1.size();i++)
  		{
  			if(Max<hot2[i])
  			{
  				index=i;
  				Max=hot2[i];
  			}
  		}
  		if(index!=-1)
  		{
  			imgTags[num]=hot1.get(index);
  			hot2[index]=-1;
  			num++;
  		}
  	}
  	String sub =null;
  	if(imgTags!=null && imgTags.length>0){
  		for(int i=0;i<imgTags.length;i++){
  			if(imgTags[i].length() > 150)
  				sub = imgTags[i].substring(0, 150);
  			else sub = imgTags[i];
  			//System.out.println("img_paths_:"+imgPaths[i]);
  			//System.out.println("t_path_:"+t_path);
  			%>
  		<p>
  		<br/>
  		<font size="3" color="red"><%=sub%></font>
  		<br/>
  		</p>
  		<%}; %>
  	<%}else{ %>
  		<p><tr><h3>没有结果</h3></tr></p>
  	<%}; %>
  </Table>
  </div>
 </div>
</body>
</html>
