 (defproject my-api "0.1.0-SNAPSHOT"
   :description "FIXME: write description"
   :dependencies [[org.clojure/clojure "1.9.0"]
                  [metosin/compojure-api "1.1.11"]
                  [clj-http "3.8.0"]
                  [org.clojure/data.json "0.2.6"]
                  [org.clojure/data.csv "0.1.4"]
                  [com.google.cloud/google-cloud-spanner "0.42.1-beta"]]
   :ring {:handler my-api.handler/app}
   :uberjar-name "server.jar"
   :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]
                                  [cheshire "5.5.0"]
                                  [ring/ring-mock "0.3.0"]]
                   :plugins [[lein-ring "0.12.0"]]}})
