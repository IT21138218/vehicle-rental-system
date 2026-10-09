/*
 * Light client-side helpers. The server ALWAYS validates again - this file only
 * gives faster feedback in the browser.
 */
(function () {
    'use strict';

    // 1. Bootstrap form validation: forms with class "needs-validation" are checked
    //    with the HTML5 rules (required, min, max, pattern ...) before submitting.
    document.querySelectorAll('form.needs-validation').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            checkMatchingPasswords(form);
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });

    // 2. "Confirm password" must equal the password field it points to (data-match="id").
    function checkMatchingPasswords(form) {
        form.querySelectorAll('[data-match]').forEach(function (confirmInput) {
            var original = document.getElementById(confirmInput.dataset.match);
            var mismatch = original && original.value !== confirmInput.value;
            confirmInput.setCustomValidity(mismatch ? 'Passwords do not match' : '');
        });
    }
})();
