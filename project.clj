(defproject clowpay "0.1.1"
  :description "WebPay API wrapper library for Clojure "
  :url "https://github.com/nyampass/clowpay"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [jp.webpay/webpay "2.2.2"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.10"]]
                   :source-paths ["dev"]}})
