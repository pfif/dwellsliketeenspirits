(ns cornelius-reader.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [cornelius-reader.subs :as subs]
            [cornelius-reader.responsive-image-helper :refer [srcset compiled-image-sizes sizes media-queries-and-sizes links-preload]]))

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

(defn image
  [displayed image-url-beginning]
  (let [media-queries (media-queries-and-sizes compiled-image-sizes)]
    [:img.page (-> {:src (str image-url-beginning "-2018.png") ;; TODO abstract this as it is used elsewhere
                    :srcSet (srcset image-url-beginning compiled-image-sizes)
                    :sizes (sizes media-queries)
                    :id (rand-int 50)}
                   ((fn [props] (if displayed props (assoc props :style {:display "none"})))))]))
(defn reader
  []
  (let [previous-page-path @(subscribe [:cornelius-reader.subs/previous-page-path])
        following-page-path @(subscribe [:cornelius-reader.subs/following-page-path])
        previous-page-image-url-beginning @(subscribe [:cornelius-reader.subs/previous-page-url-beginning])
        current-page-image-url-beginning @(subscribe [:cornelius-reader.subs/current-page-url-beginning])
        following-page-image-url-beginning @(subscribe [:cornelius-reader.subs/following-page-url-beginning])
        chapter-number @(subscribe [:cornelius-reader.subs/current-chapter-number])
        chapter-name @(subscribe [:cornelius-reader.subs/current-chapter-name])
        page-progression @(subscribe [:cornelius-reader.subs/current-page-progression])
        current-ui-mode-class @(subscribe [:cornelius-reader.subs/current-ui-mode-class])
        showing-placeholder @(subscribe [:cornelius-reader.subs/showing-placeholder])
        ]
    [:div.cornelius_reader {:class current-ui-mode-class}
     [change-page-link "previous_page link-button" previous-page-path "Page precedante"]
     (if showing-placeholder
       [:img.page {:src "/images/spinner.svg"}]
       [image true current-page-image-url-beginning])
     [change-page-link "following_page link-button" following-page-path "Page suivante"]
     [:p.chapter_number.page_metadata {:on-click #(dispatch [:cornelius-reader.events/chapters-shown])} (str "Chapitre " chapter-number)]
     [:p.chapter_name.page_metadata chapter-name]
     [:p.chapter_page_progression.page_metadata page-progression]
     [chapters]
     (when-not showing-placeholder
       [image false previous-page-image-url-beginning])
     (when-not showing-placeholder
       [image false following-page-image-url-beginning])
     ]))


(defn main-panel
  []
  (let [current-panel @(subscribe [:cornelius-reader.subs/current-frame])]
    (case current-panel
      :loading [loading]
      :reader [reader])))
