/**
 * 
 */

$(document).ready(function(){
	
	$("#form1").submit(function (event){
	
		//Ajax 요청 수행합니다.
	var serializeArray = $(this).serializeArray()
	$('#output').load('tableauurl.re',$(this).serializeArray());
	$('<td></td>').text(serializeArray).appendTo('#output');
	});
});