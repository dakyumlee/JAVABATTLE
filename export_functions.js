        function exportCSV() {
            const token = localStorage.getItem('authToken');
            if (!token) {
                alert('로그인이 필요합니다.');
                return;
            }
            
            fetch('/api/teacher/statistics/export/csv', {
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(response => {
                if (response.ok) {
                    return response.blob();
                } else {
                    throw new Error('CSV 내보내기 실패');
                }
            })
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'learning_statistics.csv';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
            })
            .catch(error => {
                console.error('CSV 내보내기 오류:', error);
                alert('CSV 내보내기 중 오류가 발생했습니다.');
            });
        }

        function exportPDF() {
            const token = localStorage.getItem('authToken');
            if (!token) {
                alert('로그인이 필요합니다.');
                return;
            }
            
            fetch('/api/teacher/statistics/export/pdf', {
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(response => {
                if (response.ok) {
                    return response.blob();
                } else {
                    throw new Error('PDF 내보내기 실패');
                }
            })
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'learning_statistics.pdf';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
            })
            .catch(error => {
                console.error('PDF 내보내기 오류:', error);
                alert('PDF 내보내기 중 오류가 발생했습니다.');
            });
        }
