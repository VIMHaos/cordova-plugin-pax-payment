# cordova-plugin-pax-payment

PAX Payment is Cordova/PhoneGap Plugin for Kompakts emulator (`sk.kompakts.emulator/sk.kompakts.emulator.MainActivity`).

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

A some example for payment:
```js
   cordova.plugins.PaxPayment.request(
      {
          Amount: 1.00,
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