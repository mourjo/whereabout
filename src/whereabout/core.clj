(ns whereabout.core
  (:gen-class)
  (:require [whereabout.data-loader :as wdl]
            [whereabout.db :as wdb]
            [whereabout.model :as wm]
            [com.stuartsierra.component :as component]
            [clojure.string :as cstr]
            [clojure.tools.logging :as ctl]))

(defn init-system [config]
  (let [{:keys [data-file-path db-file]} config
        system (component/start-system
                (component/system-map
                 :file-data (wdl/map->FileData {:file-path data-file-path})
                 :db (wdb/map->DB {:dbtype "sqlite" :dbname db-file})
                 ))]
    (ctl/info (format "Started the system with components [%s]"
                      (cstr/join ", " (keys system))))
    system))


(defn -main
  [& _]
  (let [system (init-system {:data-file-path "data_dump.csv"
                             :db-file "efgh.sqlite3"})]
    (wm/load-records system)
    (prn (wm/find-location system "147.121.62.3"))))

