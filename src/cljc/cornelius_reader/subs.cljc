(ns cornelius-reader.subs
  (:require [re-frame.core :refer [reg-sub]]
            [cornelius-reader.compiled-book-reader :refer [map-paths-to-pages]]))

;; Level two subscriptions
(reg-sub
 ::current-path
 (fn
   [db _]
   (get db :cornelius-reader.db/current-path)))

(reg-sub
 ::compiled-book
 (fn
   [db _]
   (get db :cornelius-reader.db/compiled-book)))

(reg-sub
 ::asset-server
 (fn
   [db _]
   (get db :cornelius-reader.db/asset-server)))


;; Level 3.1 subscriptions : any materialized state of the compiled-book

(reg-sub
 ::paths-to-pages
 :<- [::compiled-book]
 (fn [compiled-book _]
   (map-paths-to-pages compiled-book)))

(reg-sub
 ::paths
 :<- [::compiled-book]
 (fn [compiled-book _]
   (keys (map-paths-to-pages compiled-book))))

;; Level 3.2 subscription : any materialized state based on a value derived from the compiled-book and another value

(reg-sub
 ::current-page
 :<- ::paths-to-pages
 :<- ::current-path
 (fn [[paths-to-pages current-path] _]
   (get paths-to-pages current-path)))

(defn previous-page-path
  [[path paths] _]
  (->> (map (partial vector) paths (cons nil paths))
       (filter (fn [[current previous]] (= current path)))
       (first)
       (second)))

(reg-sub
 ::prevous-page-path
 :<- [::current-path]
 :<- [::paths]
 previous-page-path)

(defn following-page-path
  [[path paths] _]
  (let [shifted-list (concat (rest paths) [nil])]
       (->> (map (partial vector) paths shifted-list)
            (filter (fn [[current following]] (= current path)))
            (first)
            (second))))

(reg-sub
 ::following-page-path
 :<- [::current-path]
 :<- [::paths]
 following-page-path)

(reg-sub
 ::current-page-url-start
 :<- [::current-page]
 :<- [::asset-server]
 (fn
   [[current-page asset-server] _]
   (str current-page "/" (get current-page 0))))
