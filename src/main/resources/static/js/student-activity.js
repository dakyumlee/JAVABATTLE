let stompClient = null;
let userId = null;
let sessionId = null;
let isConnected = false;
let activityInterval = null;
let reconnectAttempts = 0;
let maxReconnectAttempts = 5;

function getUserIdFromToken() {
    const token = localStorage.getItem('jwtToken');
    console.log('토큰 확인:', token);
    
    if (!token) {
        console.log('토큰이 없음 - 기본값 1 사용');
        return 1;
    }
    
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log('토큰 페이로드:', payload);
        return payload.userId || payload.sub || payload.id || 1;
    } catch (e) {
        console.error('토큰 파싱 실패:', e);
        return 1;
    }
}

function initializeWebSocket() {
    userId = getUserIdFromToken() || 1;
    console.log('=== WebSocket 초기화 ===');
    console.log('사용자 ID:', userId);
    
    connectWebSocket();
    startSession();
    startActivityTracking();
}

function connectWebSocket() {
    if (reconnectAttempts >= maxReconnectAttempts) {
        console.log('최대 재연결 시도 횟수 초과. 폴링 모드로 전환.');
        startPollingMode();
        return;
    }
    
    console.log('WebSocket 연결 시도... (시도 횟수:', reconnectAttempts + 1, ')');
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    stompClient.heartbeat.outgoing = 20000;
    stompClient.heartbeat.incoming = 20000;
    
    stompClient.connect({}, function(frame) {
        console.log('✅ WebSocket 연결 성공:', frame);
        isConnected = true;
        reconnectAttempts = 0;
        
        stompClient.subscribe('/user/queue/hints', function(message) {
            console.log('💡 힌트 수신:', message.body);
            const hintData = JSON.parse(message.body);
            showHint(hintData.message);
        });
        
        stompClient.subscribe('/topic/global-hints', function(message) {
            console.log('📢 전체 힌트 수신:', message.body);
            const hintData = JSON.parse(message.body);
            showGlobalHint(hintData.message);
        });
        
        stompClient.subscribe('/topic/teacher-announcements', function(message) {
            console.log('📝 공지 수신:', message.body);
            const announcementData = JSON.parse(message.body);
            showAnnouncement(announcementData);
        });
        
        stompClient.subscribe('/topic/quiz-broadcast', function(message) {
            console.log('⚡ 퀴즈 수신:', message.body);
            const quizData = JSON.parse(message.body);
            showQuiz(quizData);
        });
        
    }, function(error) {
        console.error('❌ WebSocket 연결 실패:', error);
        isConnected = false;
        reconnectAttempts++;
        
        setTimeout(connectWebSocket, Math.min(1000 * Math.pow(2, reconnectAttempts), 30000));
    });
}

function startPollingMode() {
    console.log('폴링 모드로 전환됨');
    setInterval(function() {
        fetch('/api/student/check-notifications?userId=' + userId)
            .then(response => response.json())
            .then(data => {
                if (data.hints) {
                    data.hints.forEach(hint => showHint(hint.message));
                }
            })
            .catch(error => console.log('폴링 실패:', error));
    }, 5000);
}

function startSession() {
    if (!userId) {
        console.error('❌ userId가 없음. 세션 시작 불가');
        return;
    }
    
    sessionId = 'session-' + Date.now() + '-' + userId;
    console.log('세션 시작 시도:', sessionId);
    
    fetch('/api/session/start', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            userId: userId,
            sessionId: sessionId
        })
    })
    .then(response => {
        console.log('세션 시작 응답 상태:', response.status);
        return response.json();
    })
    .then(data => {
        console.log('✅ 세션 시작 성공:', data);
    })
    .catch(error => {
        console.error('❌ 세션 시작 실패:', error);
    });
}

function startActivityTracking() {
    if (activityInterval) {
        clearInterval(activityInterval);
    }
    
    activityInterval = setInterval(sendActivityUpdate, 30000);
    
    document.addEventListener('visibilitychange', function() {
        if (!document.hidden) {
            sendActivityUpdate();
        }
    });
    
    sendActivityUpdate();
}

