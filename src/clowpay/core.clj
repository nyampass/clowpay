(ns clowpay.core
  (:import [jp.webpay.webpay WebPay]
           [jp.webpay.webpay.data CardRequest TokenResponse CustomerResponse ChargeResponse]))

(set! *warn-on-reflection* true)

(defn webpay [^String key]
  (WebPay. key))

(defn ^TokenResponse create-token [^WebPay webpay number exp-month exp-year cvc name
                                   & {:keys [uuid]}]
  (let [request (.. (CardRequest.)
                    (number ^String number)
                    (expMonth (long exp-month))
                    (expYear (long exp-year))
                    (cvc ^String cvc)
                    (name ^String name))]
    (.. webpay token createRequest (card request) execute)))

(defn ^TokenResponse retrieve-token [^WebPay webpay ^String token-id]
  (.. webpay token (retrieve token-id)))

(defn ^CustomerResponse create-customer [^WebPay webpay ^String token-id
                                         & {:keys [email description uuid]}]
  (-> (.. webpay customer createRequest)
      (cond->
        email
        (.email ^String email)
        description
        (.description ^String description)
        uuid
        (.uuid ^String uuid))
      .execute))

(defn ^CustomerResponse retrieve-customer [^WebPay webpay ^String customer-id]
  (.. webpay customer (retrieve customer-id)))

(defn ^ChargeResponse execute-charge
  [^WebPay webpay amount
   & {:keys [currency customer-id shop token-id description capture expire-days uuid]
      :or {currency "jpy"}}]
  (assert (or (not (nil? customer-id)) (not (nil? token-id))))
  (-> (.. webpay charge createRequest)
      (.amount (long amount))
      (.currency ^String currency)
      (cond->
        customer-id
        (.customer ^String customer-id)
        shop
        (.shop ^String shop)
        token-id
        (.card ^String token-id)
        description
        (.description ^String description)
        capture
        (.capture (boolean capture))
        expire-days
        (.expireDays (long expire-days))
        uuid
        (.uuid ^String uuid))
      .execute))
