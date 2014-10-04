(ns timetracker.models
  (:refer-clojure :exclude [sort find])
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [monger.query :refer :all])
  (:use [clojure.string :only (split)])
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

(defn find_project [proj_regex] 
  (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
   (with-collection db coll
        (find {:task {$regex proj_regex}})
         (fields [:task :time :project_id])
         (sort (array-map :time 1)))))

(defn process_project [translation_regex code]
   (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
        (mc/update db coll {:task {$regex translation_regex}} {$set {:project_id code}} {:multi true})))

(defn process_file_line [[text code]] (hash-map :text text :code code))

(defn read_project_file []
  (with-open [rdr (clojure.java.io/reader "bar.txt")]
    (doall (map process_file_line (map #(split % #" ")  (line-seq rdr))))
  ))

(defn process []
    (println "process called")
    (doseq [x (read_project_file)]
        (process_project (get x :text) (get x :code))))

