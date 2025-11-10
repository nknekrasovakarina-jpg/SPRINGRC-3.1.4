document.addEventListener("DOMContentLoaded", () => {
    const quickCreateForm = document.getElementById("quickCreateForm");
    const userTableBody = document.querySelector("#userTable tbody");
    const messageContainer = document.getElementById("messageContainer");

    // Получаем CSRF токен из meta-тегов (Thymeleaf)
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    // -----------------------------
    // Загрузка всех пользователей
    // -----------------------------
    function loadUsers() {
        fetch("/api/admin/users", {
            headers: {
                [csrfHeader]: csrfToken
            }
        })
            .then(res => res.json())
            .then(users => {
                userTableBody.innerHTML = "";
                users.forEach(user => {
                    const tr = document.createElement("tr");
                    tr.innerHTML = `
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>${user.roles.map(r => r.replace("ROLE_", "")).join(", ")}</td>
                        <td>
                            <div class="btn-group" role="group">
                                <button class="btn btn-outline-primary btn-sm" onclick="editUser(${user.id})">
                                    <i class="bi bi-pencil"></i> Изменить
                                </button>
                                <button class="btn btn-outline-info btn-sm" onclick="viewUser(${user.id})">
                                    <i class="bi bi-eye"></i> Обзор
                                </button>
                                <button class="btn btn-outline-danger btn-sm" onclick="deleteUser(${user.id})">
                                    <i class="bi bi-trash"></i> Удалить
                                </button>
                            </div>
                        </td>
                    `;
                    userTableBody.appendChild(tr);
                });
            })
            .catch(err => showMessage("Ошибка загрузки пользователей", "danger"));
    }

    loadUsers();

    // -----------------------------
    // Создание нового пользователя
    // -----------------------------
    quickCreateForm.addEventListener("submit", e => {
        e.preventDefault();
        const formData = new FormData(quickCreateForm);
        const user = {
            username: formData.get("username"),
            email: formData.get("email"),
            password: formData.get("password")
        };

        fetch("/api/admin/users", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(user)
        })
            .then(res => {
                if (!res.ok) throw new Error("Ошибка создания пользователя");
                return res.json();
            })
            .then(data => {
                quickCreateForm.reset();
                loadUsers();
                showMessage("Пользователь создан успешно!", "success");
            })
            .catch(err => showMessage(err.message, "danger"));
    });

    // -----------------------------
    // Удаление пользователя
    // -----------------------------
    window.deleteUser = function(id) {
        if (!confirm("Вы уверены, что хотите удалить пользователя?")) return;

        fetch(`/api/admin/users/${id}`, {
            method: "DELETE",
            headers: {
                [csrfHeader]: csrfToken
            }
        })
            .then(res => {
                if (!res.ok) throw new Error("Ошибка удаления пользователя");
                loadUsers();
                showMessage("Пользователь удалён", "success");
            })
            .catch(err => showMessage(err.message, "danger"));
    };

    // -----------------------------
    // Редактирование пользователя (заглушка)
    // -----------------------------
    window.editUser = function(id) {
        alert(`Редактирование пользователя ${id} пока не реализовано`);
    };

    // -----------------------------
    // Просмотр пользователя (заглушка)
    // -----------------------------
    window.viewUser = function(id) {
        alert(`Просмотр пользователя ${id} пока не реализовано`);
    };

    // -----------------------------
    // Показ сообщений
    // -----------------------------
    function showMessage(msg, type) {
        const alertDiv = document.createElement("div");
        alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
        alertDiv.innerHTML = `
            ${msg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        messageContainer.appendChild(alertDiv);
        setTimeout(() => alertDiv.remove(), 5000);
    }
});