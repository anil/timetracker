(ns timetracker.models
  (:require [somnium.congomongo :as mongo]))

(defn now [] (new java.util.Date))

(defn post_processor [data] (assoc-in data [:project_id] "processed"))

(defn create [task]
  (println (str "Added " task " at " (now) ))
  (mongo/insert! :tasks {:task task, :time (now), :project_id "N/A"}))

(defn process []
  (println "start post process ...")
  (let [data (mongo/fetch :tasks)
        transformed-data (doall (map post_processor data))]
        (mongo/update! :tasks data transformed-data :multiple true))
  (println "... end  post process.")) 

