const state = {
    posts: [],
    newPost: {
        id: "",
        name: "",
        email: "",
        age: "",
        password: "",
        roles: [],
    },
    editPost: {
        id: "",
        name: "",
        email: "",
        age: "",
        password: "",
        roles: [],
    },
    error: "",
};


const postsList = document.querySelector(".body-table");
const getPostsBtn = document.querySelector(".posts__get-posts");
const btnDeleteUser = document.querySelector(".btn-delete-user");

function tabelFill(tableclass) {
    tableclass.innerHTML = "";
    // Формирование таблицы
    if (state.posts.length) {
        for (let i = 0; i < state.posts.length; i++) {
            tableclass.innerHTML += `
            <tr>
            <td>${state.posts[i].id}</td>
            <td>${state.posts[i].name}</td>
            <td>${state.posts[i].age}</td>
            <td>${state.posts[i].email} </td>
<!--            <td>${state.posts[i].password}</td>-->
            <td>${state.posts[i].shotRoles}</td>
            <td>
                <button
                   class="btn btn-sm edit-table-button" value=${state.posts[i].id} style="background: lightseagreen; color: white">Edit</button>
            </td>
            <td>
                <button class="btn btn-sm btn-danger delete-table-button" value=${state.posts[i].id}>Delete</button>
            </td>
        </tr>
        `;
        }
    }
    // Формирование таблицы
}

function formNewFill() {
    state.newPost.name = document.querySelector(".i_name").value;
    state.newPost.age = document.querySelector(".i_age").value;
    state.newPost.email = document.querySelector(".i_email").value;
    state.newPost.password = document.querySelector(".i_password").value;
    let formselect = document.querySelector(".i_userroles");

    state.newPost.roles.length = 0;
    for (let i = 0; i < formselect.options.length; i++) {
        if (formselect.options[i].selected) {
            let role = {id: 0, name: ""}
            role.id = i + 1;
            role.name = formselect.options[i].value;
            state.newPost.roles.push(role);
        }
    }
}

function formNewClear() {
    state.newPost.id = "";
    state.newPost.name = "";
    state.newPost.age = "";
    state.newPost.email = "";
    state.newPost.password = "";
    state.newPost.roles = [];
}

//запрос на получение всех Users
function getPostsRequest() {
    return fetch("http://localhost:8080/admin/table", {
        method: "GET",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
        },
    })
        .then((res) => res.json())
        .then((posts) => {
            state.posts = state.posts.concat(posts);
        });
}

// запрос на получение User по id
function getPostsRequest_found(id) {
    return fetch("http://localhost:8080/admin/found/" + id, {
        method: "GET",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
        },
    })
        .then((res) => res.json())
        .then((posts) => {
            state.editPost = posts;
        });
}

// Запрос на удаление User по id
function getPostsRequest_delete(id) {
    return fetch("http://localhost:8080/admin/delete/" + id, {
        method: "DELETE",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
        },
    })
        .then((res) => res.json())
        .then((posts) => {
            console.log(posts);
        });
}

// Если нажата клавиша вызова таблицы
getPostsBtn.addEventListener("click", async () => {
    state.posts.length = 0;
    await getPostsRequest();
    tabelFill(postsList);
});

// Если нажата клавиша 'DELETE' в таблице
$('tbody').on('click', ".delete-table-button", async function () {
    $('#user-modal-delete').modal('show');
    await getPostsRequest_found(this.value);
    formDeleteFill();
});

// Если нажата клавиша 'EDIT' в таблице
$('tbody').on('click', ".edit-table-button", async function () {
    $('#user-modal-edit').modal('show');
    await getPostsRequest_found(this.value);
    formEditClearMessage(); // Убираем старые замечания
    formEditFill();  // Выводим данные пользователя для редактирования
});

// Если нажата клавиша DELETE в форме удаления
btnDeleteUser.addEventListener("click", async () => {
    await getPostsRequest_delete(state.editPost.id); // удаляем User
    $('#user-modal-delete').modal('hide');  // закрываем окно

    // Вызываем перерисовку таблицы
    state.posts.length = 0;
    await getPostsRequest();
    tabelFill(postsList);
});

// Если прошла загрузка страницы
window.addEventListener("load", async () => {
    state.posts.length = 0;
    await getPostsRequest();
    tabelFill(postsList);
});

// Нажата клавиша Save в форме редактирования
// Пишем отредактированного пользователя в базу
edit_form.onsubmit = async (e) => {
    e.preventDefault();

    formEditFillSave();  // Кладем значения из формы для пересылки

    await savePostRequest();  // запоминаем

    if (!state.error) {   // Если прошло все без ошибок
        $('#user-modal-edit').modal('hide');  // закрываем окно

        // Вызываем перерисовку таблицы
        state.posts.length = 0;
        await getPostsRequest();
        tabelFill(postsList);

    } else {    // Если есть ошибки
        formEditClearMessage(); // Убираем старые замечания
        formEditPutMessage();   // Выводим замечания
    }
}


