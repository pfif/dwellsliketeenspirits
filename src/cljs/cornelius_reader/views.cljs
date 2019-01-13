(ns cornelius-reader.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as str]
            [cornelius-reader.subs :as subs])) ;; TODO improve loading of objects



(defn srcset-for-current-image
  "Returns the value for srcset that informs the browser of all the compiled images that exists."
  [image-url-start]
  (->
   (map #(str image-url-start "-" % ".png " % "w") [450 675 800 1200 1600 2018]) ;; TODO Centralize these values
   (str/join ",")))

(defn loading
  []
  [:div.cornelius_reader
   [:p.loading_screen "Chargement ..."]])

(defn change-page-link
  [class path text]
  [:a {:class (str class)
       :on-click (fn [e]
                   (dispatch [:cornelius-reader.events/path-changes path])
                   (.preventDefault e))
       :href path}
   [:span text]])

(defn chapters
  []
  (let [chapters-printable-list @(subscribe [:cornelius-reader.subs/chapters-printable-list])]
    [:div.chapters
     [:button {:on-click #(dispatch [:cornelius-reader.events/chapters-hidden])} "close"]
     [:ul
      (map (fn [[label path]] [:li [change-page-link "" path label]]) chapters-printable-list)]]))

(defn reader
  []
  (let [previous-page-path @(subscribe [:cornelius-reader.subs/previous-page-path])
        current-page-image-url-start @(subscribe [:cornelius-reader.subs/current-page-url-start])
        following-page-path @(subscribe [:cornelius-reader.subs/following-page-path])
        chapter-number @(subscribe [:cornelius-reader.subs/current-chapter-number])
        chapter-name @(subscribe [:cornelius-reader.subs/current-chapter-name])
        page-progression @(subscribe [:cornelius-reader.subs/current-page-progression])
        current-ui-mode-class @(subscribe [:cornelius-reader.subs/current-ui-mode-class])
        ]
    [:div.cornelius_reader {:class current-ui-mode-class}
     [change-page-link "previous_page link-button" previous-page-path "Page precedante"]
     [:img.page {:src (str current-page-image-url-start "-450.png")}]
     [change-page-link "following_page link-button" following-page-path "Page suivante"]
     [:p.chapter_number.page_metadata {:on-click #(dispatch [:cornelius-reader.events/chapters-shown])} (str "Chapitre " chapter-number)]
     [:p.chapter_name.page_metadata chapter-name]
     [:p.chapter_page_progression.page_metadata page-progression]
     [chapters]
     ]))


(defn main-panel
  []
  (let [current-panel @(subscribe [:cornelius-reader.subs/current-frame])]
    (case current-panel
      :loading [loading]
      :reader [reader])))
