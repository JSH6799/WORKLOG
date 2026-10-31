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


$(function () {

    // 내가 쓴 게시글 / 미확인 게시글 탭 전환
    $(".mypage-tab").on("click", function () {
        const tabId = $(this).attr("data-tab");

        $(".mypage-tab").removeClass("active");
        $(this).addClass("active");

        $(".mypage-panel").prop("hidden", true);
        $("#" + tabId).prop("hidden", false);
    });

    // 내가 쓴 게시글: 전체 / 프로젝트별 필터
    $(".my-board-filter-btn").on("click", function () {
        const projectId = String($(this).attr("data-project"));

        $(".my-board-filter-btn").removeClass("active");
        $(this).addClass("active");

        if (projectId === "all") {
            $(".my-board-item").show();
        } else {
            $(".my-board-item").each(function () {
                const boardProjectId = String(
                    $(this).attr("data-project")
                );

                $(this).toggle(boardProjectId === projectId);
            });
        }
    });
});


$(function() {
    const editBtn = document.getElementById("memberEditBtn");
    const modalElement = document.getElementById("passwordConfirmModal");
    const passwordForm = document.getElementById("passwordConfirmForm");
    const passwordInput = document.getElementById("currentPassword");
    const errorMessage = document.getElementById("passwordConfirmError");

    // 필요한 요소가 있을 때만 실행
    if (!editBtn || !modalElement || !passwordForm) {
        return;
    }

    // Bootstrap Modal 생성
    const passwordModal = new bootstrap.Modal(modalElement);

	const editModalElement = document.getElementById("memberEditModal");
	const editForm = document.getElementById("memberEditForm");
	const editModal = new bootstrap.Modal(editModalElement);

	const editMemberId = document.getElementById("editMemberId");
	const editMemberName = document.getElementById("editMemberName");
	const editMemberEmail = document.getElementById("editMemberEmail");
	const editNewPassword = document.getElementById("editNewPassword");
	const editConfirmPassword = document.getElementById("editConfirmPassword");
	const editErrorMessage = document.getElementById("memberEditError");
	
	const passwordMatchMessage = document.getElementById("passwordMatchMessage");

	// 비밀번호 일치 여부 실시간 확인
	function checkPasswordMatch() {
	    const newPassword = editNewPassword.value;
	    const confirmPassword = editConfirmPassword.value;

	    // 두 입력란이 모두 비어 있으면 메시지 표시 안 함
	    if (newPassword === "" && confirmPassword === "") {
	        passwordMatchMessage.textContent = "";
	        return;
	    }

	    // 확인용 비밀번호를 아직 입력하지 않았다면 메시지 표시 안 함
	    if (confirmPassword === "") {
	        passwordMatchMessage.textContent = "";
	        return;
	    }

	    if (newPassword === confirmPassword) {
	        passwordMatchMessage.textContent = "✓ パスワードが一致します。";
	        passwordMatchMessage.style.color = "green";
	    } else {
	        passwordMatchMessage.textContent = "✕ パスワードが一致しません。";
	        passwordMatchMessage.style.color = "red";
	    }
	}

	editNewPassword.addEventListener("input", checkPasswordMatch);
	editConfirmPassword.addEventListener("input", checkPasswordMatch);
	
    // 정보 편집 버튼을 누르면 비밀번호 확인 창 표시
    editBtn.addEventListener("click", function() {
        passwordForm.reset();
        errorMessage.hidden = true;
        errorMessage.textContent = "";

        passwordModal.show();
    });

	
	
	passwordForm.addEventListener("submit", function (event) {
	    event.preventDefault();

	    const currentPassword = passwordInput.value;
	    const contextPath = window.location.pathname.split("/")[1];
	    const submitBtn = passwordForm.querySelector('button[type="submit"]');

	    errorMessage.hidden = true;
	    errorMessage.textContent = "";
	    submitBtn.disabled = true;

	    fetch("/" + contextPath + "/worklogMember/passwordCheck", {
	        method: "POST",
	        headers: {"Content-Type": "application/x-www-form-urlencoded"},
	        body: new URLSearchParams({currentPassword: currentPassword})
	    })
	    .then(function (response) {
	        return response.json().then(function (result) {
	            return {ok: response.ok, result: result};
	        });
	    })
	    .then(function (res) {
	        if (!res.ok || !res.result.success) {
	            errorMessage.textContent = res.result.message || "パスワードを確認してください。";
	            errorMessage.hidden = false;
	            return;
	        }
			// 비밀번호 확인 성공
			passwordModal.hide();

			const infoValues = document.querySelectorAll(".mypage-info-value");

			editMemberName.value = infoValues[0] ? infoValues[0].textContent.trim() : "";
			editMemberId.value = infoValues[1] ? infoValues[1].textContent.trim() : "";
			editMemberEmail.value = infoValues[2] ? infoValues[2].textContent.trim() : "";

			// 비밀번호 입력란 초기화
			editNewPassword.value = "";
			editConfirmPassword.value = "";
			passwordMatchMessage.textContent = "";

			// 오류 메시지 초기화
			editErrorMessage.hidden = true;
			editErrorMessage.textContent = "";

			// 회원 정보 편집 모달 열기
			editModal.show();
	    })
	    .catch(function (error) {
	        console.error(error);
	        errorMessage.textContent = "通信エラーが発生しました。もう一度お試しください。";
	        errorMessage.hidden = false;
	    })
	    .finally(function () {
	        submitBtn.disabled = false;
	    });
	});

	// 회원 정보 저장
	editForm.addEventListener("submit", function(event) {
	    event.preventDefault();

	    const newPassword = editNewPassword.value;
	    const confirmPassword = editConfirmPassword.value;

	    editErrorMessage.hidden = true;
	    editErrorMessage.textContent = "";

	    // 새 비밀번호가 입력된 경우 일치 여부 확인
	    if (newPassword !== confirmPassword) {
	        editErrorMessage.textContent = "新しいパスワードが一致しません。";
	        editErrorMessage.hidden = false;
	        return;
	    }

	    const submitBtn = editForm.querySelector('button[type="submit"]');
	    submitBtn.disabled = true;

	    const contextPath = "/" + window.location.pathname.split("/")[1];

	    $.ajax({
	        url: contextPath + "/worklogMember/memberUpdate",
	        type: "POST",
	        data: {
	            memberName: editMemberName.value.trim(),
	            email: editMemberEmail.value.trim(),
	            newPassword: newPassword
	        },
	        dataType: "json",

	        success: function(result) {
	            if (!result.success) {
	                editErrorMessage.textContent =
	                    result.message || "会員情報の更新に失敗しました。";
	                editErrorMessage.hidden = false;
	                return;
	            }

	            alert(result.message);

	            const infoValues = document.querySelectorAll(".mypage-info-value");

	            if (infoValues[0]) {
	                infoValues[0].textContent = editMemberName.value.trim();
	            }

	            if (infoValues[2]) {
	                infoValues[2].textContent = editMemberEmail.value.trim();
	            }

	            editModal.hide();
	        },

	        error: function(xhr) {
	            console.error(xhr.status, xhr.responseText);

	            editErrorMessage.textContent =
	                "通信エラーが発生しました。Eclipseのコンソールも確認してください。";
	            editErrorMessage.hidden = false;
	        },

	        complete: function() {
	            submitBtn.disabled = false;
	        }
	    });
	});
	
});


