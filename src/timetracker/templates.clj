(ns timetracker.templates
  (:require [timetracker.models :refer [find_all find_project]] 
            [net.cgrand.enlive-html :as enlive]))

(enlive/deftemplate tpl-index "public/index.html"
  [title]
  [:#message]
    (enlive/content title)
  [:table.tasks :tbody :tr]
    (enlive/clone-for [task (find_all)]
                      [:td.time]
                         (enlive/content (str (get task :time)))
                      [:td.project_id]
                         (enlive/content (get task :project_id))
                      [:td.task]
                         (enlive/content (get task :task))))

