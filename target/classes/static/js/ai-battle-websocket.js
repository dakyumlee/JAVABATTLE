function connect() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        alert('로그인이 필요합니다.');
        window.location.href = '/';
        return;
    }

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    stompClient.connect(
        {'Authorization': `Bearer ${token}`}, 
        onConnected, 
        onError
    );
}

function onConnected(frame) {
    stompClient.subscribe('/user/queue/battle', onBattleMessage);
    
    stompClient.send('/app/ai-battle/join', {}, JSON.stringify({
        type: 'JOIN_QUEUE',
        difficulty: selectedDifficulty
    }));
}

function submitCode() {
    const code = document.getElementById('codeEditor').value;
    
    if (!isGameStarted) {
        alert('배틀이 시작되지 않았습니다.');
        return;
    }

    stompClient.send('/app/ai-battle/submit', {}, JSON.stringify({
        code: code
    }));

    document.getElementById('statusMessage').textContent = '코드 제출 완료!';
    document.getElementById('statusDetail').textContent = 'AI가 검증 중...';
}

document.getElementById('codeEditor').addEventListener('input', function() {
    if (isGameStarted && stompClient && stompClient.connected) {
        stompClient.send('/app/ai-battle/code-update', {}, JSON.stringify({
            code: this.value
        }));
    }
});

window.onbeforeunload = function() {
    if (stompClient && isGameStarted) {
        stompClient.send('/app/ai-battle/leave', {}, JSON.stringify({}));
    }
};
