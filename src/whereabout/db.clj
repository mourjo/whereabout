(ns whereabout.db
  (:require [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [honey.sql.helpers :as hsql]
            [com.stuartsierra.component :as component]))



(defn init-tables
  [system]
  (jdbc/execute! (:db system)
                 (sql/format {:drop-table [:if-exists :locations]}))
  
  (jdbc/execute! (:db system)
                 (sql/format {:create-table [:locations :if-not-exists]
                              :with-columns [[:ip_address :varchar :primary-key]
                                             [:city :varchar]
                                             [:country :varchar]]})))


(defrecord DB [config]
  component/Lifecycle
  (start [component]
    (let [cfg (jdbc/get-datasource config)
          configured-component (assoc component :db cfg)]
      (init-tables configured-component)
      configured-component))
  (stop [component]
    (assoc component :db nil)))