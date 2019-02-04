(ns cornelius-reader.subs
  (:require [re-frame.core :refer [reg-sub]]
            [cornelius-reader.compiled-book-reader :refer [map-paths-to-pages
                                                           chapter-to-path
                                                           page
                                                           page-url-beginning
                                                           following-page-path
                                                           previous-page-path]]))

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

(reg-sub
 ::showing-placeholder
 (fn
   [db _]
   (get db :cornelius-reader.db/showing-placeholder)))

(reg-sub
 ::metadata-ui-visibility-id
 (fn
   [db _]
   (get db :cornelius-reader.db/metadata-ui-visibility-id)))

;; Level 3

(reg-sub
::current-ui-mode-class
:<- [::showing-chapters-list]
(fn
  [showing-chapters-list _]
  (if showing-chapters-list
    "chapters-list-mode"
    "page-metadata-mode")))

(reg-sub
 ::metadata-ui-visibility-class
 :<- [::metadata-ui-visibility-id]
 (fn
   [metadata-ui-visibility-uuid]
   (if (nil? metadata-ui-visibility-uuid)
     "hide-metadata-ui"
     "show-metadata-ui")))

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

(defn previous-page-path-for-subs
  [[path paths] _]
  (previous-page-path path paths))

(reg-sub
 ::previous-page-path
 :<- [::current-path]
 :<- [::paths]
 previous-page-path-for-subs)

(defn following-page-path-for-subs
  [[path paths] _]
  (following-page-path path paths))

(reg-sub
 ::following-page-path
 :<- [::current-path]
 :<- [::paths]
 following-page-path-for-subs)

;; Level 3.3 : Determining pages

(defn page-for-subs
  [[paths-to-pages path] _]
  (page paths-to-pages path))

(reg-sub
 ::current-page
 :<- [::paths-to-pages]
 :<- [::current-path]
 page-for-subs)

(reg-sub
 ::previous-page
 :<- [::paths-to-pages]
 :<- [::previous-page-path]
 page-for-subs)

(reg-sub
 ::following-page
 :<- [::paths-to-pages]
 :<- [::following-page-path]
 page-for-subs)

;; Level 3.4 : Data based on the previous/current/following page or the current

(defn page-url-beginning-for-subs
  [[asset-server current-page] _]
  (page-url-beginning asset-server current-page))

(reg-sub
 ::current-page-url-beginning
 :<- [::asset-server]
 :<- [::current-page]
 page-url-beginning-for-subs)

(reg-sub
 ::previous-page-url-beginning
 :<- [::asset-server]
 :<- [::previous-page]
 page-url-beginning-for-subs)

(reg-sub
 ::following-page-url-beginning
 :<- [::asset-server]
 :<- [::following-page]
 page-url-beginning-for-subs)

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

(reg-sub
 ::should-show-prev-button
 :<- [::previous-page-path]
 (fn [previous-page-path _]
   (not (nil? previous-page-path))))

(reg-sub
 ::should-show-following-button
 :<- [::following-page-path]
 (fn [following-page-path _]
   (not (nil? following-page-path))))
