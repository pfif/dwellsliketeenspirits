(ns .cornelius-reader.db
  (:require [clojure.spec.alpha :as s]))

(def s/def ::compiled-page-filename (s/and string? #(re-matches #"c[0-9]+p[0-9]")))
(def s/def ::chapter-name string?)
(def s/def ::page-progression (s/and string? #(re-matches #"[0-9]+/[0-9]+")))
(def s/def ::chapter-number int?)
(def s/def ::page-filename string?)

(def s/def ::page (s/tuple ::compiled-page-filename ::chapter-name ::page-progression ::chapter-number ::page-filename))
(def s/def ::pages (s/coll-of ::page))

(def s/def ::chapters (s/coll-of ::chapter-name))

(def s/def ::compiled-book (s/tuple ::pages ::chapters))

(def s/def ::db (s/keys :req [::compiled-book]))
