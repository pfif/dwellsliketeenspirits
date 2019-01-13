(ns dwellsliketeenspirits.config)

(def debug? ^boolean goog.DEBUG)

(def asset-server-url
  (if debug?
    "http://127.0.0.1:3449/compiled-book"
    "http://storage.googleapis.com/dwellsliketeenspirits-data"))
