const API_URL = 'http://localhost:8080';

// Хранение токена
let jwtToken = localStorage.getItem('token');

// 1. Функция авторизации (Вход / Регистрация)
async function authAction(type) {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const role = document.getElementById('role-select').value;

    if (!username || !password) {
        alert("Заполните все поля");
        return;
    }

    const endpoint = type === 'login' ? '/auth/login' : '/auth/register';
    
    try {
        const response = await fetch(`${API_URL}${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, role })
        });

        if (response.ok) {
            if (type === 'login') {
                const data = await response.json();
                saveToken(data.token);
                initApp();
            } else {
                alert("Успешная регистрация! Теперь войдите.");
            }
        } else {
            const error = await response.text();
            alert("Ошибка авторизации: " + error);
        }
    } catch (err) {
        alert("Сервер недоступен");
    }
}

function saveToken(token) {
    jwtToken = token;
    localStorage.setItem('token', token);
}

function logout() {
    localStorage.removeItem('token');
    location.reload();
}

// 2. Инициализация приложения
async function initApp() {
    document.getElementById('auth-container').style.display = 'none';
    document.getElementById('app-container').style.display = 'block';
    document.getElementById('admin-panel').style.display = 'block';

    loadStats();
    loadSchedule();
}

// 3. Универсальный метод для защищенных запросов
async function securedFetch(url, options = {}) {
    const headers = {
        'Authorization': `Bearer ${jwtToken}`,
        'Content-Type': 'application/json',
        ...options.headers
    };

    try {
        const response = await fetch(`${API_URL}${url}`, { ...options, headers });

        if (response.status === 401 || response.status === 403) {
            alert("Сессия истекла или недостаточно прав (Требуется роль ADMIN для статистики)");
            logout();
            return null;
        }
        return response;
    } catch (error) {
        console.error("Сетевая ошибка при запросе к " + url, error);
        return null;
    }
}

// 4. Загрузка данных OEE и рисков
async function loadStats() {
    const res = await securedFetch('/api/equipment/stats');
    if (res && res.ok) {
        const stats = await res.json();
        // Проверяем наличие oee, чтобы избежать NaN%
        if (stats && stats.oee !== undefined && !isNaN(stats.oee)) {
            document.getElementById('oee-value').innerText = (stats.oee * 100).toFixed(1) + "%";
        } else {
            document.getElementById('oee-value').innerText = "0.0%";
        }
        document.getElementById('risk-count').innerText = stats.highRiskCount || 0;
    } else if (res) {
        const errText = await res.text();
        console.log("Статистика недоступна: " + errText);
        document.getElementById('oee-value').innerText = "Ошибка";
    }
}

// 5. Загрузка графика (Календарный вид)
async function loadSchedule() {
    const res = await securedFetch('/api/equipment/schedule');
    if (res && res.ok) {
        const schedule = await res.json();
        const tbody = document.getElementById('schedule-body');
        tbody.innerHTML = '';

        if (schedule.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;">График пуст. База данных не инициализирована.</td></tr>';
            return;
        }

        schedule.forEach(item => {
            const row = document.createElement('tr');
            if (item.failureRisk > 0.6) row.className = 'risk-high';
            else if (item.failureRisk < 0.3) row.className = 'risk-low';

            row.innerHTML = `
                <td>${item.equipmentName}</td>
                <td>${item.nextMaintenanceDate || 'Не назначена'}</td>
                <td>${(item.failureRisk * 100).toFixed(0)}%</td>
                <td class="${item.status && item.status.includes('Отложено') ? 'status-delayed' : ''}">${item.status || 'В ожидании'}</td>
            `;
            tbody.appendChild(row);
        });
    }
}

// 6. Создание ордера (Добавлена обработка ошибок сервера)
async function createOrder() {
    const eqId = document.getElementById('order-eq-id').value;
    const type = document.getElementById('order-type').value;

    if (!eqId || !type) {
        alert("Заполните поля ID оборудования и Тип обслуживания!");
        return;
    }

    const res = await securedFetch('/api/work-orders', {
        method: 'POST',
        body: JSON.stringify({
            equipment: { id: parseInt(eqId) },
            serviceType: type,
            status: 'planned',
            scheduledDate: new Date().toISOString().split('T')[0]
        })
    });

    if (res) {
        if (res.ok) {
            alert("Ордер успешно создан!");
            loadSchedule();
        } else {
            // Если сервер вернул ошибку (например, GlobalExceptionHandler)
            const errorText = await res.text();
            alert("Сервер отклонил создание ордера. Причина:\n" + errorText);
        }
    } else {
        alert("Не удалось связаться с сервером.");
    }
}

// Проверка при загрузке страницы
if (jwtToken) {
    initApp();
}