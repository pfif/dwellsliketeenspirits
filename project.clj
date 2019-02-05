(defproject dwellsliketeenspirits "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.439"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.5"]
                 [secretary "1.2.3"]
                 [image-resizer "0.1.10"]
                 [cljs-ajax "0.8.0"]
                 [funcool/cuerdas "2.0.5"]
                 [re-pressed "0.3.0"]
                 [cljsjs/hammer "2.0.8-0"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-ring "0.12.4"]]

  :auto-clean false

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target" "resources/public/compiled-book"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler dwellsliketeenspirits.server/handler}

  :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}

  :aliases {"compile-book" ["run" "-m" "cornelius-reader.compiler/compile-book-from-repo!"]
            "build-project-to-uberjar" ["do" "clean" ["cljsbuild" "once" "min"] ["ring" "uberjar"]]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"]
                   [figwheel-sidecar "0.5.16"]
                   [cider/piggieback "0.3.5"]]

    :plugins      [[lein-figwheel "0.5.16"]]
    :resource-paths ["resources"]}
   :prod {}
   }

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs" "src/cljc"]
     :figwheel     {:on-jsload "dwellsliketeenspirits.core/mount-root"}
     :compiler     {:main                 dwellsliketeenspirits.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "/js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}
                    }}

    {:id           "min"
     :source-paths ["src/cljs" "src/cljc"]
     :compiler     {:main            dwellsliketeenspirits.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}


    ]}
  :ring {:handler dwellsliketeenspirits.server/handler
         :port 80}
  :uberjar-name "server-standalone.jar"
  )
