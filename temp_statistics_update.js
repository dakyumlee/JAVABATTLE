        function exportCSV() {
            const token = localStorage.getItem('authToken');
            if (!token) {
                alert('로그인이 필요합니다.');
                return;
            }
            
            const link = document.createElement('a');
            link.href = `/api/teacher/statistics/export/csv?token=${token}`;
            link.download = 'learning_statistics.csv';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }

        function exportPDF() {
            const token = localStorage.getItem('authToken');
            if (!token) {
                alert('로그인이 필요합니다.');
                return;
            }
            
            window.open(`/api/teacher/statistics/export/pdf?token=${token}`, '_blank');
        }
