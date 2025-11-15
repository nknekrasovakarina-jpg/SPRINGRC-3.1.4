window.openEditModal = (id, username, email, age, rolesJson) => {
    const roles = JSON.parse(rolesJson);

    document.getElementById('edit-id').value = id;
    document.getElementById('edit-username').value = username;
    document.getElementById('edit-email').value = email;
    document.getElementById('edit-age').value = age;
    document.getElementById('edit-password').value = '';

    const select = document.getElementById('edit-roles');
    Array.from(select.options).forEach(opt => {
        opt.selected = roles.includes(opt.value);
    });

    new bootstrap.Modal(document.getElementById('editModal')).show();
}

/* ====== SAVE EDIT ====== */
document.getElementById('edit-user-form').addEventListener('submit', async e => {
    e.preventDefault();

    const id = document.getElementById('edit-id').value;

    const body = {
        username: document.getElementById('edit-username').value,
        email: document.getElementById('edit-email').value,
        age: Number(document.getElementById('edit-age').value),
        password: document.getElementById('edit-password').value,
        roles: Array.from(document.getElementById('edit-roles').selectedOptions).map(o => o.value)
    };

    const res = await fetch(`${apiAdmin}/users/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(body)
    });

    if (!res.ok) {
        showAlert(await res.text());
        return;
    }

    await loadUsers();
    bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
    showAlert("User updated successfully!", "success");
});

/* ====== OPEN DELETE MODAL ====== */
window.openDeleteModal = (id, username, email) => {
    document.getElementById('delete-id').value = id;
    document.getElementById('delete-id-show').innerText = id;
    document.getElementById('delete-username').innerText = username;
    document.getElementById('delete-email').innerText = email;
    new bootstrap.Modal(document.getElementById('deleteModal')).show();
}

/* ====== DELETE USER ====== */
document.getElementById('delete-user-form').addEventListener('submit', async e => {
    e.preventDefault();

    const id = document.getElementById('delete-id').value;

    const res = await fetch(`${apiAdmin}/users/${id}`, {
        method: 'DELETE',
        headers: {
            [csrfHeader]: csrfToken
        }
    });

    if (!res.ok) {
        showAlert(await res.text());
        return;
    }

    await loadUsers();
    bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();
    showAlert("User deleted successfully!", "success");
});

/* ====== ADD NEW USER ====== */
document.getElementById('new-user-form').addEventListener('submit', async e => {
    e.preventDefault();

    const body = {
        username: e.target.username.value,
        email: e.target.email.value,
        age: Number(e.target.age.value),
        password: e.target.password.value,
        roles: Array.from(document.getElementById('new-user-roles').selectedOptions).map(o => o.value)
    };

    const res = await fetch(apiAdmin + '/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(body)
    });

    if (!res.ok) {
        showAlert(await res.text());
        return;
    }

    await loadUsers();
    e.target.reset();
    showAlert("User created successfully!", "success");
});

/* ====== INIT ====== */
document.addEventListener('DOMContentLoaded', async () => {
    await loadCurrentUser();
    await loadRoles();
    await loadUsers();
});