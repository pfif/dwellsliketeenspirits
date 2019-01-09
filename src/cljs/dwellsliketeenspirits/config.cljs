(ns dwellsliketeenspirits.config)

#?(:cljs (def debug? ^boolean goog.DEBUG)
   :clj (def debug? true))

(def asset_server
  (if debug?
    "http://127.0.0.1/assets"
    "http://assets.dwellsliketeenspirits.com"))