function sendActivityUpdate() {
    if (!userId) {
        console.log('userId가 없어서 활동 업데이트 생략');
        return;
    }
    
    const currentPage = window.location.pathname;
    const codeEditor = document.getElementById('codeEditor') || document.getElementById('editor-container');
    let currentCode = '';
    let isCoding = false;
    
    if (codeEditor) {
        if (typeof editor !== 'undefined' && editor) {
            currentCode = editor.getValue();
            isCoding = currentCode.length > 10;
        } else if (codeEditor.value) {
            currentCode = codeEditor.value;
            isCoding = currentCode.length > 10;
        }
    }
    
    console.log('활동 업데이트 전송:', {
        userId: userId,
        page: currentPage,
        isCoding: isCoding,
        codeLength: currentCode.length
    });
    
    if (stompClient && isConnected) {
        try {
            stompClient.send('/app/student/activity', {}, JSON.stringify({
                userId: userId,
                page: currentPage,
                code: currentCode.length > 500 ? currentCode.substring(0, 500) : currentCode,
                isCoding: isCoding
            }));
            console.log('✅ WebSocket 활동 업데이트 전송 성공');
        } catch (error) {
            console.error('❌ WebSocket 활동 업데이트 전송 실패:', error);
        }
    } else {
        console.log('WebSocket 재연결 필요');
        if (reconnectAttempts < maxReconnectAttempts) {
            connectWebSocket();
        }
    }
}

function endSession() {
    if (!userId) return;
    
    fetch('/api/session/end', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            userId: userId
        })
    })
    .catch(error => {
        console.error('❌ 세션 종료 실패:', error);
    });
    
    if (activityInterval) {
        clearInterval(activityInterval);
    }
    
    if (stompClient && isConnected) {
        stompClient.disconnect();
    }
}

