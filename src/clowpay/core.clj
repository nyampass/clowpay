(ns clowpay.core
  (:import [jp.webpay.webpay WebPay]
           [jp.webpay.webpay.data CardRequest TokenResponse CustomerResponse
            ChargeResponse ChargeResponseList RecursionResponse RecursionResponseList]
           [java.util Date]))

(set! *warn-on-reflection* true)

(defn webpay [^String key]
  (WebPay. key))

(defn unixtime ^long [int-or-date]
  (if (integer? int-or-date)
    (long int-or-date)
    (quot (.getTime ^Date int-or-date) 1000)))

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

(defn ^ChargeResponse retrieve-charge [^WebPay webpay ^String charge-id]
  (.. webpay charge (retrieve charge-id)))

(defn retrieve-charges [^WebPay webpay
                        & {:keys [count offset created customer-id shop recursion]}]
  (let [charges (-> (.. webpay charge allRequest)
                    (cond->
                      count
                      (.count (long count))
                      offset
                      (.offset (long offset))
                      created
                      (.created (unixtime created))
                      customer-id
                      (.customer ^String customer-id)
                      shop
                      (.shop ^String shop)
                      recursion
                      (.recursion recursion))
                    .execute)]
    (seq (.data ^ChargeResponseList charges))))

(defn ^RecursionResponse create-recursion
  [^WebPay webpay amount ^String customer-id period
   & {:keys [currency shop description first-scheduled uuid]
      :or {currency "jpy"}}]
  (assert (contains? #{:month :year} period))
  (-> (.. webpay recursion createRequest)
      (.amount (long amount))
      (.currency ^String currency)
      (.customer customer-id)
      (.period (name period))
      (cond->
        shop
        (.shop ^String shop)
        description
        (.description ^String description)
        first-scheduled
        (.firstScheduled (unixtime first-scheduled))
        uuid
        (.uuid ^String uuid))
      .execute))

(defn ^RecursionResponse retrieve-recursion [^WebPay webpay ^String recursion-id]
  (.. webpay recursion (retrieve recursion-id)))

(defn ^RecursionResponse delete-recursion [^WebPay webpay ^String recursion-id]
  (.. webpay recursion (delete recursion-id)))

(defn retrieve-recursions [^WebPay webpay
                           & {:keys [count offset created customer shop suspended]}]
  (let [recursions (-> (.. webpay recursion allRequest)
                       (cond->
                         count
                         (.count (long count))
                         offset
                         (.offset (long offset))
                         created
                         (.created (unixtime created))
                         customer
                         (.customer ^String customer)
                         shop
                         (.shop ^String shop)
                         suspended
                         (.suspended (boolean suspended)))
                       .execute)]
    (seq (.data ^RecursionResponseList recursions))))
