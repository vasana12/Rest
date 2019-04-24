<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<!DOCTYPE html>
<html>
<head>
    <title>Basic Embed</title>

    <script type="text/javascript"
	    src="https://public.tableau.com/javascripts/api/tableau-2.min.js"></script>
    <script type="text/javascript">

        function initViz() {
        	var workBookName = '<c:out value="${workBookName}"/>';
        	var ticket = '<c:out value="${ticket}"/>';
        	alert(ticket);
            var containerDiv = document.getElementById("vizContainer"),
                url = "http://121.162.253.213:8000/trusted/"+ticket+"/views/Display/PlanogramPerformance",
                options = {
                    hideTabs: true,
                    onFirstInteractive: function () {
                        console.log("Run this code when the viz has finished loading.");
                    }
                };

            var viz = new tableau.Viz(containerDiv, url, options);
            // Create a viz object and embed it in the container div.
        }
/*         http://121.162.253.213:8000/views/Display/PlanogramPerformance?iframeSizedToWindow=true&:embed=y&:showAppBanner=false&:display_count=no&:showVizHome=no
 */    </script>
</head>
<body onload="initViz();">
 
 <div id="vizContainer" style="width:800px; height:700px;"></div>
 <h3>Trusted Ticketer</h3>
 <table class="style1">
   <tr>
   	<%-- <td id="output">
   	<c:forEach items="${ticket}" var="entry" varStatus="status">
    <iframe src="http://121.162.253.213:8000/trusted/${ticket}/Display/TestPerformance/School-LevelEvaluation?:embed=yes" width="1000px" height="850px"></iframe> 
	<br/>
	</c:forEach>
	
   	</td> --%>
   	
   </tr>

 </table>
<h4>Be sure to add your IP as a Trusted IP address to the server</h4>
</body>
  <!-- Custom scripts for all pages-->
  <script src="http://code.jquery.com/jquery-3.1.1.js"></script>
<!--   <script src="js/getTicket.js"></script>
 -->
</html>