function showHint(message) {
    console.log('💡 힌트 표시:', message);
    const hintPopup = document.createElement('div');
    hintPopup.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #3498db;
        color: white;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        z-index: 99999;
        max-width: 300px;
        font-family: 'Pretendard Variable', 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
        animation: slideIn 0.3s ease-out;
    `;
    
    hintPopup.innerHTML = `
        <div style="font-weight: bold; margin-bottom: 5px; color: #fff !important;">💡 선생님 힌트</div>
        <div style="color: #fff !important;">${message}</div>
    `;
    
    document.body.appendChild(hintPopup);
    
    setTimeout(() => {
        hintPopup.remove();
    }, 5000);
}

function showGlobalHint(message) {
    console.log('📢 전체 힌트 표시:', message);
    const hintPopup = document.createElement('div');
    hintPopup.style.cssText = `
        position: fixed;
        top: 20px;
        left: 50%;
        transform: translateX(-50%);
        background: #e74c3c;
        color: white;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        z-index: 10000;
        max-width: 400px;
        font-family: 'Pretendard Variable', 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
        animation: slideIn 0.3s ease-out;
    `;
    
    hintPopup.innerHTML = `
        <div style="font-weight: bold; margin-bottom: 5px;">📢 전체 공지</div>
        <div>${message}</div>
    `;
    
    document.body.appendChild(hintPopup);
    
    setTimeout(() => {
        hintPopup.remove();
    }, 8000);
}

function showAnnouncement(data) {
    console.log('📝 알림 데이터:', data);
    
    if (data.type === 'NEW_PROBLEM') {
        showProblemModal(data);
    } else if (data.type === 'NEW_MATERIAL') {
        showMaterialNotification(data);
        
        if (window.location.pathname === '/study' && typeof window.loadSharedMaterials === 'function') {
            setTimeout(() => {
                console.log('🔄 study.html 자료 목록 새로고침');
                window.loadSharedMaterials();
            }, 1500);
        }
    } else {
        showGeneralNotification(data);
    }
}

function showMaterialNotification(data) {
    console.log('📁 자료 알림 표시:', data);
    
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        left: 50%;
        transform: translateX(-50%);
        background: #27ae60;
        color: white;
        padding: 20px 25px;
        border-radius: 12px;
        box-shadow: 0 6px 20px rgba(39, 174, 96, 0.3);
        z-index: 10000;
        max-width: 450px;
        font-family: 'Pretendard Variable', 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
        animation: slideIn 0.3s ease-out;
        text-align: center;
    `;
    
    notification.innerHTML = `
        <div style="font-weight: bold; margin-bottom: 8px; font-size: 1.1rem;">📁 새로운 학습자료</div>
        <div style="margin-bottom: 8px; font-size: 1rem; font-weight: 600;">${data.title || '자료'}</div>
        <div style="font-size: 0.9rem; opacity: 0.9; margin-bottom: 12px;">${data.content || data.description || '새로운 학습자료가 공유되었습니다.'}</div>
        <div style="font-size: 0.8rem; opacity: 0.8;">오늘의 학습 페이지에서 확인하세요</div>
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateX(-50%) translateY(-20px)';
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 6000);
}

function showGeneralNotification(data) {
    console.log('📢 일반 알림 표시:', data);
    
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        left: 50%;
        transform: translateX(-50%);
        background: #3498db;
        color: white;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        z-index: 10000;
        max-width: 400px;
        font-family: 'Pretendard Variable', 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
        animation: slideIn 0.3s ease-out;
    `;
    
    notification.innerHTML = `
        <div style="font-weight: bold; margin-bottom: 5px;">📢 알림</div>
        <div>${data.title || data.message || '새로운 알림이 있습니다.'}</div>
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 5000);
}

function showProblemModal(data) {
    console.log('📝 문제 모달 표시:', data);
    
    const existingModal = document.getElementById('problemModal');
    if (existingModal) {
        existingModal.remove();
    }
    
    const modalOverlay = document.createElement('div');
    modalOverlay.id = 'problemModal';
    modalOverlay.style.cssText = `
        position: fixed !important;
        top: 0 !important;
        left: 0 !important;
        width: 100% !important;
        height: 100% !important;
        background: rgba(0, 0, 0, 0.85) !important;
        z-index: 99999 !important;
        display: flex !important;
        align-items: center !important;
        justify-content: center !important;
        font-family: 'Pretendard Variable', 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif !important;
    `;
    
    const modal = document.createElement('div');
    modal.style.cssText = `
        background: #ffffff !important;
        color: #333333 !important;
        border-radius: 16px !important;
        padding: 32px !important;
        max-width: 650px !important;
        width: 90% !important;
        max-height: 85vh !important;
        overflow-y: auto !important;
        box-shadow: 0 25px 50px rgba(0, 0, 0, 0.4) !important;
        position: relative !important;
        font-family: 'Pretendard Variable', 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif !important;
    `;
    
    modal.innerHTML = `
        <div style="border-bottom: 3px solid #3498db; padding-bottom: 18px; margin-bottom: 24px;">
            <h2 style="color: #3498db !important; margin: 0 !important; font-size: 1.6rem !important; font-weight: 700 !important; font-family: 'Pretendard Variable', 'Pretendard', sans-serif !important;">
                📝 새로운 문제
            </h2>
        </div>
        
        <div style="margin-bottom: 24px;">
            <h3 style="color: #2c3e50 !important; margin: 0 0 16px 0 !important; font-size: 1.3rem !important; font-weight: 600 !important; font-family: 'Pretendard Variable', 'Pretendard', sans-serif !important; line-height: 1.4 !important;">
                ${data.title || '문제 제목'}
            </h3>
            <div style="color: #444444 !important; line-height: 1.7 !important; font-size: 1rem !important; margin-bottom: 24px !important; white-space: pre-wrap !important; font-family: 'Pretendard Variable', 'Pretendard', sans-serif !important; background: #f8f9fa !important; padding: 16px !important; border-radius: 8px !important; border-left: 4px solid #3498db !important;">
                ${data.description || '문제 설명이 없습니다.'}
            </div>
        </div>
        
        <div style="margin-bottom: 24px;">
            <label style="display: block; margin-bottom: 10px; color: #2c3e50 !important; font-weight: 600 !important; font-family: 'Pretendard Variable', 'Pretendard', sans-serif !important; font-size: 0.95rem !important;">
                답안 작성:
            </label>
            <textarea 
                id="problemAnswer" 
                placeholder="여기에 답안을 작성하세요..."
                style="
                    width: 100% !important;
                    min-height: 140px !important;
                    padding: 16px !important;
                    border: 2px solid #e1e8ed !important;
                    border-radius: 12px !important;
                    font-size: 15px !important;
                    color: #333333 !important;
                    background: #ffffff !important;
                    resize: vertical !important;
                    font-family: 'Pretendard Variable', 'Pretendard', 'Courier New', monospace !important;
                    box-sizing: border-box !important;
                    line-height: 1.5 !important;
                    transition: border-color 0.3s ease !important;
                "
                onfocus="this.style.borderColor='#3498db'; this.style.outline='none';"
                onblur="this.style.borderColor='#e1e8ed';"
            ></textarea>
        </div>
        
        <div style="display: flex; gap: 12px; justify-content: flex-end; flex-wrap: wrap;">
            <button 
                onclick="closeProblemModal()" 
                style="
                    background: #6c757d !important;
                    color: white !important;
                    border: none !important;
                    padding: 14px 24px !important;
                    border-radius: 8px !important;
                    cursor: pointer !important;
                    font-size: 15px !important;
                    font-weight: 600 !important;
                    transition: all 0.3s ease !important;
                    font-family: 'Pretendard Variable', 'Pretendard', sans-serif !important;
                    min-width: 120px !important;
                "
                onmouseover="this.style.background='#5a6268'; this.style.transform='translateY(-1px)'"
                onmouseout="this.style.background='#6c757d'; this.style.transform='translateY(0)'"
            >
                나중에 하기
            </button>
            <button 
                onclick="submitProblemAnswer('${data.title || '문제'}')" 
                style="
                    background: #3498db !important;
                    color: white !important;
                    border: none !important;
                    padding: 14px 24px !important;
                    border-radius: 8px !important;
                    cursor: pointer !important;
                    font-size: 15px !important;
                    font-weight: 600 !important;
                    transition: all 0.3s ease !important;
                    font-family: 'Pretendard Variable', 'Pretendard', sans-serif !important;
                    min-width: 120px !important;
                "
                onmouseover="this.style.background='#2980b9'; this.style.transform='translateY(-1px)'"
                onmouseout="this.style.background='#3498db'; this.style.transform='translateY(0)'"
            >
                답안 제출
            </button>
        </div>
    `;
    
    modalOverlay.appendChild(modal);
    document.body.appendChild(modalOverlay);
    
    modalOverlay.addEventListener('click', function(e) {
        if (e.target === modalOverlay) {
            closeProblemModal();
        }
    });
    
    setTimeout(() => {
        const textarea = document.getElementById('problemAnswer');
        if (textarea) {
            textarea.focus();
        }
    }, 100);
}

function closeProblemModal() {
    const modal = document.getElementById('problemModal');
    if (modal) {
        modal.style.opacity = '0';
        modal.style.transform = 'scale(0.9)';
        setTimeout(() => {
            modal.remove();
        }, 200);
    }
}

function submitProblemAnswer(problemTitle = '문제') {
    console.log('📤 답안 제출 시도:', problemTitle);
    
    const answerTextarea = document.getElementById('problemAnswer');
    const answer = answerTextarea ? answerTextarea.value.trim() : '';
    
    if (!answer) {
        alert('답안을 작성해주세요.');
        if (answerTextarea) {
            answerTextarea.focus();
        }
        return;
    }
    
    if (!userId) {
        console.error('❌ userId가 없음. 답안 제출 불가');
        alert('사용자 정보를 확인할 수 없습니다.');
        return;
    }
    
    const submitBtn = event.target;
    const originalText = submitBtn.textContent;
    submitBtn.textContent = '제출 중...';
    submitBtn.disabled = true;
    submitBtn.style.background = '#95a5a6';
    
    console.log('📤 답안 제출 데이터:', {
        userId: userId,
        problemTitle: problemTitle,
        answer: answer,
        timestamp: new Date().toISOString()
    });
    
    fetch('/api/student/submit-answer', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            userId: userId,
            problemTitle: problemTitle,
            answer: answer,
            timestamp: new Date().toISOString()
        })
    })
    .then(response => {
        console.log('📥 답안 제출 응답 상태:', response.status);
        return response.json();
    })
    .then(data => {
        console.log('📥 답안 제출 응답:', data);
        if (data.success) {
            showSuccessMessage('답안이 성공적으로 제출되었습니다!');
            closeProblemModal();
        } else {
            alert('답안 제출에 실패했습니다: ' + (data.message || '알 수 없는 오류'));
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
            submitBtn.style.background = '#3498db';
        }
    })
    .catch(error => {
        console.error('❌ 답안 제출 오류:', error);
        alert('답안 제출 중 오류가 발생했습니다.');
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
        submitBtn.style.background = '#3498db';
    });
}

function showSuccessMessage(message) {
    console.log('✅ 성공 메시지 표시:', message);
    const successPopup = document.createElement('div');
    successPopup.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #27ae60;
        color: white;
        padding: 16px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(39, 174, 96, 0.3);
        z-index: 100000;
        max-width: 320px;
        font-family: 'Pretendard Variable', 'Pretendard', sans-serif;
        font-weight: 500;
        animation: slideInRight 0.3s ease-out;
    `;
    
    successPopup.innerHTML = `
        <div style="display: flex; align-items: center; gap: 8px;">
            <span>✅</span>
            <span>${message}</span>
        </div>
    `;
    
    document.body.appendChild(successPopup);
    
    setTimeout(() => {
        successPopup.style.opacity = '0';
        successPopup.style.transform = 'translateX(100%)';
        setTimeout(() => {
            successPopup.remove();
        }, 300);
    }, 3000);
}

