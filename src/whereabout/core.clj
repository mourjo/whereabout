(ns whereabout.core
  (:gen-class)
  (:require [whereabout.data-loader :as wdl]
            [whereabout.db :as wdb]
            [whereabout.model :as wm]
            [com.stuartsierra.component :as component]
            [clojure.string :as cstr]
            [clojure.tools.logging :as ctl]))

(defn init-system [config]
  (let [{:keys [data-file-path db-file skip-db-init?]} config
        system (component/start-system
                (component/system-map
                 :file-data (wdl/map->FileData {:file-path data-file-path})
                 :db (wdb/map->DB {:dbtype "sqlite" :dbname db-file :skip-init? skip-db-init?})
                 ))]
    (when-not skip-db-init?
      (wm/hydrate-records system))
    (ctl/info (format "Started the system with components [%s]"
                      (cstr/join ", " (keys system))))
    system))


(defn -main
  [& _]
  (let [system (init-system {:data-file-path "data_dump.csv"
                             :db-file "/tmp/whereabout.sqlite3"})]
    
    (prn (wm/find-location system "147.121.62.3"))))

