(ns whereabout.data-loader-test
  (:require
   [clojure.test :refer :all]
   [com.stuartsierra.component :as component]
   [whereabout.data-loader :as sut]))

(deftest loader-test
  (let [system (component/start (sut/map->FileData {:file-path "data_dump.csv"}))
        data (:records system)]
    (is (not-any? empty? (map :ip_address data)))
    (is (= ["Afghanistan" "Albania" "Algeria" "American Samoa" "Andorra"]
           (take 5 (distinct (sort (map :country data))))))
    (is (= ["Wallis and Futuna" "Western Sahara" "Yemen" "Zambia" "Zimbabwe"]
           (take-last 5 (distinct (sort (map :country data)))))))

  (let [system (component/start (sut/map->FileData {:file-path "resources/subset_data_dump.csv"}))
        data (:records system)]
    (is (= ["Afghanistan" "Albania" "Algeria" "American Samoa" "Andorra"]
           (take 5 (distinct (sort (map :country data))))))))
