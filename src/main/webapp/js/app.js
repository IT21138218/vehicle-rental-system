/*
 * Light client-side helpers. The server ALWAYS validates again - this file only
 * gives faster feedback in the browser.
 */
(function () {
    'use strict';

    // 0. Success messages disappear after 6 seconds. Errors stay until the user closes them.
    document.querySelectorAll('.alert-success.alert-dismissible').forEach(function (alert) {
        setTimeout(function () {
            if (window.bootstrap && document.body.contains(alert)) {
                window.bootstrap.Alert.getOrCreateInstance(alert).close();
            }
        }, 6000);
    });

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

    // 2. Vehicle form: the type-specific field (seats / engine CC / cargo kg) changes its
    //    label and limits when another vehicle type is chosen.
    document.querySelectorAll('select[data-spec-input]').forEach(function (typeSelect) {
        var specInput = document.getElementById(typeSelect.dataset.specInput);
        var label = document.querySelector('label[for="' + specInput.id + '"]');
        var feedback = document.getElementById('specFeedback');
        function applyType() {
            var option = typeSelect.options[typeSelect.selectedIndex];
            label.textContent = option.dataset.label;
            specInput.min = option.dataset.min;
            specInput.max = option.dataset.max;
            if (feedback) {
                feedback.textContent = option.dataset.label + ' must be between '
                    + option.dataset.min + ' and ' + option.dataset.max + '.';
            }
        }
        typeSelect.addEventListener('change', applyType);
        applyType();
    });

    // 3. Booking form: live cost estimate = days x daily rate (the server re-calculates it).
    document.querySelectorAll('form[data-cost-estimate]').forEach(function (form) {
        var rate = parseFloat(form.dataset.dailyRate) || 0;
        var start = form.querySelector('[name="startDate"]');
        var end = form.querySelector('[name="endDate"]');
        function update() {
            var days = 0;
            if (start.value && end.value) {
                days = Math.round((Date.parse(end.value) - Date.parse(start.value)) / 86400000);
            }
            end.setCustomValidity(start.value && end.value && days <= 0 ? 'End date must be after the start date' : '');
            days = Math.max(days, 0);
            document.getElementById('estimateDays').textContent = days;
            document.getElementById('estimateTotal').textContent = (days * rate)
                .toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2});
        }
        start.addEventListener('change', update);
        end.addEventListener('change', update);
        update();
    });

    // 4. Bill form: the method-specific field ("Received by" / "Card last 4 digits")
    //    changes its label, pattern and hint when the payment method changes.
    document.querySelectorAll('select[data-extra-input]').forEach(function (methodSelect) {
        var input = document.getElementById(methodSelect.dataset.extraInput);
        var label = document.querySelector('label[for="' + input.id + '"]');
        var feedback = document.getElementById('extraFeedback');
        var firstRun = true;
        function applyMethod() {
            var option = methodSelect.options[methodSelect.selectedIndex];
            label.textContent = option.dataset.label;
            input.pattern = option.dataset.pattern;
            input.placeholder = option.dataset.placeholder;
            if (feedback) { feedback.textContent = option.dataset.feedback; }
            if (!firstRun && !new RegExp('^(?:' + option.dataset.pattern + ')$').test(input.value)) {
                input.value = '';
            }
            firstRun = false;
        }
        methodSelect.addEventListener('change', applyMethod);
        applyMethod();
    });

    // 5. "Confirm password" must equal the password field it points to (data-match="id").
    function checkMatchingPasswords(form) {
        form.querySelectorAll('[data-match]').forEach(function (confirmInput) {
            var original = document.getElementById(confirmInput.dataset.match);
            var mismatch = original && original.value !== confirmInput.value;
            confirmInput.setCustomValidity(mismatch ? 'Passwords do not match' : '');
        });
    }
})();
