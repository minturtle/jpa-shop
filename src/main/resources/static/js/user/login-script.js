document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('login-form');

    loginForm.addEventListener('submit', function (event) {
        event.preventDefault(); // 기본 폼 제출 동작을 방지

        const formData = new FormData(loginForm);
        const userId = formData.get('username');
        const password = formData.get('password');

        fetch('/api/user/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({  username: userId, 'password' : password })
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return Promise.reject('로그인에 실패했습니다.');
                }
            })
            .then(data => {
                if (data.success) {
                    alert('로그인 성공!');
                    // 로그인 성공 시 리다이렉트
                    window.location.href = '/';
                } else {
                    alert('로그인 실패: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('로그인 중 오류가 발생했습니다.');
            });
    });
});