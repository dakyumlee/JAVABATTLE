// 메인 페이지의 배틀 관련 함수들 수정

function createRoom() {
    requireAuth(() => {
        const roomName = prompt('방 이름을 입력하세요:');
        if (roomName && stompClient && stompClient.connected) {
            stompClient.send('/app/battle/create-room', {}, JSON.stringify({
                roomName: roomName
            }));
        } else {
            alert('서버에 연결되지 않았습니다.');
        }
    });
}

function joinRandomBattle() {
    requireAuth(() => {
        if (stompClient && stompClient.connected) {
            stompClient.send('/app/battle/join-random', {}, JSON.stringify({}));
        } else {
            alert('서버에 연결되지 않았습니다.');
        }
    });
}

function startSoloBattle() {
    requireAuth(() => {
        window.location.href = '/battle?mode=solo';
    });
}

// WebSocket 연결 및 배틀 메시지 처리 추가
let battleStompClient = null;

function connectToBattleServer() {
    const token = localStorage.getItem('jwtToken');
    if (!token) return;

    const socket = new SockJS('/ws');
    battleStompClient = Stomp.over(socket);
    
    battleStompClient.connect(
        {'Authorization': `Bearer ${token}`}, 
        function(frame) {
            console.log('배틀 서버 연결 성공');
            
            // 배틀 메시지 구독
            battleStompClient.subscribe('/user/queue/battle', function(message) {
                const data = JSON.parse(message.body);
                handleBattleMessage(data);
            });
        },
        function(error) {
            console.error('배틀 서버 연결 실패:', error);
        }
    );
}

function handleBattleMessage(data) {
    switch(data.type) {
        case 'ROOM_CREATED':
            alert(`방이 생성되었습니다: ${data.roomName}`);
            window.location.href = `/battle/room/${data.roomId}`;
            break;
        case 'WAITING':
            alert('상대방을 찾는 중입니다...');
            break;
        case 'GAME_START':
            window.location.href = `/battle/room/${data.roomId}`;
            break;
        case 'JOIN_FAILED':
            alert(data.message);
            break;
    }
}

// 페이지 로드 시 배틀 서버 연결
document.addEventListener('DOMContentLoaded', function() {
    if (authToken) {
        connectToBattleServer();
    }
});
