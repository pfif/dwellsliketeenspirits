(ns cornelius-reader.compiler
  (:require [clojure.string :as str]))

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
   (map (fn [[chap-i chap pages]] (map (fn [[page-i _ page-progression]] [(str "c" chap-i "p" page-i) chap page-progression chap-i]) pages)))
   (reduce concat)))

(defn compiled-chapters
  [book]
  (keys book))

(defn compile-book!
  [input-directory output-directory]
  (let [raw-book (slurp (str/join "/" [input-directory "book.edn"]))
        book (read-string raw-book)
        compiled-pages (compiled-pages book)
        compiled-chapters (compiled-chapters book)]
    (spit
     (str/join "/" [output-directory "compiled-book.edn"])
     (pr-str [compiled-pages compiled-chapters]))))
