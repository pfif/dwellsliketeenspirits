(ns cornelius-reader.db
  (:require [clojure.spec.alpha :as s]))

;; Compiled book
(s/def ::compiled-page-filename (s/and string? #(re-matches #"c[0-9]+p[0-9]+" %)))
(s/def ::chapter-name string?)
(s/def ::page-progression (s/and string? #(re-matches #"[0-9]+/[0-9]+" %)))
(s/def ::chapter-number int?)
(s/def ::page-filename string?)
(s/def ::page-i int?)

(s/def ::page (s/tuple ::compiled-page-filename ::chapter-name ::page-progression ::chapter-number ::page-filename ::page-i))
(s/def ::pages (s/coll-of ::page))

(s/def ::chapters (s/coll-of ::chapter-name))

(s/def ::compiled-book (s/nilable (s/tuple ::pages ::chapters)))

;; UI state
(s/def ::current-path (s/nilable (s/and string? #(re-matches #"/[a-z-]+/[0-9]+/" %))))
(s/def ::showing-chapters-list boolean?)
(s/def ::showing-placeholder boolean?)
(s/def ::metadata-ui-visibility-id (s/nilable int?))

;; Configuration
(s/def ::asset-server string?)

;; Database
(s/def ::db (s/keys :req [::compiled-book ::current-path ::asset-server ::showing-placeholder ::metadata-ui-visibility-id]))