function showQuiz(quizData) {
    console.log('⚡ 퀴즈 표시 함수 실행:', quizData);
    
    if (!quizData || !quizData.options) {
        console.error('❌ 퀴즈 데이터가 올바르지 않음:', quizData);
        return;
    }
    
    const existingQuiz = document.getElementById('quizPopup');
    if (existingQuiz) {
        existingQuiz.remove();
    }
    
    const popup = document.createElement('div');
    popup.id = 'quizPopup';
    popup.style.cssText = `
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: white;
        border: 3px solid #e74c3c;
        padding: 25px;
        border-radius: 16px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.4);
        z-index: 100000;
        max-width: 650px;
        width: 90%;
        font-family: 'Pretendard Variable', 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
        color: #333 !important;
    `;
    
    const title = quizData.title || '퀴즈';
    const question = quizData.question || '문제가 없습니다.';
    const options = quizData.options || [];
    const correctAnswer = quizData.correctAnswer !== undefined ? quizData.correctAnswer : 0;
    
    popup.setAttribute('data-quiz-title', title);
    popup.setAttribute('data-quiz-question', question);
    popup.setAttribute('data-correct-answer', correctAnswer);
    
    const optionsHtml = options.map((option, index) => `
        <label style="
            display: block; 
            margin: 12px 0; 
            cursor: pointer; 
            color: #333 !important;
            padding: 12px;
            border: 2px solid #e1e8ed;
            border-radius: 8px;
            transition: all 0.3s ease;
            background: #f8f9fa;
        " 
        onmouseover="this.style.borderColor='#3498db'; this.style.background='#e3f2fd';"
        onmouseout="this.style.borderColor='#e1e8ed'; this.style.background='#f8f9fa';"
        onclick="selectQuizOption(${index})">
            <input type="radio" name="quizOption" value="${index}" style="
                margin-right: 12px;
                transform: scale(1.2);
                accent-color: #3498db;
            ">
            <span style="color: #333 !important; font-size: 15px; font-weight: 500;">${index + 1}. ${option}</span>
        </label>
    `).join('');
    
    popup.innerHTML = `
        <div style="text-align: center; margin-bottom: 20px;">
            <div style="
                display: inline-block;
                background: linear-gradient(135deg, #e74c3c, #c0392b);
                color: white;
                padding: 8px 16px;
                border-radius: 20px;
                font-weight: bold;
                font-size: 14px;
                margin-bottom: 15px;
            ">⚡ 깜짝 퀴즈</div>
            <h3 style="
                color: #2c3e50 !important; 
                margin: 0 0 15px 0; 
                font-size: 1.4rem;
                font-weight: 600;
                line-height: 1.4;
            ">${title}</h3>
        </div>
        
        <div style="
            background: #f8f9fa;
            padding: 20px;
            border-radius: 12px;
            border-left: 4px solid #e74c3c;
            margin-bottom: 20px;
        ">
            <p style="
                color: #2c3e50 !important; 
                margin: 0;
                font-size: 16px;
                line-height: 1.6;
                font-weight: 500;
            ">${question}</p>
        </div>
        
        <div style="margin-bottom: 25px;">
            ${optionsHtml}
        </div>
        
        <div style="display: flex; gap: 12px; justify-content: center;">
            <button onclick="submitQuizAnswer()" style="
                background: #e74c3c; 
                color: white; 
                border: none; 
                padding: 12px 24px; 
                border-radius: 8px; 
                cursor: pointer;
                font-size: 15px;
                font-weight: 600;
                transition: all 0.3s ease;
                font-family: 'Pretendard Variable', 'Pretendard', sans-serif;
                min-width: 120px;
            " onmouseover="this.style.background='#c0392b'" onmouseout="this.style.background='#e74c3c'">답안 제출</button>
            <button onclick="closeQuizPopup()" style="
                background: #6c757d; 
                color: white; 
                border: none; 
                padding: 12px 24px; 
                border-radius: 8px; 
                cursor: pointer;
                font-size: 15px;
                font-weight: 600;
                transition: all 0.3s ease;
                font-family: 'Pretendard Variable', 'Pretendard', sans-serif;
                min-width: 120px;
            " onmouseover="this.style.background='#5a6268'" onmouseout="this.style.background='#6c757d'">건너뛰기</button>
        </div>
    `;
    
    document.body.appendChild(popup);
    console.log('⚡ 퀴즈 팝업 생성 완료');
}

