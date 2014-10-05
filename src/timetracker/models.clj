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
    (mc/insert db coll {:task task, :time (now), 
                        :project_id "N/A", 
                        :duration "N/A"})))

(defn find-all [] 
  (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
    (with-collection db coll
      (find {})
      (fields [:task :time :project_id :duration])
      (sort (array-map :time 1)))))

(defn process-project [translation-regex code]
  (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
    (mc/update db coll {:task {$regex translation-regex}}
                       {$set {:project_id code}} 
                       {:multi true})))

(defn process-file-line [[text code]]
  (hash-map :text text :code code))

(defn read-project-file []
  (with-open [rdr (clojure.java.io/reader "bar.txt")]
    (doall (map process-file-line (map #(split % #" ")  (line-seq rdr))))))

(defn update-duration [id duration] 
  ;(println "update duration called") 
  (let [conn (mg/connect)
        db   (mg/get-db conn "tasktracker")
        coll "tasks"]
    (mc/update db coll {:_id id} {$set {:duration duration}} )))

(defn time-difference [time1 time2]
  (t/in-minutes (t/interval (t-c/from-date time1) (t-c/from-date time2))))

(defn process-duration [tasks]
  ;(println "process duration called") 
  (let [f (first tasks)
        s (second tasks)
        r (rest tasks)
        d (time-difference (:time f) (:time s))]
    (if (>= (count r) 1)
      (do (update-duration (:_id f) (str d))
          (process-duration r)))))

(defn process []
  ;(println "process called")
  (doseq [x (read-project-file)]
    (process-project (:text x) (:code x)))
  (process-duration (find-all)))

