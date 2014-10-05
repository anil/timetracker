(ns timetracker.templates
  (:require [timetracker.models :refer [find-all]] 
            [net.cgrand.enlive-html :as enlive]))


(enlive/deftemplate tpl-process "public/process.html" [] )

(enlive/deftemplate tpl-index "public/index.html"
  [title]
  [:#message]
    (enlive/content title)
  [:table.tasks :tbody :tr]
    (enlive/clone-for [task (find-all)]
                      [:td.time]
                        (enlive/content (str (get task :time)))
                      [:td.project_id]
                        (enlive/content (get task :project_id))
                      [:td.duration]
                        (enlive/content (get task :duration))
                      [:td.task]
                        (enlive/content (get task :task))))