function selectQuizOption(optionIndex) {
    console.log('🎯 퀴즈 옵션 선택:', optionIndex);
    
    const popup = document.getElementById('quizPopup');
    if (!popup) return;
    
    const radioButton = popup.querySelector(`input[value="${optionIndex}"]`);
    if (radioButton) {
        radioButton.checked = true;
        console.log('✅ 라디오 버튼 선택 완료:', optionIndex);
    }
    
    const allLabels = popup.querySelectorAll('label');
    allLabels.forEach((label, index) => {
        if (index === optionIndex) {
            label.style.borderColor = '#e74c3c';
            label.style.background = '#ffeaa7';
            label.style.fontWeight = '600';
        } else {
            label.style.borderColor = '#e1e8ed';
            label.style.background = '#f8f9fa';
            label.style.fontWeight = '500';
        }
    });
}

function closeQuizPopup() {
    const popup = document.getElementById('quizPopup');
    if (popup) {
        popup.style.opacity = '0';
        popup.style.transform = 'translate(-50%, -50%) scale(0.9)';
        setTimeout(() => {
            popup.remove();
        }, 200);
    }
}

function submitQuizAnswer() {
    console.log('📤 퀴즈 답안 제출 시작');
    
    const popup = document.getElementById('quizPopup');
    if (!popup) {
        console.error('❌ 퀴즈 팝업을 찾을 수 없음');
        return;
    }
    
    const selectedOption = popup.querySelector('input[name="quizOption"]:checked');
    if (!selectedOption) {
        alert('답을 선택해주세요.');
        return;
    }
    
    const userAnswer = parseInt(selectedOption.value);
    const correctAnswer = parseInt(popup.getAttribute('data-correct-answer'));
    const quizTitle = popup.getAttribute('data-quiz-title');
    const question = popup.getAttribute('data-quiz-question');
    const isCorrect = userAnswer === correctAnswer;
    
    if (!userId) {
        console.error('❌ userId가 없음. 퀴즈 답안 제출 불가');
        alert('사용자 정보를 확인할 수 없습니다.');
        return;
    }
    
    console.log('📤 퀴즈 답안 제출 데이터:', {
        userId: userId,
        quizTitle: quizTitle,
        question: question,
        userAnswer: userAnswer,
        correctAnswer: correctAnswer,
        isCorrect: isCorrect
    });
    
    const submitBtn = event.target;
    const originalText = submitBtn.textContent;
    submitBtn.textContent = '제출 중...';
    submitBtn.disabled = true;
    submitBtn.style.background = '#95a5a6';
    
    fetch('/api/student/submit-quiz', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            userId: userId,
            quizTitle: quizTitle,
            question: question,
            userAnswer: userAnswer,
            correctAnswer: correctAnswer
        })
    })
    .then(response => {
        console.log('📥 퀴즈 답안 제출 응답 상태:', response.status);
        return response.json();
    })
    .then(data => {
        console.log('📥 퀴즈 답안 제출 응답:', data);
        
        const resultMessage = data.isCorrect ? '정답입니다! 🎉' : '틀렸습니다. 정답은 ' + (correctAnswer + 1) + '번입니다.';
        const resultColor = data.isCorrect ? '#27ae60' : '#e74c3c';
        
        popup.innerHTML = `
            <div style="text-align: center; padding: 30px;">
                <div style="
                    font-size: 48px;
                    margin-bottom: 20px;
                ">${data.isCorrect ? '🎉' : '😅'}</div>
                <h3 style="
                    color: ${resultColor}; 
                    margin-bottom: 15px;
                    font-size: 1.5rem;
                    font-weight: 600;
                ">${resultMessage}</h3>
                ${data.success ? '<p style="color: #6c757d; font-size: 0.9em; margin-bottom: 20px;">답안이 저장되었습니다.</p>' : ''}
                <button onclick="closeQuizPopup()" style="
                    background: #3498db; 
                    color: white; 
                    border: none; 
                    padding: 12px 24px; 
                    border-radius: 8px; 
                    cursor: pointer;
                    font-size: 15px;
                    font-weight: 600;
                    font-family: 'Pretendard Variable', 'Pretendard', sans-serif;
                ">확인</button>
            </div>
        `;
        
        setTimeout(() => {
            closeQuizPopup();
        }, 3000);
        
    })
    .catch(error => {
        console.error('❌ 퀴즈 답안 제출 오류:', error);
        
        const resultMessage = isCorrect ? '정답입니다! 🎉' : '틀렸습니다. 정답은 ' + (correctAnswer + 1) + '번입니다.';
        const resultColor = isCorrect ? '#27ae60' : '#e74c3c';
        
        popup.innerHTML = `
            <div style="text-align: center; padding: 30px;">
                <div style="
                    font-size: 48px;
                    margin-bottom: 20px;
                ">${isCorrect ? '🎉' : '😅'}</div>
                <h3 style="
                    color: ${resultColor}; 
                    margin-bottom: 15px;
                    font-size: 1.5rem;
                    font-weight: 600;
                ">${resultMessage}</h3>
                <p style="color: #e74c3c; font-size: 0.9em; margin-bottom: 20px;">답안 저장에 실패했습니다.</p>
                <button onclick="closeQuizPopup()" style="
                    background: #3498db; 
                    color: white; 
                    border: none; 
                    padding: 12px 24px; 
                    border-radius: 8px; 
                    cursor: pointer;
                    font-size: 15px;
                    font-weight: 600;
                    font-family: 'Pretendard Variable', 'Pretendard', sans-serif;
                ">확인</button>
            </div>
        `;
        
        setTimeout(() => {
            closeQuizPopup();
        }, 3000);
    });
}

window.addEventListener('beforeunload', endSession);
window.addEventListener('load', initializeWebSocket);
document.addEventListener('DOMContentLoaded', initializeWebSocket);