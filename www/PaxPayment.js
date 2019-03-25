var exec = require('cordova/exec');

var PaymentInProgress = false;

var PaxPayment = {
    register: function (name, successCallback, errorCallback) {
        exec(successCallback, errorCallback, "PaxPayment", "register", [name]);
    },
    request: function (args, successCallback, errorCallback) {
        successCallback = successCallback ? successCallback : function() {};
        errorCallback = errorCallback ? errorCallback : function() {};

        if (args instanceof Array) {
            // do nothing
        } else {
            if (typeof(args) === 'object') args = [ args ];
            else args = [];
        }

        if (typeof errorCallback != "function") {
            console.log("PaxPayment.request failure: failure callback parameter not a function");
            return;
        }

        if (typeof successCallback != "function") {
            console.log("PaxPayment.request failure: success callback parameter not a function");
            return;
        }

        if (PaymentInProgress) {
            errorCallback('Payment is already in progress');
            return;
        }

        PaymentInProgress = true;

        exec(
            function(result) {
                PaymentInProgress = false;
                successCallback(result);
            },
            function(error) {
                PaymentInProgress = false;
                errorCallback(error);
            }, 
            'PaxPayment', 
            'request', 
            args
        );
    },
};

module.exports = PaxPayment;
