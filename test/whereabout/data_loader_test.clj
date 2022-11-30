(ns whereabout.data-loader-test
  (:require [whereabout.data-loader :as sut]
            [clojure.test :refer :all]))


(deftest loader-test
  (let [data (sut/load-records)]
    (is (not-any? empty? (map :ip_address data)))
    (is (= ["Afghanistan" "Albania" "Algeria" "American Samoa" "Andorra"]
           (take 5 (distinct (sort (map :country data))))))
    (is (= ["Wallis and Futuna" "Western Sahara" "Yemen" "Zambia" "Zimbabwe"]
           (take-last 5 (distinct (sort (map :country data))))))))
