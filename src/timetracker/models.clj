(ns timetracker.models
  (:require [somnium.congomongo :as mongo]))

(defn now [] (new java.util.Date))

(defn create [task]
  (println (str "Added " task " at " (now) ))
  (mongo/insert! :tasks {:task task, :time (now)}))
