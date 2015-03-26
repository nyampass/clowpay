# clowpay

WebPay API wrapper library for Clojure

## Usage

[![Clojars Project](http://clojars.org/clowpay/latest-version.svg)]

```clojure
user> (require '[clowpay.core :as cpay])
nil
;; instantiate WebPay object with secret key issued by WebPay
user> (def wp (cpay/webpay <secret-key>))
#'user/wp
;; create token from card information
user> (def token (cpay/create-token wp "4242-4242-4242-4242" 11 2016 "123" "SHOGO OHTA"))
#'user/token
;; create customer from token ID
user> (def customer (cpay/create-customer wp (.id token)))
#'user/customer
;; charge the customer 2980 yen
user> (cpay/execute-charge wp 2980 :customer (.id customer))
#<ChargeResponse jp.webpay.webpay.data.ChargeResponse[
  id: ch_1hP6U8fvrbppcrT
  object: charge
  livemode: false
  amount: 2980
  ...
  ]
]>
user> 
```

## License

Copyright © 2015 Nyampass Co. Ltd.

Distributed under the Eclipse Public License either version 1.0.
