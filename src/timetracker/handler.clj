(ns timetracker.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as ring]
            [timetracker.templates :refer [tpl-index]]
            [timetracker.models :refer [create process]]))

(defroutes app-routes
  (GET "/" [] (tpl-index "Anil's Task Tracker"))
  (GET "/process" [] (process) "Processed")
  (POST "/added" [task] 
      (do  
          (create task)
          (ring/redirect "/")))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
