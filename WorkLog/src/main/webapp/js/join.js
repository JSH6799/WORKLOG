$(function(){
	let idChecked = false;
		$("#idCheckBtn").on("click",function(){
			const id = $("#id").val().trim();
			if(id === ''){
				alert("IDを入力してください");
				return;
			}
			$.ajax({
				url:contextPath + '/worklogMember/idCheck',
				type:'get',
				data:{memberId:id},
				dataType:'json',
				success:function(result){
					if(result.isDuplicate){
						$("#idCheckMsg").text('既に使用されているIDです。').css('color',"#ff3b5c");
					}else{
						$("#idCheckMsg").text('使用可能なIDです。').css('color',"#3bcff2");
						idChecked = true;
					}
				},error:function(){
					alert("通信エラー発生")
				}
			})
		}) // idCheck
			
		$("#id").on('input',function(){
			idChecked = false;
			$("#idCheckMsg").text('');
		})
		
		$("#pwdCheck").on('input',function(){
			const pwd = $("#pwd").val();
			const pwdCheck = $(this).val();
			if(pwdCheck === '') {
				$("#pwdCheckMsg").text('');
			}else if(pwd === pwdCheck) {
				$("#pwdCheckMsg").text("パスワードが一致します。").css('color','#3b6ff2');
			}else {
				$("#pwdCheckMsg").text("パスワードが一致しません。").css('color','#ff3b5c');
			}
		}) // pwdCheck
		
		$("#joinForm").on('submit',function(e){
			if(!idChecked) {
				alert("IDの重複チェックを確認してください。");
				e.preventDefault();
				return;
			}
			if($('#pwd').val() !== $("#pwdCheck").val()) {
				alert("パスワードが一致しません。");
				e.preventDefault();
				return;
			}
		})
	
})