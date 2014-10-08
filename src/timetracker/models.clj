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

(defn process-project [translation-regex code]
  (with-tasks [conn db coll]  
    (mc/update db coll 
               {:task {$regex translation-regex}}
               {$set {:project_id code}} 
               {:multi true})))

(defn process-file-line [[text code]]
  (hash-map :text text :code code))

(defn read-project-file [filename]
  (with-open [rdr (clojure.java.io/reader filename)]
    (doall (map process-file-line (map #(split % #" ")  (line-seq rdr))))))

(defn update-duration [id duration] 
  (with-tasks [conn db coll]
    (mc/update db coll {:_id id} {$set {:duration duration}} )))

(defn time-difference [time1 time2]
  (t/in-minutes (t/interval (t-c/from-date time1) (t-c/from-date time2))))

(defn process-duration [tasks]
  (let [f (first tasks)
        s (second tasks)
        r (rest tasks)
        d (time-difference (:time f) (:time s))]
    (when (seq r)
      (update-duration (:_id f) (str d))
      (process-duration r))))

(defn process-tasks []
  (doseq [x (read-project-file "bar.txt")]
    (process-project (:text x) (:code x)))
  (process-duration (find-all-tasks)))

