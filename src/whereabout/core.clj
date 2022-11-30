(ns whereabout.core
  (:gen-class)
  (:require
   [clojure.string :as cstr]
   [clojure.tools.logging :as ctl]
   [com.stuartsierra.component :as component]
   [whereabout.data-loader :as wdl]
   [whereabout.db :as wdb]
   [whereabout.server :as ws]
   [whereabout.model :as wm]))

(defn init-system
  [{:keys [data-file-path db-file port skip-db-init?]}]
  (let [system (component/start-system
                (component/system-map
                 :file-data (wdl/map->FileData {:file-path data-file-path
                                                :skip? skip-db-init?})
                 :db (wdb/map->DB {:dbtype "sqlite"
                                   :dbname db-file
                                   :skip-init? skip-db-init?})
                 :server (component/using
                          (ws/map->Server {:port port})
                          [:db])))]
    (when-not skip-db-init?
      (wm/hydrate-records system))
    (ctl/info (format "Started the system with components [%s]"
                      (cstr/join ", " (keys system))))
    system))


(defn -main
  [& _]
  (let [port (or (System/getenv "PORT") "8082")
        db-file (or (System/getenv "DB") "whereabout.sqlite3")
        data-file-path (or (System/getenv "DATA_FILE") "data_dump.csv")]
    (ctl/info "Starting the system with"
              {:port port
               :db-file db-file
               :data-file-path data-file-path}) 
    (init-system {:data-file-path data-file-path
                  :port (Integer/parseInt port)
                  :db-file db-file})))

