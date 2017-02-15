window.onload = function () {
    $("input[type=password]").keyup(function () {

        var char = $("#char");
        var pswd1 = $("#password1");
        var pwmatch = $("#pwmatch");

        if (pswd1.val().length >= 5) {
            char.removeClass("invalid");
            char.addClass("valid");
            char.css("color", "#00A41E");
        } else {
            char.removeClass("valid");
            char.addClass("invalid");
            char.css("color", "#FF0004");
        }

        if (pswd1.val() === $("#password2").val() && pswd1.val() !== "") {
            pwmatch.removeClass("invalid");
            pwmatch.addClass("valid");
            pwmatch.css("color", "#00A41E");
        } else {
            pwmatch.removeClass("valid");
            pwmatch.addClass("invalid");
            pwmatch.css("color", "#FF0004");
        }
    });
};