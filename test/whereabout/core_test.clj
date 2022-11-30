(ns whereabout.core-test
  (:require
   [clojure.test :refer :all]
   [honey.sql :as sql]
   [honey.sql.helpers :as hsql]
   [next.jdbc :as jdbc]
   [whereabout.core :as sut]
   [whereabout.model :as wm]))

(deftest init-test
  (let [system (sut/init-system {:data-file-path "subset_data_dump.csv"
                                 :db-file "/tmp/init_test.sqlite3"})]
    (is (= {:country "Morocco", :city "Willburgh"}
           (wm/find-location system "192.184.51.218")))
    (is (= {:country "Nepal", :city "DuBuquemouth"}
           (wm/find-location system "200.106.141.15")))
    (jdbc/execute! (:db system)
                   (-> (hsql/delete-from :locations)
                       (hsql/where [:not= :ip_address "200.106.141.15"])
                       sql/format)))

  (let [system (sut/init-system {:data-file-path "subset_data_dump.csv"
                                 :db-file "/tmp/init_test.sqlite3"
                                 :skip-db-init? true})]
    (is (nil? (wm/find-location system "192.184.51.218")))
    (is (= {:country "Nepal", :city "DuBuquemouth"}
           (wm/find-location system "200.106.141.15")))))
