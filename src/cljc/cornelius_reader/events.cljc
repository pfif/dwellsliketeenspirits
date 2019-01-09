(ns cornelius-reader.events
  (:require [clojure.spec.alpha :as s]
            [re-frame.core :refer [after reg-event-db reg-cofx reg-event-fx inject-cofx reg-fx]]
            [cuerdas.core :as cuerdas]
            [cornelius-reader.compiled-book-reader :refer [map-paths-to-pages paths-for-all-chapters]]))

;; Spec Validation (stolen from https://github.com/sulami/farm/blob/ba83866c1d94c37d221f19b8d2509a067ecedbc5/src/cljc/farm/events.cljc#L15)

(def check-spec-interceptor
  (after (fn [db]
           (when-not (s/valid? :cornelius-reader/db db)
             (throw (ex-info (str "spec check failed: " (s/explain-str :cornelius-reader/db db)) {}))))))

;; UNIT/EVENT : Initalization
(defn initialization
  [db [_ asset-server]]
  (-> db
      (assoc :cornelius-reader.db/compiled-book nil)
      (assoc :cornelius-reader.db/current-page-reference 0)
      (assoc :cornelius-reader.db/asset-server asset-server)))

(reg-event-db
 ::initialization
 [check-spec-interceptor]
 initialization)

;; UNIT/EVENT : Compiled book ready
(defn compiled-book-ready
  [db [_ compiled-book]]
  (-> db
      (assoc :cornelius-reader.db/compiled-book compiled-book)
      (assoc :cornelius-reader.db/current-page-reference 0)))

(reg-event-db
 ::compiled-book-ready
 [check-spec-interceptor]
 compiled-book-ready)


;; UNIT/EVENT : ::changed-page

(reg-cofx
 ::current-path
 (fn [coeffects _]
   (assoc coeffects :path #?(:cljs (.pathname window.location)
                             :default (throw "The current platform cannot get the path of the browser")))))
(defn canonical-path
  "Choose from a list of path the canonical one matching the passed one.
  For instance, if a path for chapters is passed, the path to its first page is returned.

  TODO: Make it consider URLs without trailing slash"
  [paths-for-all-pages paths-for-all-chapters path]
  (cond
    (some #{path} paths-for-all-pages) path
    (some #{path} paths-for-all-chapters) (str path "1/")
    :else (str (last paths-for-all-chapters) "1/")))

(defn changed-page
  "{current-path str?, db ratom}, target-path => List of re-frame effects

  Orders re-frame to set the current-path to a canonical path in the database.
  Use target-path and current-path in this order of preference.

  TODO: Unit test the behaviour of this function. Or break it down if it makes more sense."
  [{:keys [current-path db]} target-path]
  (let [compiled-book (get db :cornelius-reader.db/compiled-book)
        path-to-use (or target-path current-path)
        canonical-path ((canonical-path
                         (keys (map-paths-to-pages compiled-book))
                         (paths-for-all-chapters compiled-book)
                         path-to-use))]
    [::update-path canonical-path
     :db (assoc db :cornelius-reader.db/current-path canonical-path)]))

(reg-event-fx
 ::changed-page
 [(inject-cofx ::current-path)
  check-spec-interceptor]
 changed-page)

(reg-fx
 ::update-path
 (fn [url]
   #?(:cljs (.replaceState history {} "" url)
      :default (throw "The current platform cannot set the path of the browser"))))
