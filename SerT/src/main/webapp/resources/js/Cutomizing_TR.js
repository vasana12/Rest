/**
 * 
 */
$(document).ready(function(){
	alert("test");
	$(document).on("click","tr[role=row]",function(event){
		var workBookName = $(this).children().eq(1).text();
		alert(workBookName);
		location.href="getTicket.do?workBookName="+workBookName;
	})
})

