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

(defn now [] (java.util.Date.))

(defmacro with-tasks [[conn db coll] & body]
  `(let [~conn (mg/connect)
         ~db (mg/get-db ~conn "tasktracker")
         ~coll "tasks"]
     ~@body))

(defn create [task]
  (with-tasks [conn db coll]
    (mc/insert db coll {:task task, :time (now)})))

(defn find-all-tasks [] 
  (with-tasks [conn db coll]  
    (with-collection db coll
      (find {})
      (fields [:task :time :project_id :duration])
      (sort (array-map :time 1)))))

(defn update-task-code [regex-mapping]
  (with-tasks [conn db coll]  
    (mc/update db coll 
               {:task {$regex (:regex regex-mapping)}}
               {$set {:project_id (:code regex-mapping)}} 
               {:multi true})))

(defn update-task-codes [regex-mappings]
  (doseq [regex-mapping regex-mappings]
    (update-task-code regex-mapping)))

(defn parse-regex-mappings [filename]
  (with-open [rdr (clojure.java.io/reader filename)]
    (doall
      (for [line (line-seq rdr)
            :let [[text code] (split line #" ")]
            :when (and text code)]
        {:regex text :code code}))))

(defn update-task-duration [id duration] 
  (with-tasks [conn db coll]
    (mc/update db coll {:_id id} {$set {:duration duration}} )))

(defn time-difference [time1 time2]
  (t/in-minutes (t/interval (t-c/from-date time1) (t-c/from-date time2))))

(defn update-task-durations [tasks]
  (let [f (first tasks)
        s (second tasks)
        r (rest tasks)
        d (time-difference (:time f) (:time s))]
    (when (seq r)
      (update-task-duration (:_id f) (str d))
      (update-task-durations r))))

(defn process-tasks []
  (update-task-codes (parse-regex-mappings "bar.txt"))
  (update-task-durations (find-all-tasks)))

