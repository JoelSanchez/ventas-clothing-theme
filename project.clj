(defproject ventas-clothing-theme "0.1.2"
  :description "The Clothing ventas theme. Intended for clothing stores."

  :url "https://github.com/joelsanchez/ventas-clothing-theme"

  :scm {:url "git@github.com:joelsanchez/ventas-clothing-theme.git"}

  :pedantic? :abort

  :author {:name "Joel Sánchez"
           :email "webmaster@kazer.es"}

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0" :exclusions [org.clojure/spec.alpha]]
                 [ventas-core "0.1.1"]
                 [org.apache.commons/commons-compress "1.9"]]

  :plugins [[lein-ancient "0.6.15"]
            [deraen/lein-sass4clj "0.3.1" :exclusions [org.apache.commons/commons-compress]]]

  :min-lein-version "2.6.1"

  :source-paths ["src/clj" "src/cljs"]

  :test-paths ["test/clj"]

  :jvm-opts ["-XX:-OmitStackTraceInFastThrow"
             ;; Disable empty/useless menu item in OSX
             "-Dapple.awt.UIElement=true"]

  :sass {:source-paths ["src/scss"]
         :target-path "resources/public/files/css"
         :source-map true}

  :deploy-repositories {"releases" {:url "https://clojars.org/repo"
                                    :sign-releases false
                                    :username :env
                                    :password :env}
                        "snapshots" {:url "https://clojars.org/repo"
                                     :sign-releases false
                                     :username :env
                                     :password :env}}

  :profiles {:dev {:repl-options {:init-ns repl
                                  :port 4001
                                  :nrepl-middleware [cider.piggieback/wrap-cljs-repl]
                                  :timeout 120000}
                   :dependencies [[deraen/sass4clj "0.3.1" :exclusions [org.apache.commons/commons-compress]]
                                  [thheller/shadow-cljs "2.7.21" :exclusions [org.clojure/tools.reader
                                                                              com.google.guava/guava
                                                                              org.clojure/tools.cli
                                                                              commons-codec
                                                                              commons-io
                                                                              ring/ring-core]]
                                  [cider/piggieback "0.3.10" :exclusions [org.clojure/clojurescript org.clojure/tools.logging nrepl]]
                                  [binaryage/devtools "0.9.10"]
                                  [org.clojure/tools.namespace "0.3.0-alpha4"]
                                  [devcards "0.2.4" :exclusions [cljsjs/react cljsjs/react-dom org.clojure/clojurescript]]]
                   :source-paths ["dev/clj" "dev/cljs"]}})
