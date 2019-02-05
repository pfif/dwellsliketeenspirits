(ns cornelius-reader.dispatcher
  (:require [re-pressed.core :as rp]
            [re-frame.core :as re-frame]
            [goog.events :refer [listen]])
  (:import [goog.events EventType]))

(defn create-event-dispatchers
  []
  (re-frame/dispatch-sync [::rp/set-keydown-rules {:event-keys [[[:cornelius-reader.events/previous-page] [{:keyCode 37}]]
                                                                [[:cornelius-reader.events/following-page] [{:keyCode 39}]]]}])
  (re-frame/dispatch-sync [::rp/add-keyboard-event-listener "keydown"])
  (if (nil? (.-PointerEvent js/window))
    (listen (.-body js/document) EventType.MOUSEMOVE (fn [ev]
                                                         (re-frame/dispatch [:cornelius-reader.events/show-metadata-ui])))
    (listen (.-body js/document) EventType.POINTERMOVE (fn [ev]
                                                         (re-frame/dispatch [:cornelius-reader.events/show-metadata-ui]))))
  )
