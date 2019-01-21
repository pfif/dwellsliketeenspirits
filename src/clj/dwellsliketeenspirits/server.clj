(ns dwellsliketeenspirits.server
  (:require [clojure.string :refer [starts-with?]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
))

(defn- wrap-default-index [next-handler]
  (fn [request]
    (next-handler
     (if (or (starts-with? (:uri request) "/css/")
             (starts-with? (:uri request) "/js/")
             (starts-with? (:uri request) "/images/")
             (starts-with? (:uri request) "/compiled-book/")) ;; TODO delete this from prod envs
       request
       (assoc request :uri "/index.html")))))

(def handler
  (-> (fn [_] {:status 404 :body "static asset not found"})
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-default-index)))
