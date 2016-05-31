<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
response.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String imagePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";
%>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>search result</title>
<link rel="stylesheet" href="speech-input.css">
<style>
.si-wrapper input {
	font-size: 1.0em;
	padding: .1em;
}
</style>
<style type="text/css">
<!--
#Layer1 {
	position:absolute;
	left:28px;
	top:26px;
	width:649px;
	height:32px;
	z-index:1;
}
#Layer2 {
	position:absolute;
	left:29px;
	top:82px;
	width:648px;
	height:602px;
	z-index:2;
}
#Layer3 {
	position:absolute;
	left:28px;
	top:697px;
	width:652px;
	height:67px;
	z-index:3;
}
-->
a:link{text-decoration:none; color:#00F;}
a:visited{text-decoration:none; color:#FF00FF;}
a:hover{text-decoration:underline; color:#228B22;}
a:active{text-decoration:overline; color:#F00;}
</style>
</head>

<body>
<%
	String currentQuery=(String) request.getAttribute("currentQuery");
	int currentPage=(Integer) request.getAttribute("currentPage");
%>
<div class="si-wrapper" style="top: 10px; left: 10px; ">
	
	<form id="form1" name="form1" method="get" action="WebServer">
    <label>
      <input name="query"  type="text"   class="si-input" value="<%=currentQuery%>"/>     	
    </label>
    <label>
    <input type="submit" name="Submit"  value="查询" />
    </label>
  </form>
  <script src="speech-input.js"></script>
</div>


<!--
<div id="Layer1">
  <form id="form1" name="form1" method="get" action="WebServer">
    <label>
      <input name="query" value="<%=currentQuery%>" type="text" size="70" />
    </label>
    <label>
    <input type="submit" name="Submit" value="查询" />
    </label>
  </form>
</div>
-->
<div id="Layer2" style="top: 82px; height: 585px;">
  <div id="imagediv">结果显示如下：
  <br>
  <Table style="left: 0px; width: 594px;">
  <% 
  	String[] imgTags=(String[]) request.getAttribute("Tags");
  	String[] imgPaths=(String[]) request.getAttribute("Paths");
  	String[] titles=(String[]) request.getAttribute("Titles");
  	String sub =null;
  	String t_path =null;
  	if(imgTags!=null && imgTags.length>0){
  		for(int i=0;i<imgTags.length;i++){
  			if(imgTags[i].length() > 150)
  				sub = imgTags[i].substring(0, 150);
  			else sub = imgTags[i];
  			t_path = imgPaths[i].substring(91,imgPaths[i].length());
  			//System.out.println("img_paths_:"+imgPaths[i]);
  			//System.out.println("t_path_:"+t_path);
  			t_path = "http://"+t_path;%>
  		<p>
  		<a href =<%=t_path %> target="_blank"><%=(currentPage-1)*10+i+1%>. <%=titles[i] %></a>
  		<br/>
  		<font><%=sub+"..." %></font>
  		<br/>
  		<a href = <%=t_path %> target="_blank"><%=t_path %></a>
  		</p>
  		<%}; %>
  	<%}else{ %>
  		<p><tr><h3>no such result</h3></tr></p>
  	<%}; %>
  </Table>
  </div>
  <div>
  	<p>
		<%if(currentPage>1){ %>
			<a href="WebServer?query=<%=currentQuery%>&page=<%=currentPage-1%>">上一页</a>
		<%}; %>
		<%for (int i=Math.max(1,currentPage-5);i<currentPage;i++){%>
			<a href="WebServer?query=<%=currentQuery%>&page=<%=i%>"><%=i%></a>
		<%}; %>
		<strong><%=currentPage%></strong>
		<%for (int i=currentPage+1;i<=currentPage+5;i++){ %>
			<a href="WebServer?query=<%=currentQuery%>&page=<%=i%>"><%=i%></a>
		<%}; %>
		<a href="WebServer?query=<%=currentQuery%>&page=<%=currentPage+1%>">下一页</a>
	</p>
  </div>
</div>
<div id="Layer3" style="top: 839px; left: 27px;">
	
</div>
<div>
</div>
</body>
