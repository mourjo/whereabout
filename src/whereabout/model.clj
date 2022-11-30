(ns whereabout.model
  (:require [next.jdbc :as jdbc]
            [next.jdbc.prepare :as jdbc-prep]
            [honey.sql :as sql]
            [honey.sql.helpers :as hsql]
            [next.jdbc.result-set :as rs]))

(defn load-records
  "One-time initialization of all records read from a CSV file"
  [system]
  (jdbc/with-transaction [t (:db system)]
    (with-open [ps (jdbc/prepare t [(-> (hsql/insert-into :locations)
                                        (hsql/values [{:ip_address "" :city "" :country ""}])
                                        sql/format
                                        first)])]
      (jdbc-prep/execute-batch! ps
                                (map (juxt :ip_address :city :country) (-> system :file-data :records))
                                {:batch-size 10000})
      :done)))


(defn find-location
  "Given an IP address returns a city/country that matches the IP"
  [system ip-addr]
  (jdbc/execute-one! (:db system)
                     (-> (hsql/select :city :country)
                         (hsql/from :locations)
                         (hsql/where [:= :ip_address ip-addr])
                         sql/format)
                     {:builder-fn rs/as-unqualified-maps}))
