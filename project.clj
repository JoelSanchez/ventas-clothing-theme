(defproject ventas-clothing-theme "0.1.0-SNAPSHOT"
  :description "The Clothing ventas theme. Intended for clothing stores."

  :url "https://github.com/joelsanchez/ventas-core"

  :scm {:url "git@github.com:joelsanchez/ventas.git"}

  :pedantic? :abort

  :author {:name "Joel Sánchez"
           :email "webmaster@kazer.es"}

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :repositories {"my.datomic.com"
                 ~(merge
                    {:url "https://my.datomic.com/repo"}
                    (let [username (System/getenv "DATOMIC__USERNAME")
                          password (System/getenv "DATOMIC__PASSWORD")]
                      (when (and username password)
                        {:username username
                         :password password})))}

  :dependencies [[org.clojure/clojure "1.9.0" :exclusions [org.clojure/spec.alpha]]
                 [ventas-core "0.0.12-SNAPSHOT"]]

  :plugins [[lein-ancient "0.6.15"]]

  :min-lein-version "2.6.1"

  :source-paths ["src/clj" "src/cljs"]

  :test-paths ["test/clj"]

  :jvm-opts ["-XX:-OmitStackTraceInFastThrow"
             ;; Disable empty/useless menu item in OSX
             "-Dapple.awt.UIElement=true"]

  :profiles {:dev {:repl-options {:init-ns repl
                                  :port 4001
                                  :nrepl-middleware [cider.piggieback/wrap-cljs-repl]
                                  :timeout 120000}
                   :dependencies [[ventas/devtools "0.0.11-SNAPSHOT" :exclusions [ring/ring-core
                                                                                  ring/ring-codec
                                                                                  org.clojure/tools.cli
                                                                                  org.clojure/tools.logging
                                                                                  org.jboss.logging/jboss-logging]]
                                  [cider/piggieback "0.3.10" :exclusions [org.clojure/clojurescript org.clojure/tools.logging nrepl]]
                                  [binaryage/devtools "0.9.10"]
                                  [org.clojure/tools.namespace "0.3.0-alpha4"]
                                  [devcards "0.2.4" :exclusions [cljsjs/react cljsjs/react-dom org.clojure/clojurescript]]]
                   :source-paths ["dev/clj" "dev/cljs"]}})
