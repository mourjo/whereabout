(ns whereabout.core
  (:gen-class)
  (:require [whereabout.data-loader :as wdl]
            [clojure.pprint :as pp]))

(defn -main
  [& args]
  (pp/pprint (take 10 (wdl/do-stuff))))

