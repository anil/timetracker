(ns timetracker.models
  (:require [somnium.congomongo :as mongo]))

(defn now [] (new java.util.Date))

(defn create [task]
  (println (str "Added " task " at " (now) ))
  (mongo/insert! :tasks {:task task, :time (now), :project_id "N/A"}))

(defn process []
  (println "start post process ...")
  (let [data (mongo/fetch-one :tasks)]
        (mongo/update! :tasks data (merge data {:project_id "processed"})))
  (println "... end  post process.")) 

