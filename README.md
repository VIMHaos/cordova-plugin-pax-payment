# cordova-plugin-pax-payment

PAX Payment is Cordova/PhoneGap Plugin for Kompakts emulator (`sk.kompakts.emulator/sk.kompakts.emulator.MainActivity`). On the [A920 Payment Tablet Terminal](http://www.pax.us/portfolio_page/a920-payment-tablet-terminal/) is the world’s most elegantly designed and compact secure electronic payment terminal.

Follows the [Cordova Plugin spec](https://cordova.apache.org/docs/en/latest/plugin_ref/spec.html), so that it works with [Plugman](https://github.com/apache/cordova-plugman).

## Installation

This requires phonegap 7.1.0+ ( current stable v8.0.0 )

    cordova plugin add cordova-plugin-pax-payment

It is also possible to install via repo url directly ( unstable )

    cordova plugin add https://github.com/VIMHaos/cordova-plugin-pax-payment.git

### Supported Platforms

- Android

## Using the plugin ##
The plugin creates the object `cordova.plugins.PaxPayment` with the method `request(options, successCallback, errorCallback)`.

1. Register Kompakts emulator service:
```js
   cordova.plugins.PaxPayment.register("sk.kompakts.emulator",
      function (result) {
          alert(result);
      },
      function (error) {
          alert('Register fail: ' + error)
      }
   );
```

2. Make request for payment:
```js
   cordova.plugins.PaxPayment.request(
      {
          Amount: 1.00,
          Currency: 'eur',
          Operation: 'CP',
          TransactionID: '1512',
          VarSymbol: '123'
      },
      function (result) {
          alert('Payment success: ' + JSON.stringify(result));
      },
      function (error) {
          alert('Payment fail: ' + JSON.stringify(error))
      }
   );
```