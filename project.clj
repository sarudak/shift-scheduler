(defproject shift-scheduler "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [honeysql "0.4.3"]]
  :plugins [[lein-ring "0.8.11"]
            [lein-midje "3.1.3"]
            [com.jakemccrary/lein-test-refresh "0.5.2"]]
  :ring {:handler shift-scheduler.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]
                        [midje "1.6.3"]
                        [clj-time "0.8.0"]]}})
