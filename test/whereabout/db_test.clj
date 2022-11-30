(ns whereabout.db-test
  (:require
   [clojure.test :refer :all]
   [com.stuartsierra.component :as component]
   [honey.sql :as sql]
   [honey.sql.helpers :as hsql]
   [next.jdbc :as jdbc]
   [whereabout.db :as sut]))

(deftest init-test
  (let [system (component/start (sut/map->DB {:dbtype "sqlite" :dbname "somefile.sqlite3"}))]
    (is (= []
           (jdbc/execute! (:db system)
                          (-> (hsql/select :* )
                              (hsql/from :locations)
                              sql/format))))

    (jdbc/execute! (:db system)
                   (-> (hsql/insert-into :locations)
                       (hsql/values [{:ip_address "1.2.3.4" :city "Kolkata" :country "India"}])
                       sql/format))

    (is (= [{:locations/ip_address "1.2.3.4" :locations/city "Kolkata" :locations/country "India"}]
           (jdbc/execute! (:db system)
                          (-> (hsql/select :* )
                              (hsql/from :locations)
                              sql/format))))))
