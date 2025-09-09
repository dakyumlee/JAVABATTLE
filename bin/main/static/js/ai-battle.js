
class AIBattleManager {
    constructor() {
        this.stompClient = null;
        this.selectedDifficulty = null;
        this.timeLeft = 300;
        this.timerInterval = null;
        this.isGameStarted = false;
        this.roomId = null;
    }

    init() {
        this.showDifficultySelector();
    }

    showDifficultySelector() {
        const selector = document.getElementById('difficultySelector');
        if (selector) {
            selector.style.display = 'flex';
        }
    }

    selectDifficulty(difficulty) {
        this.selectedDifficulty = difficulty;
        this.hideDifficultySelector();
        this.showWaitingScreen();
        this.updateDifficultyBadge(difficulty);
        this.connect();
    }

    hideDifficultySelector() {
        const selector = document.getElementById('difficultySelector');
        if (selector) {
            selector.style.display = 'none';
        }
    }

    showWaitingScreen() {
        const waitingScreen = document.getElementById('waitingScreen');
        if (waitingScreen) {
            waitingScreen.style.display = 'flex';
        }
    }

    hideWaitingScreen() {
        const waitingScreen = document.getElementById('waitingScreen');
        if (waitingScreen) {
            waitingScreen.style.display = 'none';
        }
    }

    updateDifficultyBadge(difficulty) {
        const badge = document.getElementById('difficultyBadge');
        if (badge) {
            badge.textContent = difficulty.toUpperCase();
            badge.className = `difficulty-badge difficulty-${difficulty}`;
        }
    }

