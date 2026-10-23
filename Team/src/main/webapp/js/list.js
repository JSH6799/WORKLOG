$(function(){
	
	// 검색 유효성
	$("#searchForm").on('submit',function(e){
		const searchText = $("#searchText").val().trim();
		if(searchText === ''){
			alert("검색어를 입력해 주세요");
			e.preventDefault();
			$("#searchText").focus();
			return;
		}
	})
	
	let isUnreadOnly = false;
	$("#buttonFilter").on('click',function(){
		isUnreadOnly = !isUnreadOnly;
		// 미확인 게시물만 보기
		if(isUnreadOnly == true){
			$(".board-list").each(function(){
				const unread = $(this).find(".check-required").length;
				if(unread === 0){
					$(this).hide();
				}
			})
		// 모든 게시물 다시 보기
		}else{
			$(".board-list").show();
		}
	})
	
})