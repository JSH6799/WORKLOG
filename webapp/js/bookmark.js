$(function(){
	
	const $trigger = $("#bookmarkScrollTrigger");
	
	let page = 1;
	let hasMore = true;
	let loading = false;
	
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
			    const bnoList = [];

			    checked.each(function(){
			        bnoList.push($(this).val());
			    });

			    $.ajax({
			        url: $("#contextPath").val() + "/bookmark/delete",
			        type: "POST",
			        data: { bno: bnoList },
					traditional: true,
			        dataType: "json"
			    })
			    .done(function(result){

			        if(result.success){
			            checked.each(function(){
			                $(this).closest(".bookmark-card").remove();
			            });

			            $("#bmkEditBtn").removeClass("bmk-edit");
			            $("#bmkEditBtn").text("편집");
			            $(".bmk-check").hide().prop("checked", false);
			            $("#bmkCancelBtn").hide();

			        }else{
			            alert(result.message || "ブックマークの削除に失敗しました");
			        }

			    })
			    .fail(function(){
			        alert("ブックマークの削除に失敗しました");
			    });

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
	$("#searchForm").on("submit", function(e) {
	    const searchText = $("#searchText").val().trim();

	    if (searchText === '') {
	        alert("검색어를 입력해 주세요");
	        e.preventDefault();
	        $("#searchText").focus();
	        return;
	    }

	    $("#searchProjectId").val(
	        $(".project-filter-btn.active").attr("data-project") || "all"
	    );
	});
	
	// 편집 취소
	$("#bmkCancelBtn").on("click",function(){
			$("#bmkEditBtn").removeClass("bmk-edit");
			$("#bmkEditBtn").text('편집');
			$(".bmk-check").hide();
			$(".bmk-check").prop("checked",false);
			$("#bmkCancelBtn").hide();
		})
	
	// 게시글 카드 생성
	function createBookmarkCard(b) {
	    const $card = $("<article>")
	        .addClass("bookmark-card")
	        .attr("data-project", b.projectId);

	    $("<span>").addClass("bookmark-project-name")
	        .text(b.projectName).appendTo($card);

	    $("<h3>").text(b.title).appendTo($card);

	    $("<span>").addClass("bookmark-date")
	        .text(b.bookmarkDate).appendTo($card);

	    if ($("#bmkEditBtn").hasClass("bmk-edit")) {
	        $("<input>").attr({
	            type: "checkbox",
	            value: b.bno
	        }).addClass("bmk-check").show().prependTo($card);
	    }

	    return $("<li>").addClass("board-list")
	        .append($("<div>").addClass("bookmark-list").append($card));
	}

	// 다음 북마크 불러오기
	function loadMoreBookmarks() {
	    if (loading || !hasMore) return;

	    loading = true;

		$.getJSON($("#contextPath").val() + "/bookmark/list", {
		    ajax: "true",
		    page: page + 1,
		    projectId: $(".project-filter-btn.active")
		        .attr("data-project") || "all",
		    searchType: $("#searchForm .search-select").val(),
		    keyword: $("#searchText").val().trim()
		})
	    .done(function (list) {
			if (list.length === 0) {
			    hasMore = false;
			    return;
			}

			if (list.length < 10) {
			    hasMore = false;
			}

			list.forEach(function (b) {
			    $(".board-grid").append(createBookmarkCard(b));
			});

			page++;
	    })
	    .fail(function () {
	        console.error("북마크 목록을 불러오지 못했습니다.");
	    })
	    .always(function () {
	        loading = false;
	    });
	}

	// 목록 아래에 도달하면 다음 페이지 요청
	const observer = new IntersectionObserver(function (entries) {
	    if (entries[0].isIntersecting) loadMoreBookmarks();
	});

	if ($trigger.length) observer.observe($trigger[0]);


    // 프로젝트별 게시글 필터링
	$(".project-filter-btn").on("click", function () {
	    const projectId = String($(this).attr("data-project"));

	    $(".project-filter-btn").removeClass("active");
	    $(this).addClass("active");

	    // 기존 목록 초기화
	    $(".board-grid").empty();

	    // 무한 스크롤 상태 초기화
	    page = 0;
	    hasMore = true;

	    // 선택한 프로젝트의 첫 페이지부터 다시 불러오기
	    loadMoreBookmarks();
	});
});

