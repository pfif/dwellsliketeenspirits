;; TODO This compiler overlaps with compiled-book-reader. The role of both should be specified.
(ns cornelius-reader.compiler
  (:require [clojure.string :as str]
            [clojure.java.io :refer [file]]
            [image-resizer.core :refer [resize-to-width]])
  (:import [javax.imageio ImageIO]
           [java.io File]))

;; UNIT : compiled book definition
(defn add-page-progression
  [pages]
  (map (fn [[page-i page]] [page-i page (str page-i "/" (count pages))]) pages)
  )

(defn compiled-pages
  [book]
  (->>
   book
   (map-indexed (fn [chap-i [chap pages]] [(inc chap-i) chap pages]))
   (map #(assoc % 2 (map-indexed (fn [page-i page] [(inc page-i) page]) (nth % 2))))
   (map #(assoc % 2 (add-page-progression (nth % 2))))
   (map (fn [[chap-i chap pages]] (map (fn
                                         [[page-i page-filename page-progression]]
                                         [(str "c" chap-i "p" page-i) chap page-progression chap-i page-filename page-i])
                                       pages)))
   (reduce concat)))

(defn compiled-chapters
  [book]
  (keys book))

(defn compiled-book
  [book]
  (let [compiled-pages (compiled-pages book)
        compiled-chapters (compiled-chapters book)]
    [compiled-pages compiled-chapters]))

;; UNIT : resize images
(defn resize-image!
  [input-file output-file width]
  (let [image (resize-to-width (File. input-file) width)
        converted-image (File. (str output-file ".png"))]
    (ImageIO/write image "png" converted-image)))

(defn resize-images!
  [specs]
  (dorun (map #(apply resize-image! %) specs))
  )



;; UNIT : specs for resizing images
(defn resized-image-spec
  [compiled-page input-directory output-directory]
  (->>
   [450 675 800 900 1200 1600 2018]
   (map (fn
          [width]
          [(str (file (str input-directory "/" (nth compiled-page 4))))
           (str output-directory "/" (nth compiled-page 0) "-" width)
           width]))
   ))

(defn resized-images-spec
  [compiled-pages input-directory output-directory]
  (->>
   compiled-pages
   (map #(resized-image-spec % input-directory output-directory))
   (reduce concat)))

;; GLUE FUNCTIONS

(defn compile-book!
  [input-directory output-directory]
  (let [raw-book (slurp (str/join "/" [input-directory "book.edn"]))
        book (read-string raw-book)
        compiled-book (compiled-book book)]
    (->>
     (resized-images-spec (first compiled-book) input-directory output-directory)
     (resize-images!))
    (spit
     (str output-directory  "/compiled-book.edn")
     compiled-book)))

(defn compile-book-from-repo!
  []
  (compile-book! "book" "compiled-book"))
