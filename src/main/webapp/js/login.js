$(function () {
    $('#loginForm').on('submit', function (e) {
        if ($('#id').val().trim() === '') {
            alert('IDを入力してください。');
            e.preventDefault();
            return;
        }
        if ($('#pwd').val().trim() === '') {
            alert('パスワードを入力してください。');
            e.preventDefault();
            return;
        }
    });
});