(ns dwellsliketeenspirits.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [cornelius-reader.views :as cornelius-views]
   [cornelius-reader.events :as cornelius-events]
   [cornelius-reader.db :as cornelius-db]
   [ajax.core :refer [GET]]
   [dwellsliketeenspirits.events :as events]
   [dwellsliketeenspirits.routes :as routes]
   [dwellsliketeenspirits.views :as views]
   [dwellsliketeenspirits.config :as config]
   ))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [cornelius-views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:cornelius-reader.events/initialization config/asset-server-url])
  ;; TODO : Extract this fetcher
  (GET (str config/asset-server-url "/compiled-book.edn")
       {:handler (fn [response]
                   (re-frame/dispatch [:cornelius-reader.events/compiled-book-ready
                                       (cljs.reader/read-string response)]))})
  (dev-setup)
  (mount-root))
