(ns timetracker.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [somnium.congomongo :as mongo]
            [ring.util.response :as ring]
            [timetracker.templates :refer [tpl-index]]
            [timetracker.models :refer [create]]))

(def conn (mongo/make-connection "tasktracker" :host "127.0.0.1" :port 27017))
(mongo/set-connection! conn)

(defroutes app-routes
  (GET "/" [] (tpl-index "Anil's Task Tracker"))
  (POST "/added" [task] 
      (do  
          (create task)
          (ring/redirect "/")))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
