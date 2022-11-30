(ns whereabout.data-loader
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:import [java.net InetAddress UnknownHostException]))

(defn read-lines
  "Read raw file line by line, and split each line by commas"
  [file-path]
  (with-open [reader (io/reader (io/resource file-path))]
    (doall
     (csv/read-csv reader))))


(defn valid-ip-address?
  "Check if the passed IP address is valid"
  [data]
  (boolean
   (when-let [ip (:ip_address data)]
     (when (seq ip)
       (try (InetAddress/getByName ip)
            (catch UnknownHostException _))))))


(defn valid-record?
  [data]
  (and (valid-ip-address? data)
       ((some-fn (comp seq :country) (comp seq :city)) data)))


(defn lines->structs
  [lines]
  (let [header-line (map keyword (first lines))]
    (keep (fn [line]
            (let [data (zipmap header-line line)]
              (when (valid-record? data)
                data)))
          (drop 1 lines))))


(defn do-stuff
  []
  (lines->structs (read-lines "data_dump.csv")))
