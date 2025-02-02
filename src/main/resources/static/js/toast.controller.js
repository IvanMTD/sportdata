function showToast(type, message){
    let $infoToast = $('#info-toast');
    let toastId = 'toast-' + Date.now(); // Уникальный идентификатор для каждого тоста
    $infoToast.append(`
        <div class="toast" role="alert" aria-live="assertive" aria-atomic="true" id="${toastId}">
            <div class="toast-header">
                <!--<img src="/img/post-master-toast-logo.png" width="48" class="rounded-2 me-2" alt="logo">-->
                <b class="me-auto">SportData</b>
                <small class="text-body-secondary">${type}</small>
                <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body">
                ${message}
            </div>
        </div>
    `);

    let toastElement = $infoToast.find(`#${toastId}`);
    let completedToast = new bootstrap.Toast(toastElement[0], {
        autohide: true,
        delay: 2000
    }); // Отключаем автоматическое закрытие
    completedToast.show();
}

function showActionToast(message, func){
    let $infoToast = $('#info-toast');
    let toastId = 'toast-' + Date.now(); // Уникальный идентификатор для каждого тоста
    $infoToast.append(`
        <div class="toast" role="alert" aria-live="assertive" aria-atomic="true" id="${toastId}">
            <div class="toast-header">
                <!--<img src="/img/post-master-toast-logo.png" width="48" class="rounded-2 me-2" alt="logo">-->
                <b class="me-auto">SportData</b>
                <small class="text-body-secondary">Внимание</small>
                <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body">
                ${message}
                <div class="mt-2 pt-2 border-top d-flex">
                    <button type="button" class="btn btn-primary btn-sm ms-auto confirm-button">Подтвердить</button>
                </div>
            </div>
        </div>
    `);

    let toastElement = $infoToast.find(`#${toastId}`);
    let completedToast = new bootstrap.Toast(toastElement[0], { autohide: false }); // Отключаем автоматическое закрытие
    completedToast.show();

    $infoToast.find(`#${toastId} .confirm-button`).on('click', function() {
        func(); // Вызов функции
        completedToast.hide(); // Скрытие тоста после выполнения функции
    });
}
