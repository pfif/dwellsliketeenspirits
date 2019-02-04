(ns cornelius-reader.dispatcher
  (:require [re-pressed.core :as rp]
            [re-frame.core :as re-frame]))

(defn create-event-dispatchers
  []
  (re-frame/dispatch-sync [::rp/set-keydown-rules {:event-keys [[[:cornelius-reader.events/previous-page] [{:keyCode 37}]]
                                                                [[:cornelius-reader.events/following-page] [{:keyCode 39}]]]}])
  (re-frame/dispatch-sync [::rp/add-keyboard-event-listener "keydown"])
  (set! (.-onmousemove js/window) #(re-frame/dispatch [:cornelius-reader.events/show-metadata-ui])))
