(ns timetracker.models
  (:refer-clojure :exclude [sort find])
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [monger.query :refer :all]
            [clj-time.core :as t]
            [clj-time.coerce :as t-c])
  (:use [clojure.string :only (split)])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

(defn now [] (new java.util.Date))

(defn create [task]
  (println (str "Added " task " at " (now) ))
  (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
        (mc/insert db coll {:task task, :time (now), :project_id "N/A", :duration "N/A"})))

(defn find_all [] 
  (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
   (with-collection db coll
        (find {})
         (fields [:task :time :project_id :duration])
         (sort (array-map :time 1)))))

(defn find_all_reverse [] 
  (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
   (with-collection db coll
        (find {})
         (fields [:task :time :project_id :duration])
         (sort (array-map :time -1)))))

(defn find_project [proj_regex] 
  (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
   (with-collection db coll
        (find {:task {$regex proj_regex}})
         (fields [:task :time :project_id :_id])
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

(defn update_duration [id duration] 
   (println "update duration called") 
   (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
        (mc/update db coll {:_id id} {$set {:duration duration}} )))

(defn time_difference [time1 time2]
  (t/in-minutes (t/interval (t-c/from-date time1) (t-c/from-date time2))))

(defn process_duration [tasks]
  ;(println "process duration called") 
  (let [f (first tasks)
        s (second tasks)
        r (rest tasks)
        d (time_difference (get f :time) (get s :time))]
        (if (>= (count r) 1)
            (do (update_duration (get f :_id) (str d))
               (process_duration r)))))

(defn process []
    ;(println "process called")
    (doseq [x (read_project_file)]
        (process_project (get x :text) (get x :code)))
    (process_duration (find_all))
 )

