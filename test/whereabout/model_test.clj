(ns whereabout.model-test
  (:require [whereabout.model :as sut]
            [whereabout.core :as wc]
            [clojure.test :refer :all]))


(deftest lookup-test
  (let [system (wc/init-system {:data-file-path "data_dump.csv"
                                :db-file "efgh.sqlite3"})]
    
    (sut/load-records system)
    
    (is (= {:city "Zulaufville", :country "Algeria"}
           (sut/find-location system "193.208.69.17")))

    (is (= {:city "Elzamouth", :country "India"}
           (sut/find-location system "147.121.62.3")))

    (is (nil? (sut/find-location system "invalid")))

    (doseq [line-number [500 1000 2000 10000 60000 700000]]
      (let [line-from-file (-> system :file-data :records (nth line-number))]
        (is (= (select-keys line-from-file [:city :country])
               (sut/find-location system (:ip_address line-from-file))))))))

