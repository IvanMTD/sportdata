function passwordValidation(){
    validateOldPassword('old');
    validateNewPassword('new','old');
    validateConfirmPassword('repeat','new');
}

function validateOldPassword(id){
    let $input = $('#' + id);
    let $invalid = $('#' + id + '-invalid');
    $.ajax({
        url:'/api/account/password/check',
        type:'get',
        data:{
            password: $input.val()
        },
        success:function(response){
            let value = $input.val();
            if(value === ''){
                $input.addClass('is-invalid');
                $input.removeClass('is-valid');
                $invalid.text('Поле не может быть пустым');
            }else if(!response){
                $input.addClass('is-invalid');
                $input.removeClass('is-valid');
                $invalid.text('Старый пароль указан не верно');
            }else{
                $input.removeClass('is-invalid');
                $input.addClass('is-valid');
            }
        }
    });
}

function validateNewPassword(newId, oldId){
    let $input = $('#' + newId);
    let $old = $('#' + oldId);
    let $invalid = $('#' + newId + '-invalid');
    let pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&_-])[A-Za-z\d@$!%*?&_-]{8,}$/;
    let value = $input.val();
    let oldValue = $old.val();
    if(value === ''){
        $input.addClass('is-invalid');
        $input.removeClass('is-valid');
        $invalid.text('Поле не может быть пустым');
    }else if(value === oldValue){
        $input.addClass('is-invalid');
        $input.removeClass('is-valid');
        $invalid.text('Новый пароль не должен совпадать со старым');
    }else if(!pattern.test(value)){
        $input.addClass('is-invalid');
        $input.removeClass('is-valid');
        $invalid.text('Пароль не соответствует требованиям безопасности!');
    }else{
        $input.removeClass('is-invalid');
        $input.addClass('is-valid');
    }
}

function validateConfirmPassword(confirmPass,newPass){
    let $confirm = $('#' + confirmPass);
    let $newPass = $('#' + newPass);
    let $invalid = $('#' + confirmPass + '-invalid');
    let confirmValue = $confirm.val();
    let newValue = $newPass.val();

    if(confirmValue === ''){
        $confirm.addClass('is-invalid');
        $confirm.removeClass('is-valid');
        $invalid.text('Поле не может быть пустым');
    }else if(confirmValue !== newValue){
        $confirm.addClass('is-invalid');
        $confirm.removeClass('is-valid');
        $invalid.text('Не совпадает с новым паролем');
    }else{
        $confirm.removeClass('is-invalid');
        $confirm.addClass('is-valid');
    }
}