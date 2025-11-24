const API_BASE = '/api';

let logSessionModal, createGoalModal;

document.addEventListener('DOMContentLoaded', () => {
    initializeApp();
});

function initializeApp() {
    logSessionModal = new bootstrap.Modal(document.getElementById('logSessionModal'));
    createGoalModal = new bootstrap.Modal(document.getElementById('createGoalModal'));
    
    document.querySelectorAll('.nav-tab').forEach(tab => {
        tab.addEventListener('click', (e) => switchView(e.target.dataset.view));
    });
    
    document.getElementById('quickLogBtn').addEventListener('click', () => logSessionModal.show());
    document.getElementById('quickLogBtn2').addEventListener('click', () => logSessionModal.show());
    document.getElementById('logSessionBtn').addEventListener('click', () => logSessionModal.show());
    document.getElementById('createGoalBtn').addEventListener('click', () => createGoalModal.show());
    document.getElementById('viewAllSessions').addEventListener('click', () => switchView('sessions'));
    
    document.getElementById('sessionForm').addEventListener('submit', handleSessionSubmit);
    document.getElementById('goalForm').addEventListener('submit', handleGoalSubmit);
    
    loadDashboard();
}

function switchView(view) {
    document.querySelectorAll('.nav-tab').forEach(tab => {
        tab.classList.toggle('active', tab.dataset.view === view);
    });
    
    document.querySelectorAll('.view-section').forEach(section => {
        section.classList.remove('active');
    });
    
    document.getElementById(`${view}View`).classList.add('active');
    
    if (view === 'sessions') {
        loadAllSessions();
    } else if (view === 'goals') {
        loadAllGoals();
    } else if (view === 'dashboard') {
        loadDashboard();
    }
}

async function loadDashboard() {
    await Promise.all([
        loadDashboardStats(),
        loadRecentSessions()
    ]);
}

async function loadDashboardStats() {
    try {
        const weeklyAnalytics = await fetch(`${API_BASE}/sessions/analytics/weekly`).then(r => r.json());
        const sessions = await fetch(`${API_BASE}/sessions`).then(r => r.json());
        const goals = await fetch(`${API_BASE}/goals/active`).then(r => r.json());
        
        const totalMinutes = weeklyAnalytics.totalMinutes || 0;
        document.getElementById('totalHours').textContent = (totalMinutes / 60).toFixed(1);
        
        const weekSessions = sessions.filter(s => {
            const sessionDate = new Date(s.startTime);
            const weekAgo = new Date();
            weekAgo.setDate(weekAgo.getDate() - 7);
            return sessionDate >= weekAgo;
        });
        document.getElementById('thisWeek').textContent = weekSessions.length;
        
        const streak = calculateStreak(sessions);
        document.getElementById('currentStreak').textContent = streak;
        
        const completedGoals = await countCompletedGoals(goals);
        document.getElementById('goalsCompleted').textContent = completedGoals;
        
    } catch (error) {
        console.error('Error loading dashboard stats:', error);
    }
}

async function countCompletedGoals(goals) {
    let completed = 0;
    for (const goal of goals) {
        try {
            const progress = await fetch(`${API_BASE}/goals/${goal.id}/progress`).then(r => r.json());
            if (progress.percentage >= 100) {
                completed++;
            }
        } catch (error) {
            console.error('Error checking goal progress:', error);
        }
    }
    return completed;
}

function calculateStreak(sessions) {
    if (sessions.length === 0) return 0;
    
    const dates = sessions.map(s => {
        const d = new Date(s.startTime);
        return new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime();
    });
    
    const uniqueDates = [...new Set(dates)].sort((a, b) => b - a);
    
    if (uniqueDates.length === 0) return 0;
    
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const todayTime = today.getTime();
    
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    const yesterdayTime = yesterday.getTime();
    
    if (uniqueDates[0] !== todayTime && uniqueDates[0] !== yesterdayTime) {
        return 0;
    }
    
    let streak = 1;
    for (let i = 1; i < uniqueDates.length; i++) {
        const diff = uniqueDates[i - 1] - uniqueDates[i];
        const dayDiff = diff / (1000 * 60 * 60 * 24);
        
        if (dayDiff === 1) {
            streak++;
        } else {
            break;
        }
    }
    
    return streak;
}

async function loadRecentSessions() {
    try {
        const sessions = await fetch(`${API_BASE}/sessions`).then(r => r.json());
        const recent = sessions.slice(0, 5);
        displaySessions(recent, 'dashboardSessions');
    } catch (error) {
        console.error('Error loading recent sessions:', error);
    }
}

