$(function(){
	
	$(".project-toggle").on("click",function(){
		const label = $(this).find(".toggle-label");
		
		if($(this).attr("aria-expanded") === "true"){
			label.text("▼ 閉じる");
		}else{
			label.text("▶ 開く");
		}
	});
	
})