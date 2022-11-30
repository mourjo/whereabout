(defproject whereabout "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [ring/ring-jetty-adapter "1.9.6"]
                 [compojure "1.7.0" :exclusions [org.clojure/clojure]]
                 [org.clojure/tools.logging "1.2.4"]
                 [org.clojure/data.csv "1.0.1"]
                 [seancorfield/next.jdbc "1.1.569"]
                 [log4j/log4j "1.2.17"]
                 [org.slf4j/slf4j-log4j12 "1.7.36"]
                 [ring/ring-defaults "0.3.4"]
                 [cheshire "5.11.0"]
                 [com.stuartsierra/component "1.1.0"]
                 [org.xerial/sqlite-jdbc "3.40.0.0"]
                 [com.github.seancorfield/honeysql "2.4.947"]
                 [clj-http "3.12.3"]]
  :repl-options {:init-ns whereabout.core}
  :main whereabout.core
  :profiles {:uberjar {:aot :all}})
