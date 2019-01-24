(ns cornelius-reader.events
  (:require [clojure.spec.alpha :as s]
            [re-frame.core :refer [after reg-event-db reg-cofx reg-event-fx inject-cofx reg-fx dispatch]]
            [cuerdas.core :as cuerdas]
            [cornelius-reader.compiled-book-reader :refer [map-paths-to-pages paths-for-all-chapters page
                                                           page-url-beginning]]
            [cornelius-reader.responsive-image-helper :refer [compiled-image-sizes sizes srcset
                                                              media-queries-and-sizes]]))

;; The event chain in this page:
;;
;; 1. Initialize the database (start here when initializing the module)
;; 2. Load the compiled book
;; (3. Push a new path) (start here when changing the page)
;; 4. Show placeholder
;; 5. sanitize-path
;; 6. hide-chapters
;; 7. image-starts-loading
;; 8. image-loaded
;;
;; 1. loaded

;; Spec Validation (stolen from https://github.com/sulami/farm/blob/ba83866c1d94c37d221f19b8d2509a067ecedbc5/src/cljc/farm/events.cljc#L15)

(def check-spec-interceptor
  (after (fn [db]
           (when-not (s/valid? :cornelius-reader.db/db db)
             (throw (ex-info (str "spec check failed: " (s/explain-str :cornelius-reader.db/db db)) {}))))))

;; UNIT/EVENT : Initalization
(defn initialization
  [db [_ asset-server]]
  (-> db
      (assoc :cornelius-reader.db/compiled-book nil)
      (assoc :cornelius-reader.db/current-path nil)
      (assoc :cornelius-reader.db/asset-server asset-server)
      (assoc :cornelius-reader.db/showing-chapters-list false)
      (assoc :cornelius-reader.db/showing-placeholder false)))

(reg-event-db
 ::initialization
 [check-spec-interceptor]
 initialization)

;; UNIT/EVENT : Compiled book ready
(defn compiled-book-ready
  [{:keys [db]} [_ compiled-book]]
  {:db (assoc db :cornelius-reader.db/compiled-book compiled-book)
   :dispatch [::show-placeholder]})

(reg-event-fx
 ::compiled-book-ready
 [check-spec-interceptor]
 compiled-book-ready)

;; UNIT/EVENT : ::push-new-path

(reg-event-fx
 ::path-changes
 [check-spec-interceptor]
 (fn [_ [_ new-path]]
   {::push-new-path new-path
    :dispatch [::show-placeholder]}))

(reg-fx
::push-new-path
(fn [url]
  #?(:cljs (.pushState window.history {} "" url)
     :default (throw "The current platform cannot set the path of the browser"))))

;; UNIT/EVENT : ::show-placeholder
(defn show-placeholder
  "Indicates in the state that the image is loading. "
  [{:keys [db]} _]
  {:db (assoc db :cornelius-reader.db/showing-placeholder true),
   :dispatch [::sanitize-path]})

(reg-event-fx
 ::show-placeholder
 show-placeholder)

;; UNIT/EVENT : ::sanitize-path

(reg-cofx
 ::current-path
 (fn [coeffects _]
   (assoc coeffects :current-path #?(:cljs window.location.pathname
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

(defn sanitize-path
  "{current-path str?, db ratom}, target-path => List of re-frame effects

  Orders re-frame to set the current-path to a canonical path in the database.
  Use target-path and current-path in this order of preference.

  TODO: Unit test the behaviour of this function. Or break it down if it makes more sense.
  (Maybe the target path should always be provided (it could be provided by another event, like compiled-book-ready))
  Also this would allow to actually add the page to the browser history instead of replacing it. (You have one event that changes the state and another one that corrects it)"
  [{:keys [current-path db]} _]
  (let [compiled-book (get db :cornelius-reader.db/compiled-book)
        canonical-path (canonical-path ;; TODO don't redefine function name
                         (keys (map-paths-to-pages compiled-book))
                         (paths-for-all-chapters compiled-book)
                         current-path)]
    {::update-path canonical-path
     :db (assoc db :cornelius-reader.db/current-path canonical-path)
     :dispatch [::image-starts-loading]}))

(reg-event-fx
 ::sanitize-path
 [(inject-cofx ::current-path)
  check-spec-interceptor]
 sanitize-path)

(reg-fx
 ::update-path
 (fn [url]
   #?(:cljs (.replaceState window.history {} "" url)
      :default (throw "The current platform cannot set the path of the browser"))))

;; UNITS/EVENTS : ::image-starts-loading
(reg-event-fx
 ::image-starts-loading
 (fn [{:keys [db]}]
   (let [compiled-book (get db :cornelius-reader.db/compiled-book)
         current-path (get db :cornelius-reader.db/current-path)
         current-page (-> (map-paths-to-pages compiled-book)
                          (page current-path))
         asset-server (get db :cornelius-reader.db/asset-server)
         current-page-url-beginning (page-url-beginning asset-server current-page)
         current-srcset (srcset current-page-url-beginning compiled-image-sizes)
         current-sizes (sizes (media-queries-and-sizes compiled-image-sizes))]
     {::load-image [current-page-url-beginning current-srcset current-sizes]})))

(reg-fx
 ::load-image
 (fn [[page-url-beginning srcset sizes]]
   (let [image #?(:cljs (js/Image.)
                  :default (throw "The current platform cannot create an image element"))]
     (set! (.-onload image) #(dispatch [::image-loaded]))
     (set! (.-srcset image) srcset)
     (set! (.-sizes image) sizes)
     (set! (.-src image) (str page-url-beginning "-2018.png")))))

;; UNITS/EVENTS : ::image-loaded

(reg-event-db
 ::image-loaded
 (fn [db _]
   (assoc db :cornelius-reader.db/showing-placeholder false)))

;; UNITS/EVENTS : ::show-chapters

(reg-event-db
 ::chapters-shown
 (fn [db _]
   (assoc db :cornelius-reader.db/showing-chapters-list true)))

;; UNITS/EVENTS : ::hide-chapters

(reg-event-db
 ::chapters-hidden
 (fn [db _]
   (assoc db :cornelius-reader.db/showing-chapters-list false)))
