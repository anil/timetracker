(ns timetracker.templates
  (:require [somnium.congomongo :as mongo]
            [net.cgrand.enlive-html :refer [deftemplate content]]))

(deftemplate tpl-index "public/index.html"
  [title]
  [:#message] (content title)
  (println  "hit the Index ")) 


