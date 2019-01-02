(ns .cornelius-reader.db
  (:require [clojure.spec.alpha :as s]))

(s/def ::compiled-page-filename (s/and string? #(re-matches #"c[0-9]+p[0-9]")))
(s/def ::chapter-name string?)
(s/def ::page-progression (s/and string? #(re-matches #"[0-9]+/[0-9]+")))
(s/def ::chapter-number int?)
(s/def ::page-filename string?)

(s/def ::page (s/tuple ::compiled-page-filename ::chapter-name ::page-progression ::chapter-number ::page-filename))
(s/def ::pages (s/coll-of ::page))

(s/def ::chapters (s/coll-of ::chapter-name))

(s/def ::compiled-book (s/tuple ::pages ::chapters))

(s/def ::db (s/keys :req [::compiled-book]))
