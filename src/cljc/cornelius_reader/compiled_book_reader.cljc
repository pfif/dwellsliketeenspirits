(ns cornelius-reader.compiled-book-reader
  (:require [cuerdas.core :refer [slug]]))

;; UNIT: paths for all pages
(defn page-to-path
  [page]
  (str "/" (slug (nth page 1)) "/" (nth page 5) "/"))

(defn map-paths-to-pages
  "combiled-book => (array-map paths => pages)

  Maps paths to pages. Used through out the codebase to :
  - Access a page associated with a URL
  - Get a list of paths by using (keys) on the result

  This returns an array-map because it maintains the order the keys were provided in.
  Note: This might become problematic performance-wise if book have more than hundread of pages."
  [compiled-book]
  (let [pages (first compiled-book)
        paths (map page-to-path pages)]
    (->>
     (interleave paths pages)
     (apply array-map))))

;; UNIT : paths for all chapters
(defn chapter-to-path
  [chapter]
  (str "/" (slug chapter) "/"))

(defn paths-for-all-chapters
  [compiled-book]
  (->>
   compiled-book
   (last)
   (map chapter-to-path)))


