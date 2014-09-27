(ns timetracker.models
  (:require [somnium.congomongo :as mongo]
            [ring.util.response :as ring]))

(defn create [task]
  (println (str "Added " task))
  (mongo/insert! :robots {:name task})
  (ring/redirect "/"))
