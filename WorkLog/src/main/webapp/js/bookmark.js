$(function(){
	
	// 편집 버튼 눌렀을 때
	$("#bmkEditBtn").on("click",function(){
		$(this).toggleClass("bmk-edit");
		if($(this).hasClass("bmk-edit")){
			// 편집 모드
			$(this).text('삭제');
			$(".bmk-check").show();
			$("#bmkCancelBtn").show();
		}else{
			// 삭제 버튼을 눌렀을 때
			const checked = $(".bmk-check:checked");
			if(checked.length === 0){
				alert("목록에서 삭제할 게시글을 선택해 주세요");
				//다시 편집 모드로 유지
				$(this).addClass("bmk-edit");
				$(this).text('삭제');
				$(".bmk-check").show();
				$("#bmkCancelBtn").show();
				return;
			}
			if(confirm("목록에서 삭제하시겠습니까?")){
				checked.each(function(){
					$(this).closest(".bookmark-card").remove();
				})
			}else{
				// 취소하면 편집 모드 유지
				$(this).addClass("bmk-edit");
				$(this).text("삭제");
				$(".bmk-check").show();
				$("#bmkCancelBtn").show();
			}
		}
	})
	
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
	
	
	// 편집 취소
	$("#bmkCancelBtn").on("click",function(){
			$("#bmkEditBtn").removeClass("bmk-edit");
			$("#bmkEditBtn").text('편집');
			$(".bmk-check").hide();
			$(".bmk-check").prop("checked",false);
			$("#bmkCancelBtn").hide();
		})
		
})

$(function () {

    // 프로젝트별 게시글 필터링
    $(".project-filter-btn").on("click", function () {
        const projectId = String($(this).attr("data-project"));

        // 선택된 버튼 표시
        $(".project-filter-btn").removeClass("active");
        $(this).addClass("active");

        // 프로젝트별 게시글 표시
        $(".bookmark-card").each(function () {
            const cardProject = String($(this).attr("data-project"));
            if (projectId === "all" || cardProject === projectId) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    });
});

