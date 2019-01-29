(ns cornelius-reader.responsive-image-helper
  (:require [clojure.string :as str]))

(def compiled-image-sizes [450 675 800 1200 1600 2018]) ;; TODO Centralize these values

(defn srcset
  "Returns the value for srcset that informs the browser of all the compiled images that exists."
  [image-url-beginning image-sizes]
  (->>
   (map #(str image-url-beginning "-" % ".jpg " % "w") image-sizes)
   (str/join ",")))

(defn a4-height
  [a4-width]
  (-> (/ 297 210) (* a4-width) (int)))

(defn media-query-for-wide-screen
  [image-width]
  (let [image-height (a4-height image-width)]
    [(str "(min-aspect-ratio: 210/297) and (max-height: " image-height "px)") image-width]))

(defn media-query-for-narrow-screen
  [image-width]
  [(str "(max-aspect-ratio: 210/297) and (max-width: " image-width "px)") image-width])

(defn last-media-query-for-wide-screen
  [image-width]
  (let [image-height (a4-height image-width)]
    [(str "(min-aspect-ratio: 210/297) and (min-height: " image-height "px)") image-width]))

(defn last-media-query-for-narrow-screen
  [image-width]
  [(str "(max-aspect-ratio: 210/297) and (min-width: " image-width "px)") image-width])

(defn sizes
  "Returns the value for the size parameter"
  [image-sizes]
  (str/join "," (->
                 (map (fn [[media-query size]] (str media-query " " size "px")) image-sizes)
                 (concat [(str (-> image-sizes last last) "px")]))))

(defn media-queries-and-sizes
  [image-sizes]
  (->
   (map media-query-for-wide-screen (butlast image-sizes))
   (concat [(last-media-query-for-wide-screen (last image-sizes))])
   (concat (map media-query-for-narrow-screen (butlast image-sizes)))
   (concat [(last-media-query-for-narrow-screen (last image-sizes))])))

;; Unused for now as browser are not ready. The final version will use another type of link preload
;; https://github.com/whatwg/html/pull/4048
(defn links-preload
  [image-url-beginning media-queries]
  (mapcat (fn [[media-query size]]
            (let [image-url (str image-url-beginning "-" size ".jpg")]
              [[:link {:rel "preload" :href image-url :as "image" :media media-query}]]))
          media-queries))
