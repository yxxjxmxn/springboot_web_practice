// bootstrap toast alert
let toast = {
    alert: function (message, options) {
        let settings = $.extend({
            title: "알림",
            bg_color: "white",
        }, options);

        let timestamp = $.now();
        let html_toast = '<div id="liveToast_' + timestamp + '" class="toast" role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide=true data-bs-delay="10000">';
        html_toast += '<div class="toast-header bg-danger">';
        html_toast += '<i class="fas fa-bell text-' + settings.bg_color + ' me-2"></i>';
        html_toast += '<strong class="me-auto text-white">' + settings.title + '</strong>';
        html_toast += '<button type="button" class="btn-close text-white" data-bs-dismiss="toast" aria-label="Close"></button>';
        html_toast += '</div>';
        html_toast += '<div class="toast-body">';
        html_toast += message;
        html_toast += '</div>';
        html_toast += '</div>';

        $("#box_toast").append(html_toast);

        let toastLive = $("#liveToast_" + timestamp);
        let toast = new bootstrap.Toast(toastLive);
        toast.show();
    }
}