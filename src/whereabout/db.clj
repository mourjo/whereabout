(ns whereabout.db
  (:require
   [clojure.tools.logging :as ctl]
   [com.stuartsierra.component :as component]
   [honey.sql :as sql]
   [next.jdbc :as jdbc]))

(defn init-tables
  "(Re-)Initializes the tables"
  [system drop-table?]
  (when drop-table?
    (ctl/info "Dropping existing table")
    (jdbc/execute! (:db system)
                   (sql/format {:drop-table [:if-exists :locations]})))

  (ctl/info "Creating locations table if not exists")
  (jdbc/execute! (:db system)
                 (sql/format {:create-table [:locations :if-not-exists]
                              :with-columns [[:ip_address :varchar :primary-key]
                                             [:city :varchar]
                                             [:country :varchar]]})))


(defrecord DB [dbtype dbname skip-init?]
  component/Lifecycle
  (start [component]
    (let [cfg (jdbc/get-datasource {:dbtype dbtype :dbname dbname})
          configured-component (assoc component :db cfg)]
      (init-tables configured-component (not skip-init?))
      configured-component))
  (stop [component]
    (assoc component :db nil)))
