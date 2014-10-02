(ns timetracker.models
  (:refer-clojure :exclude [sort find])
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [monger.query :refer :all])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

(defn now [] (new java.util.Date))

(defn create [task]
  (println (str "Added " task " at " (now) ))
  (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
        (mc/insert db coll {:task task, :time (now), :project_id "N/A"})))

(defn find_all [] 
  (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
   (with-collection db coll
        (find {})
         (fields [:task :time :project_id])
         (sort (array-map :time 1)))))

(defn process []
   (println "start post process ...")
   (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
        (mc/update db coll {} {$set {:project_id "Processed"}} {:multi true})))



