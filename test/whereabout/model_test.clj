(ns whereabout.model-test
  (:require [clojure.test :refer :all]
            [whereabout.core :as wc]
            [whereabout.model :as sut]))

(deftest lookup-test
  (let [system-1 (wc/init-system {:data-file-path "data_dump.csv"
                                  :db-file "/tmp/abcd.sqlite3"})
        system-2 (wc/init-system {:data-file-path "subset_data_dump.csv"
                                  :db-file "/tmp/efgh.sqlite3"})]
    
    (is (= {:city "DuBuquemouth", :country "Nepal"}
           (sut/find-location system-1 "200.106.141.15")
           (sut/find-location system-2 "200.106.141.15")))

    (is (= {:city "Andrehaven", :country "Niger"}
           (sut/find-location system-1 "41.166.155.45")
           (sut/find-location system-2 "41.166.155.45")))

    (is (= {:city "Port Burnice", :country "San Marino"}
           (sut/find-location system-1 "24.38.14.113")))

    (is (nil? (sut/find-location system-2 "24.38.14.113")))

    (doseq [line-number [500 1000 2000 10000 60000 700000]]
      (let [line-from-file (-> system-1 :file-data :records (nth line-number))]
        (is (= (select-keys line-from-file [:city :country])
               (sut/find-location system-1 (:ip_address line-from-file))))))))


