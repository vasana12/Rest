<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Trusted Ticket Requester</title>
<script type="text/javascript">
$(function(){
	$("loginBtn").click(function(){
		if($("username").val()==""){
			return;
		}
	})
})
</script>
<style type="text/css">
 .style1 {width: 100%;}
 .style2 {width: 429px;}
 #server {width: 254px;}
</style>
</head>
<body>

<h3>Trusted Ticketer</h3>
<form method="POST" id="form1" onSubmit="submitForm()">
 <table class="style1">
   <tr>
     <td class="style2">Username</td>
     <td><input type="text" name="username" value="" /></td>
   </tr>
   <tr>
     <td class="style2">Server</td>
     <td><input type="text" id="server" name="server" value="http://" /></td>
   </tr>
   <tr>
     <td class="style2">Client IP (optional)</td>
     <td><input type="text" id="client_ip" name="client_ip" value="" /></td>
   </tr>
   <tr>
     <td class="style2">Site (leave blank for Default site; otherwise enter the site name)</td>
     <td><input type="text" id="target_site" name="target_site" value="" /></td>
   </tr>
   <tr>
     <td class="style2"><input type="submit" name="submittable" value="Get Ticket" /></td>
     <td>&#160;</td>
   </tr>
 </table>
</form>
<h4>Be sure to add your IP as a Trusted IP address to the server</h4>
</body>
</html>