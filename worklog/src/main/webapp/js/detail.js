$(function(){
	
	// 북마크 버튼
	$("#bookmarkBtn").on("click",function(){
		$(this).toggleClass('bmk');
		if($(this).hasClass('bmk')){
			$(this).text('✅ブックマーク');
		}else{
			$(this).text('📑ブックマーク');
		}
	})
	
	$("#confirmBtn").on("click",function(){
		$(this).toggleClass('cfm');
		if($(this).hasClass('cfm')){
			$(this).text('✅ 確認完了');
		}else{
			$(this).text('確認');
		}
	})
	
})