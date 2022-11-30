(ns whereabout.server-test
  (:require
   [cheshire.core :as cc]
   [clj-http.client :as http]
   [clojure.test :refer :all]
   [com.stuartsierra.component :as component]
   [whereabout.core :as wc])
  (:import
   (java.net ServerSocket)))

(defonce system (atom nil))

(defn open-port
  []
  (with-open [sock (ServerSocket. 0)]
    (.getLocalPort sock)))


(defn start-system
  []
  (when @system
    (component/stop-system @system))
  (let [port (open-port)
        sys (wc/init-system {:data-file-path "subset_data_dump.csv"
                             :port port
                             :db-file "/tmp/whereabout.sqlite3"})]

    (reset! system sys)
    port))


(deftest not-found-test
  (let [port (start-system)]
    (is (->> {:throw-exceptions false}
             (http/get (format "http://localhost:%d" port))
             :status
             (= 404)))

    (is (->> {:throw-exceptions false}
             (http/get (format "http://localhost:%d/xyz" port))
             :status
             (= 404)))

    (is (->> {:throw-exceptions false}
             (http/get (format "http://localhost:%d/location?ip" port))
             :status
             (= 404)))

    (is (->> {:throw-exceptions false}
             (http/get (format "http://localhost:%d/location?ip=1234" port))
             :status
             (= 404)))))


(deftest location-lookup-test
  (let [port (start-system)]
    (is (->> {:throw-exceptions false}
             (http/get (format "http://localhost:%d/location?ip=214.165.161.44" port))
             :status
             (= 200)))

    (is (->> {:throw-exceptions false}
             (http/get (format "http://localhost:%d/location?ip=96.175.82.77" port))
             :body
             cc/parse-string
             (= {"result" {"city" "Port Tyshawnton", "country" "Saint Helena"}})))))