    connect() {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            alert('로그인이 필요합니다.');
            window.location.href = '/';
            return;
        }

        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect(
            {'Authorization': `Bearer ${token}`}, 
            (frame) => this.onConnected(frame), 
            (error) => this.onError(error)
        );
    }

    onConnected(frame) {
        console.log('AI Battle WebSocket 연결 성공:', frame);
        
        this.stompClient.subscribe('/user/queue/battle', (message) => {
            this.onBattleMessage(message);
        });
        
        this.stompClient.send('/app/ai-battle/join', {}, JSON.stringify({
            type: 'JOIN_QUEUE',
            difficulty: this.selectedDifficulty
        }));
    }

    onError(error) {
        console.error('AI Battle WebSocket 연결 실패:', error);
        this.showErrorScreen();
    }

    showErrorScreen() {
        const waitingScreen = document.getElementById('waitingScreen');
        if (waitingScreen) {
            waitingScreen.innerHTML = `
                <div style="text-align: center;">
                    <div style="color: #ef4444; font-size: 18px; margin-bottom: 10px;">AI 서버 연결 실패</div>
                    <div style="color: #9ca3af; font-size: 14px; margin-bottom: 20px;">AI 배틀 서버에 연결할 수 없습니다</div>
                    <button onclick="location.reload()" class="btn btn-primary" style="margin-right: 10px;">다시 시도</button>
                    <button onclick="window.location.href='/'" class="btn btn-secondary">홈으로</button>
                </div>
            `;
        }
    }

    onBattleMessage(message) {
        const data = JSON.parse(message.body);
        console.log('AI Battle 메시지:', data);

        switch(data.type) {
            case 'WAITING':
                this.updateWaitingMessage(data.message || 'AI가 문제를 생성하고 있습니다...');
                break;
            case 'GAME_START':
                this.startGame(data);
                break;
            case 'CODE_UPDATE':
                this.updateOpponentCode(data.code);
                break;
            case 'GAME_END':
                this.endGame(data);
                break;
            case 'INCORRECT':
                this.showIncorrectMessage(data);
                break;
            case 'OPPONENT_LEFT':
                alert('상대방이 나갔습니다.');
                window.location.href = '/';
                break;
        }
    }

    updateWaitingMessage(message) {
        const waitingText = document.querySelector('.waiting-text');
        if (waitingText) {
            waitingText.textContent = message;
        }
    }

    startGame(data) {
        console.log('AI 배틀 게임 시작:', data);
        this.isGameStarted = true;
        this.roomId = data.roomId;
        
        this.hideWaitingScreen();
        this.displayProblem(data.problem);
        this.updateGameStatus();
        this.displayOpponent(data.opponent);
        this.startTimer();
    }

    displayProblem(problem) {
        const titleElement = document.getElementById('problemTitle');
        const descriptionElement = document.getElementById('problemDescription');
        const sampleSection = document.getElementById('sampleSection');
        const sampleInput = document.getElementById('sampleInput');
        const sampleOutput = document.getElementById('sampleOutput');

        if (titleElement) titleElement.textContent = problem.title;
        if (descriptionElement) descriptionElement.textContent = problem.description;
        
        if (problem.sampleInput && problem.sampleOutput && sampleSection && sampleInput && sampleOutput) {
            sampleInput.textContent = problem.sampleInput;
            sampleOutput.textContent = problem.sampleOutput;
            sampleSection.style.display = 'block';
        }
    }

    updateGameStatus() {
        const statusMessage = document.getElementById('statusMessage');
        const statusDetail = document.getElementById('statusDetail');
        
        if (statusMessage) statusMessage.textContent = '🤖 AI 배틀 진행 중!';
        if (statusDetail) statusDetail.textContent = 'AI가 생성한 문제를 해결하세요';
    }

    displayOpponent(opponent) {
        if (opponent && opponent.name) {
            const player2Name = document.getElementById('player2Name');
            const opponentName = document.getElementById('opponentName');
            
            if (player2Name) player2Name.textContent = opponent.name;
            if (opponentName) opponentName.textContent = opponent.name;
        }
    }

    updateOpponentCode(code) {
        const opponentCode = document.getElementById('opponentCode');
        if (opponentCode) {
            opponentCode.textContent = code || '상대방이 코드를 작성 중입니다...';
        }
    }

    startTimer() {
        this.timerInterval = setInterval(() => {
            this.timeLeft--;
            this.updateTimerDisplay();
            
            if (this.timeLeft <= 0) {
                clearInterval(this.timerInterval);
                this.endGame({ result: 'TIME_UP' });
            }
        }, 1000);
    }

    updateTimerDisplay() {
        const timer = document.getElementById('timer');
        if (timer) {
            const minutes = Math.floor(this.timeLeft / 60);
            const seconds = this.timeLeft % 60;
            timer.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        }
    }

    submitCode() {
        const codeEditor = document.getElementById('codeEditor');
        if (!codeEditor) return;
        
        const code = codeEditor.value;
        
        if (!this.isGameStarted) {
            alert('AI 배틀이 시작되지 않았습니다.');
            return;
        }

        if (!this.stompClient || !this.stompClient.connected) {
            alert('서버와 연결이 끊어졌습니다.');
            return;
        }

        this.stompClient.send('/app/ai-battle/submit', {}, JSON.stringify({
            code: code
        }));

        this.updateSubmissionStatus();
    }

    updateSubmissionStatus() {
        const statusMessage = document.getElementById('statusMessage');
        const statusDetail = document.getElementById('statusDetail');
        
        if (statusMessage) statusMessage.textContent = '코드 제출 완료!';
        if (statusDetail) statusDetail.textContent = 'AI가 검증 중...';
    }

    updateCode() {
        const codeEditor = document.getElementById('codeEditor');
        if (!codeEditor || !this.isGameStarted || !this.stompClient || !this.stompClient.connected) {
            return;
        }

        this.stompClient.send('/app/ai-battle/code-update', {}, JSON.stringify({
            code: codeEditor.value
        }));
    }

    showIncorrectMessage(data) {
        const statusMessage = document.getElementById('statusMessage');
        const statusDetail = document.getElementById('statusDetail');
        
        if (statusMessage) {
            statusMessage.textContent = '❌ 틀렸습니다!';
            statusMessage.style.color = '#ef4444';
        }
        if (statusDetail) {
            statusDetail.textContent = data.message || '다시 시도해보세요';
        }
        
        setTimeout(() => {
            if (statusMessage) {
                statusMessage.textContent = '🤖 AI 배틀 진행 중!';
                statusMessage.style.color = '#6366f1';
            }
            if (statusDetail) {
                statusDetail.textContent = 'AI가 생성한 문제를 해결하세요';
            }
        }, 3000);
    }

    endGame(data) {
        clearInterval(this.timerInterval);
        this.isGameStarted = false;

        const statusMessage = document.getElementById('statusMessage');
        const statusDetail = document.getElementById('statusDetail');
        
        let message = '';
        let color = '#6366f1';

        switch(data.result) {
            case 'WIN':
                message = '🎉 승리! AI 문제 해결 성공!';
                color = '#10b981';
                break;
            case 'LOSE':
                message = '😢 패배';
                color = '#ef4444';
                break;
            case 'TIME_UP':
                message = '⏰ 시간 종료';
                color = '#9ca3af';
                break;
        }

        if (statusMessage) {
            statusMessage.innerHTML = message;
            statusMessage.style.color = color;
        }
        if (statusDetail) {
            statusDetail.textContent = '5초 후 메인 페이지로 이동합니다';
        }

        setTimeout(() => {
            window.location.href = '/';
        }, 5000);
    }

    leaveBattle() {
        if (this.stompClient && this.isGameStarted) {
            this.stompClient.send('/app/ai-battle/leave', {}, JSON.stringify({}));
        }
    }
}

const aiBattleManager = new AIBattleManager();

function selectDifficulty(difficulty) {
    aiBattleManager.selectDifficulty(difficulty);
}

function submitCode() {
    aiBattleManager.submitCode();
}

function runCode() {
    const code = document.getElementById('codeEditor').value;
    
    fetch('/api/battle/run', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        },
        body: JSON.stringify({ code: code })
    })
    .then(response => response.json())
    .then(data => {
        alert('실행 결과: ' + (data.success ? data.output : data.error));
    })
    .catch(error => {
        console.error('코드 실행 실패:', error);
        alert('코드 실행에 실패했습니다.');
    });
}

// 이벤트 리스너
document.addEventListener('DOMContentLoaded', function() {
    // 코드 에디터 실시간 업데이트
    const codeEditor = document.getElementById('codeEditor');
    if (codeEditor) {
        codeEditor.addEventListener('input', function() {
            aiBattleManager.updateCode();
        });
    }
});

window.addEventListener('beforeunload', function() {
    aiBattleManager.leaveBattle();
});
