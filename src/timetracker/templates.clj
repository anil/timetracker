(ns timetracker.templates
  (:require [timetracker.models :refer [find-all-tasks]]
            [net.cgrand.enlive-html :as enlive]))


(enlive/deftemplate tpl-process "public/process.html" [] )

(enlive/deftemplate tpl-index "public/index.html"
  [title]
  [:#message]
    (enlive/content title)
  [:table.tasks :tbody :tr]
    (enlive/clone-for [task (find-all-tasks)]
                      [:td.time]
                        (enlive/content (str (:time task)))
                      [:td.project_id]
                        (enlive/content (:project_id task "N/A"))
                      [:td.duration]
                        (enlive/content (str (:duration task "N/A")))
                      [:td.task]
                        (enlive/content (:task task ))))

