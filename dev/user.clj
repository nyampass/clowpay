(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [clojure.repl :refer :all]
            [clojure.pprint :refer [pp pprint]]
            [clowpay.core :refer :all]))

(def secret-key "test_secret_0m170x23m6ic8oYgHnbNmanD")

(def wp (webpay secret-key))
