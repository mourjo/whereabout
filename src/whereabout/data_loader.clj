(ns whereabout.data-loader
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [clojure.tools.logging :as ctl])
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
  "Checks if the current record has valid data"
  [data]
  (and (valid-ip-address? data)
       ((some-fn (comp seq :country) (comp seq :city)) data)))


(defn lines->structs
  "Converts line of the input file into a hash map"
  [lines]
  (let [header-line (map keyword (first lines))]
    (keep (fn [line]
            (let [data (zipmap header-line line)]
              (when (valid-record? data)
                data)))
          (drop 1 lines))))


(defn load-records
  "Reads a file's contents, filters out invalid records and deduplicates records"
  [file-path]
  (let [now (System/nanoTime)
        lines (read-lines file-path)
        duplicated-records (lines->structs lines)
        records (->> (group-by :ip_address duplicated-records)
                     vals
                     (mapv first))]
    (ctl/info (format "Imported %,d out of %,d records successfully, in %,.2f ms"
                      (count records)
                      (dec (count lines))
                      (/ (double (- (. System (nanoTime)) now)) 1000000.0)))
    records))


(defrecord FileData [file-path]
  component/Lifecycle
  (start [component]
    (assoc component :records (load-records file-path)))
  (stop [component]
    (assoc component :records nil)))
