$(function(){
	
	// 검색 유효성
	$("#searchForm").on('submit',function(e){
		const searchText = $("#searchText").val().trim();
		if(searchText === ''){
			alert("検索ワードを入力してください");
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
	
	$("#buttonFilter").on("click", function() {
	    const projectId = $(this).data("project-id");
	    const contextPath = $(this).data("context-path");
	    const params = new URLSearchParams(location.search);
	    
	    if (params.get("unreadOnly") === "Y") {
	        // 미확인 목록을 보고 있다면 전체 목록으로 돌아가기
	        location.href = contextPath
	            + "/project_board/list?projectId="
	            + encodeURIComponent(projectId);
	    } else {
	        // 전체 목록을 보고 있다면 미확인 목록으로 이동
	        location.href = contextPath
	            + "/project_board/list?projectId="
	            + encodeURIComponent(projectId)
	            + "&unreadOnly=Y";
	    }
	});
	
	const button = $("#buttonFilter");
	const params = new URLSearchParams(location.search);

	if (params.get("unreadOnly") === "Y") {
	    button.text("全体一覧に戻る");
	} else {
	    button.text("未確認投稿一覧");
	}
	
})