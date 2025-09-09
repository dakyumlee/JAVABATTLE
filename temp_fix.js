function createProblemConfirm() {
    const title = document.getElementById('problemTitle').value.trim();
    const description = document.getElementById('problemDescription').value.trim();
    
    if (!title || !description) {
        alert('문제 제목과 설명을 모두 입력해주세요.');
        return;
    }

    fetch('/api/teacher/create-problem', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title: title, description: description })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('문제가 성공적으로 출제되었습니다!');
            closeModal('createProblemModal');
        } else {
            alert('문제 출제에 실패했습니다.');
        }
    })
    .catch(() => {
        alert('문제 출제에 실패했습니다.');
    });
}
