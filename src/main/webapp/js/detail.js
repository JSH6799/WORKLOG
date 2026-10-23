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
	
	// 확인 버튼
	$("#confirmBtn").on("click", function(){
		const btn = $(this);

	// 요청 중 중복 클릭 방지
	if(btn.prop("disabled")) {
		return;
	}

	// 이미 확인 완료된 경우 다시 요청하지 않음
	if(btn.hasClass("cfm")) {
		return;
	}

	const params = new URLSearchParams(window.location.search);
	const bno = params.get("bno");

	if(!bno) {
		alert("게시글 번호를 찾을 수 없습니다.");
		return;
	}

	btn.prop("disabled", true);

	$.ajax({
		url: `${window.location.origin}${window.location.pathname.replace(/\/[^/]*$/, "/check")}`,
		type: "POST",
		data: { bno: bno },
		dataType: "json",
		success: function(res) {
			if(res.success) {
				btn.addClass("cfm");
				btn.text("✅ 確認完了");
				btn.removeClass("btn-outline");
			} else {
				alert("確認処理に失敗しました。");
			}
		}, error: function(xhr) {
			if(xhr.status === 401) {
				alert("ログインしてください。");
			} else {
				alert("確認処理に失敗しました。もう一度お試しください。");
			}
		},complete: function() {
			btn.prop("disabled", false);
		}
	});
});
	
})