// Пишем нового пользователя
form_newuser.onsubmit = async (e) => {
    e.preventDefault();
    formNewFill();
    await createPostRequest();

    // Если нет ошибок
    if (!state.error) {

        // Чистим форму
        formNewClear();
        document.getElementById('form_newuser').reset();
        formNewClearMessage(); // Очистка замечаний
        // Чистим форму

        // Вызываем событие переключение на таблицу
        let event = new Event("click", {bubbles: true, cancelable: true})
        getPostsBtn.dispatchEvent(event);
        // Вызываем событие переключение на таблицу
    } // Если произошла ошибка выводим окно с сообщением
    else {
        formNewClearMessage();// Очистка старых замечаний
        formNewPutMessage(); // Вносим новые сообщения
    }
}

function formNewPutMessage() {
    let message = state.error.split(';');
    if (message.length) message.length = message.length - 1;
    for (let key of message) {
        let fieldname = key.split('-')[0];
        let messageInfo = key.split('-')[1];
        let divmesage = document.querySelector('div.message' + fieldname);
        let elem = document.createElement("p");
        elem.style.color = 'red';
        let elemText = document.createTextNode(messageInfo);
        elem.appendChild(elemText);
        divmesage.appendChild(elem);
    }
}

function formEditPutMessage() {
    let message = state.error.split(';');
    if (message.length) message.length = message.length - 1;
    for (let key of message) {
        let fieldname = key.split('-')[0];
        let messageInfo = key.split('-')[1];
        let divmesage = document.querySelector('div.messageEdit' + fieldname);
        let elem = document.createElement("p");
        elem.style.color = 'red';
        let elemText = document.createTextNode(messageInfo);
        elem.appendChild(elemText);
        divmesage.appendChild(elem);
    }
}


function formNewClearMessage() {
    let divname = ['div.messagename', 'div.messageage', 'div.messageemail'];
    for (let key of divname) {
        let divmessage = document.querySelector(key);
        let removeElement = document.querySelectorAll(key + " p")[0];
        if (removeElement) divmessage.removeChild(removeElement);
    }
}

function formEditClearMessage() {
    let divname = ['div.messageEditname', 'div.messageEditage', 'div.messageEditemail'];
    for (let key of divname) {
        let divmessage = document.querySelector(key);
        let removeElement = document.querySelectorAll(key + " p")[0];
        if (removeElement) divmessage.removeChild(removeElement);
    }
}

function createPostRequest() {
    return fetch("http://localhost:8080/admin/new", {
        method: "POST",
        body: JSON.stringify(state.newPost),
        headers: {
            "Content-type": "application/json; charset=UTF-8",
        },
    })
        .then((res) => res.json())
        .then((post) => {
            state.error = post.message;
        });
}


function savePostRequest() {
    return fetch("http://localhost:8080/admin/saveuser", {
        method: "POST",
        body: JSON.stringify(state.newPost),
        headers: {
            "Content-type": "application/json; charset=UTF-8",
        },
    })
        .then((res) => res.json())
        .then((post) => {
            state.error = post.message;
        });
}

// Заполнение формы редактирования
function formEditFill() {
    document.querySelector(".e_id").value = state.editPost.id;
    document.querySelector(".e_name").value = state.editPost.name;
    document.querySelector(".e_age").value = state.editPost.age;
    document.querySelector(".e_email").value = state.editPost.email;
    document.querySelector(".e_password").value = state.editPost.password;
    let formselect = document.querySelector(".e_userroles");

    for (let i = 0; i < state.editPost.roles.length; i++) {
        formselect.options[state.editPost.roles[i].id - 1].selected = 1;
    }
}


// Запоминание User для редактирования
function formEditFillSave() {
    state.newPost.id = document.querySelector(".e_id").value;
    state.newPost.name = document.querySelector(".e_name").value;
    state.newPost.age = document.querySelector(".e_age").value;
    state.newPost.email = document.querySelector(".e_email").value;
    state.newPost.password = document.querySelector(".e_password").value;
    let formselect = document.querySelector(".e_userroles");

    state.newPost.roles.length = 0;
    for (let i = 0; i < formselect.options.length; i++) {
        if (formselect.options[i].selected) {
            let role = {id: 0, name: ""}
            role.id = i + 1;
            role.name = formselect.options[i].value;
            state.newPost.roles.push(role);
        }
    }
}

// Заполнение формы DELETE
function formDeleteFill() {
    document.querySelector(".delete_form_inp_id").value = state.editPost.id;
    document.querySelector(".delete_form_inp_fN").value = state.editPost.name;
    document.querySelector(".delete_form_inp_age").value = state.editPost.age;
    document.querySelector(".delete_form_inp_mail").value = state.editPost.email;
    document.querySelector(".delete_form_inp_password").value = state.editPost.password;
    document.querySelector(".delete_form_inp_roles").value = state.editPost.shotRoles;
}





