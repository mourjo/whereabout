(ns whereabout.db
  (:require [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [com.stuartsierra.component :as component]))

(defn init-tables
  "(Re-)Initializes the tables"
  [system]
  (jdbc/execute! (:db system)
                 (sql/format {:drop-table [:if-exists :locations]}))
  
  (jdbc/execute! (:db system)
                 (sql/format {:create-table [:locations :if-not-exists]
                              :with-columns [[:ip_address :varchar :primary-key]
                                             [:city :varchar]
                                             [:country :varchar]]})))


(defrecord DB [dbtype dbname]
  component/Lifecycle
  (start [component]
    (let [cfg (jdbc/get-datasource {:dbtype dbtype :dbname dbname})
          configured-component (assoc component :db cfg)]
      (init-tables configured-component)
      configured-component))
  (stop [component]
    (assoc component :db nil)))
