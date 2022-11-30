(ns whereabout.core
  (:gen-class)
  (:require [whereabout.data-loader :as wdl]
            [whereabout.db :as wdb]
            [whereabout.model :as wm]
            [com.stuartsierra.component :as component]
            [clojure.tools.logging :as ctl]))

(defn init-system [config]
  (let [{:keys [data-file-path db-file]} config]
    (component/system-map
     :file-data (wdl/map->FileData {:file-path data-file-path})
     :db (wdb/map->DB {:dbtype "sqlite" :dbname db-file})
     )))


(defn -main
  [& args]
  (let [system (init-system {:data-file-path "data_dump.csv"
                             :db-file "efgh.sqlite3"})]
    (component/start-system system)
    (whereabout.model/load-records system)
    (ctl/info "Started system")))

