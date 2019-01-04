(ns cornelius-reader.events
  (:require [clojure.spec.alpha :as s]
            [re-frame.core :refer [after reg-event-db]]))

;; Spec Validation (stolen from https://github.com/sulami/farm/blob/ba83866c1d94c37d221f19b8d2509a067ecedbc5/src/cljc/farm/events.cljc#L15)

(def check-spec-interceptor
  (after (fn [db]
           (when-not (s/valid? :cornelius-reader/db db)
             (throw (ex-info (str "spec check failed: " (s/explain-str :cornelius-reader/db db)) {}))))))

;; Initalization

(defn initialization
  [db [_ compiled-book]]
  (-> db
      (assoc :cornelius-reader.db/compiled-book compiled-book)
      (assoc :cornelius-reader.db/current-page-reference 0)))

(reg-event-db
 ::initialize
 [check-spec-interceptor]
 initialization)

;; Navigate pages

(defn change-page
  [db page-number]
  [])
