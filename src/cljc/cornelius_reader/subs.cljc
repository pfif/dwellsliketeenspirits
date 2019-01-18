(ns cornelius-reader.subs
  (:require [re-frame.core :refer [reg-sub]]
            [cornelius-reader.compiled-book-reader :refer [map-paths-to-pages chapter-to-path]]))

;; Level two subscriptions
;; TODO turn all of these into one
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

(reg-sub
 ::showing-chapters-list
 (fn
   [db _]
   (get db :cornelius-reader.db/showing-chapters-list)))

;; Level 3

(reg-sub
::current-ui-mode-class
:<- [::showing-chapters-list]
(fn
  [showing-chapters-list _]
  (if showing-chapters-list
    "chapters-list-mode"
    "page-metadata-mode")))

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

(reg-sub
 ::current-frame
 :<- [::compiled-book]
 (fn [compiled-book _]
   (if (nil? compiled-book)
     :loading
     :reader)))

(reg-sub
 ::chapters
 :<- [::compiled-book]
 (fn [compiled-book _]
   (nth compiled-book 1)))

(reg-sub
 ::chapters-printable-list
 :<- [::chapters]
 (fn [chapters]
   (map-indexed (fn [i chapter] [(str (inc i) ". " chapter) (chapter-to-path chapter)]) chapters)))

;; Level 3.2 subscription : Determines previous/following path (current just need to be fetched at level 2)

(defn previous-page-path
  [[path paths] _]
  (->> (map (partial vector) paths (cons nil paths))
       (filter (fn [[current previous]] (= current path)))
       (first)
       (second)))

(reg-sub
 ::previous-page-path
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

;; Level 3.3 : Determining pages

(defn page
  [[paths-to-pages path] _]
  (get paths-to-pages path))

(reg-sub
 ::current-page
 :<- [::paths-to-pages]
 :<- [::current-path]
 page)

(reg-sub
 ::previous-page
 :<- [::paths-to-pages]
 :<- [::previous-page-path]
 page)

(reg-sub
 ::following-page
 :<- [::paths-to-pages]
 :<- [::following-page-path]
 page)

;; Level 3.4 : Data based on the previous/current/following page or the current

(defn page-url-beginning
  [[asset-server current-page] _]
  (str asset-server "/" (get current-page 0)))

(reg-sub
 ::current-page-url-beginning
 :<- [::asset-server]
 :<- [::current-page]
 page-url-beginning)

(reg-sub
 ::previous-page-url-beginning
 :<- [::asset-server]
 :<- [::previous-page]
 page-url-beginning)

(reg-sub
 ::following-page-url-beginning
 :<- [::asset-server]
 :<- [::following-page]
 page-url-beginning)

(reg-sub
 ::current-chapter-number
 :<- [::current-page]
 (fn [current-page _]
   (get current-page 3)))

(reg-sub
 ::current-chapter-name
 :<- [::current-page]
 (fn [current-page _]
   (get current-page 1)))

(reg-sub
 ::current-page-progression
 :<- [::current-page]
 (fn [current-page _]
   (get current-page 2)))