async function loadAllSessions() {
    try {
        const sessions = await fetch(`${API_BASE}/sessions`).then(r => r.json());
        displaySessions(sessions, 'allSessions');
    } catch (error) {
        console.error('Error loading sessions:', error);
    }
}

function displaySessions(sessions, containerId) {
    const container = document.getElementById(containerId);
    
    if (sessions.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">📖</div>
                <div class="empty-text">No study sessions yet. Start logging your study time!</div>
            </div>
        `;
        return;
    }
    
    container.innerHTML = sessions.map(session => `
        <div class="session-card">
            <div class="session-header-info">
                <div class="session-subject">${escapeHtml(session.subject)}</div>
                <div class="session-duration-badge">${session.durationMinutes} min</div>
            </div>
            <div class="session-meta">
                <span>📅 ${formatDate(session.startTime)}</span>
                <span>⏰ ${formatTime(session.startTime)}</span>
            </div>
            ${session.notes ? `<div class="session-notes">${escapeHtml(session.notes)}</div>` : ''}
        </div>
    `).join('');
}

async function loadAllGoals() {
    try {
        const goals = await fetch(`${API_BASE}/goals/active`).then(r => r.json());
        
        const goalsWithProgress = await Promise.all(
            goals.map(async goal => {
                const progress = await fetch(`${API_BASE}/goals/${goal.id}/progress`).then(r => r.json());
                return { ...goal, progress };
            })
        );
        
        displayGoals(goalsWithProgress);
    } catch (error) {
        console.error('Error loading goals:', error);
    }
}

function displayGoals(goals) {
    const container = document.getElementById('allGoals');
    
    if (goals.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">🎯</div>
                <div class="empty-text">No active goals. Create a study goal to track your progress!</div>
            </div>
        `;
        return;
    }
    
    container.innerHTML = goals.map(item => {
        const goal = item;
        const progress = item.progress;
        const percentage = progress ? progress.percentage : 0;
        const current = progress ? progress.currentMinutes : 0;
        const target = goal.targetMinutes;
        
        return `
            <div class="goal-card">
                <div class="goal-header-info">
                    <div class="goal-name">${escapeHtml(goal.name)}</div>
                    <span class="goal-type-badge">${goal.type}</span>
                </div>
                <div class="progress-container">
                    <div class="progress-bar-custom">
                        <div class="progress-fill" style="width: ${Math.min(percentage, 100)}%"></div>
                    </div>
                    <div class="progress-text">
                        <span>${current} / ${target} minutes</span>
                        <span>${Math.round(percentage)}%</span>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

async function handleSessionSubmit(e) {
    e.preventDefault();
    
    const session = {
        subject: document.getElementById('subject').value,
        durationMinutes: parseInt(document.getElementById('duration').value),
        notes: document.getElementById('notes').value,
        startTime: new Date().toISOString()
    };
    
    try {
        const response = await fetch(`${API_BASE}/sessions`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(session)
        });
        
        if (response.ok) {
            document.getElementById('sessionForm').reset();
            logSessionModal.hide();
            loadDashboard();
            showToast('Session logged successfully!');
        }
    } catch (error) {
        console.error('Error creating session:', error);
        showToast('Error logging session. Please try again.');
    }
}

async function handleGoalSubmit(e) {
    e.preventDefault();
    
    const goal = {
        name: document.getElementById('goalName').value,
        targetMinutes: parseInt(document.getElementById('targetMinutes').value),
        type: document.getElementById('goalType').value,
        active: true
    };
    
    try {
        const response = await fetch(`${API_BASE}/goals`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(goal)
        });
        
        if (response.ok) {
            document.getElementById('goalForm').reset();
            createGoalModal.hide();
            loadDashboard();
            loadAllGoals();
            showToast('Goal created successfully!');
        }
    } catch (error) {
        console.error('Error creating goal:', error);
        showToast('Error creating goal. Please try again.');
    }
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
    });
}

function formatTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', {
        hour: 'numeric',
        minute: '2-digit',
        hour12: true
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showToast(message) {
    const existingToast = document.querySelector('.custom-toast');
    if (existingToast) {
        existingToast.remove();
    }
    
    const toast = document.createElement('div');
    toast.className = 'custom-toast';
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #1d1d1f;
        color: white;
        padding: 1rem 1.5rem;
        border-radius: 10px;
        font-weight: 600;
        z-index: 10000;
        animation: slideIn 0.3s ease;
    `;
    
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);
