(ns timetracker.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [somnium.congomongo :as mongo]
            [timetracker.templates :refer [tpl-index]]))

(def conn (mongo/make-connection "notes" :host "127.0.0.1" :port 27017))
(mongo/set-connection! conn)

(defroutes app-routes
  (GET "/" [] (tpl-index "Anil's Task Tracker" ""))
  (POST "/" [task] (tpl-index "Anil's Task Tracker" task))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
