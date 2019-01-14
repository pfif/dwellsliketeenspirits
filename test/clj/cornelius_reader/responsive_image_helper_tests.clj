(ns cornelius-reader.responsive-image-helper-tests
  (:require  [clojure.test :refer :all]
             [cornelius-reader.responsive-image-helper :as tested]
             [clojure.string :as str]))

(deftest srcset
  (testing "Returns the correct information for the browser to parse all the sizes of an image based on the url's beginning"
    (is (= (tested/srcset "/image/begin" [450 650])
           "/image/begin-450.png 450w,/image/begin-650.png 650w"))))

(deftest sizes
  (testing "It returns the sizes for"
    (let [result (tested/sizes [450 675 800])]
      (testing "the wide design"
        (is (str/includes? result
                           "(min-aspect-ratio: 210/297) and (max-height: 636px) 450px")))
      (testing "the narrow design"
        (is (str/includes? result
                           "(max-aspect-ratio: 210/297) and (max-width: 450px) 450px")))
      (testing "the greatest version available without any media queries"
        (is (str/ends-with? result
                            ",800px"))))
    ))

