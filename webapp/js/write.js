
$(function() {

    // 기본 이미지 경로
    const defaultImg = contextPath + "/images/default_img.jpg";

    // 이미지 선택 시 미리보기
    $("#imgFile").on("change", function() {

        const file = this.files[0];

        if (!file) return;

        // 이미지 파일인지 확인
        if (!file.type.startsWith("image/")) {
            alert("画像ファイルを選択してください。");
            $(this).val("");
            $("#imgPreview").attr("src", defaultImg);
            return;
        }

        // 선택한 이미지 미리보기
        const previewUrl = URL.createObjectURL(file);
        $("#imgPreview").attr("src", previewUrl);

        // 기존 이미지 삭제 여부 초기화
        $("#removeImg").val("false");
    });

    // 이미지 제거
    $("#imgRemoveBtn").on("click", function() {

        $("#imgFile").val("");
        $("#imgPreview").attr("src", defaultImg);
        $("#removeImg").val("true");
    });

	// 이미지 선택 시 미리보기
	$("#imgFile").on("change", function(){
	    const file = this.files[0];
	    if(!file) return;

	    const previewUrl = URL.createObjectURL(file);
	    $("#imgPreview").attr("src", previewUrl);
	    $("#removeImg").val("false");
	});
	
	
	// 확인 여부 버튼
	$("#confirm").on("change",function(){
		let checked = $("#confirm").is(":checked");
		// 체크했을 때
		if(checked == true){
		
		// 체크 안 했을 때	
		}else{
			
		}
	})
	
	
	// 유효성 검사
	$("#writeForm").on('submit',function(e){
		const title = $("#title").val().trim();
		const content = $("#content").val().trim();
		if(title === ''){
			alert("タイトルを入力してください");
			e.preventDefault();
			$("#title").focus();
			return;
		}
		if(content == ''){
			alert("内容を入力してください");
			e.preventDefault();
			$("#content").focus();
			return;
		}
	})
	
	
})