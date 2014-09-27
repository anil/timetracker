(ns timetracker.templates
  (:require [somnium.congomongo :as mongo]
            [net.cgrand.enlive-html :refer [deftemplate content]]))

(deftemplate tpl-index "public/index.html"
  [value task]
  [:#message] (content value)
  [:#saved] (content task)
  (println (str "Added " task)) 
  (mongo/insert! :robots {:name task}))

