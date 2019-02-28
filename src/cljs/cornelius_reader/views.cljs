(ns cornelius-reader.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]
            [cornelius-reader.subs :as subs]
            [cornelius-reader.responsive-image-helper :refer [srcset compiled-image-sizes sizes media-queries-and-sizes links-preload]]
            [cljsjs.hammer]))

(defn website-skeleton
  [classes elements]
  [:div.cornelius_reader {:class classes}
   (concat [[:h1#website_title [:a {:href "/"} "Dwells like teen spirits"]]
            [:div#friends]]
           elements)])

(defn loading
  []
  [:p.page.loadingpj "Chargement ..."])

(defn loading-screen
  []
  (website-skeleton
   ""
   [loading]))

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
  (let [media-queries (media-queries-and-sizes compiled-image-sizes)
        hammer (atom nil)]
    (r/create-class
     {:component-did-mount (fn [this]
                             (reset! hammer (new js/Hammer.Manager (r/dom-node this)))
                             (.add @hammer (new js/Hammer.Tap))
                             (.add @hammer (new js/Hammer.Swipe))
                             (.on @hammer "tap" (fn [ev]
                                                  (.preventDefault ev)
                                                  (dispatch [:cornelius-reader.events/toggle-metadata-ui])))
                             (.on @hammer "swiperight" (fn [ev]
                                                         (.preventDefault ev)
                                                         (dispatch [:cornelius-reader.events/previous-page])))
                             (.on @hammer "swipeleft" (fn [ev]
                                                        (.preventDefault ev)
                                                         (dispatch [:cornelius-reader.events/following-page]))))
      :reagent-render (fn [displayed image-url-beginning]
                        [:img.page (-> {:src (str image-url-beginning "-2018.jpg") ;; TODO abstract this as it is used elsewhere
                                        :srcSet (srcset image-url-beginning compiled-image-sizes)
                                        :sizes (sizes media-queries)}
                                       ((fn [props] (if displayed props (assoc props :style {:display "none"})))))])
      :component-will-unmount (fn [_]
                                (.destroy @hammer))})))
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
        should-show-prev-button @(subscribe [:cornelius-reader.subs/should-show-prev-button])
        should-show-following-button @(subscribe [:cornelius-reader.subs/should-show-following-button])
        metadata-ui-visibility-class @(subscribe [:cornelius-reader.subs/metadata-ui-visibility-class])
        ]
    (website-skeleton (str current-ui-mode-class " " metadata-ui-visibility-class)
                      [(if should-show-prev-button
                         [change-page-link "previous_page link-button" previous-page-path "Page precedante"])
                       (if showing-placeholder
                         [loading]
                         [image true current-page-image-url-beginning]
                         )
                       (if should-show-following-button
                         [change-page-link "following_page link-button" following-page-path "Page suivante"])
                       [:p.chapter_number.page_metadata {:on-click #(dispatch [:cornelius-reader.events/chapters-shown])} (str "Chapitre " chapter-number)]
                       [:p.chapter_name.page_metadata chapter-name]
                       [:p.chapter_page_progression.page_metadata page-progression]
                       [chapters]
                       (when-not showing-placeholder
                         [image false previous-page-image-url-beginning])
                       (when-not showing-placeholder
                         [image false following-page-image-url-beginning])])))




(defn main-panel
  []
  (let [current-panel @(subscribe [:cornelius-reader.subs/current-frame])]
    (case current-panel
      :loading [loading-screen]
      :reader [reader])))
