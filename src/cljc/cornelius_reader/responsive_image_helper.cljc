(ns cornelius-reader.responsive-image-helper
  (:require [clojure.string :as str]))

(def compiled-image-sizes [450 675 800 1200 1600 2018]) ;; TODO Centralize these values

(defn srcset
  "Returns the value for srcset that informs the browser of all the compiled images that exists."
  [image-url-beginning image-sizes]
  (->>
   (map #(str image-url-beginning "-" % ".png " % "w") image-sizes)
   (str/join ",")))

(defn media-query-for-wide-screen
  [image-width]
  (let [image-height (-> (/ 297 210) (* image-width) (int))]
    (str "(min-aspect-ratio: 210/297) and (max-height: " image-height "px) " image-width "px")))

(defn media-query-for-narrow-screen
  [image-width]
  (str "(max-aspect-ratio: 210/297) and (max-width: " image-width "px) " image-width "px"))

(defn sizes
  "Returns the value for the size parameter"
  [image-sizes]
  (let [computed-sizes (->
                        (map media-query-for-wide-screen (butlast image-sizes))
                        (concat (map media-query-for-narrow-screen (butlast image-sizes)))
                        (concat [(str (last image-sizes) "px")]))]
       (str/join "," computed-sizes)
       ))
