(ns dwellsliketeenspirits.config)

(def debug? ^boolean goog.DEBUG)

(def asset-server-url
  (if debug?
    "/compiled-book"
    "http://storage.googleapis.com/dwellsliketeenspirits-data"))
