(defproject timetracker "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [enlive "1.1.1"]
                 [clj-time "0.8.0"]
                 [com.novemberain/monger "2.0.0"]]
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler timetracker.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
