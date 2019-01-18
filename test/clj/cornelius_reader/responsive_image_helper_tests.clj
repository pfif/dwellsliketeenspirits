(ns cornelius-reader.responsive-image-helper-tests
  (:require  [clojure.test :refer :all]
             [cornelius-reader.responsive-image-helper :as tested]
             [clojure.string :as str]))

(deftest srcset
  (testing "Returns the correct information for the browser to parse all the sizes of an image based on the url's beginning"
    (is (= (tested/srcset "/image/begin" [450 650])
           "/image/begin-450.png 450w,/image/begin-650.png 650w"))))

(deftest sizes
  (testing "It returns a correct value for the sizes argument"
    (let [result (tested/sizes [["lol" 450] ["lal" 500] ["lbl" 800] ["lcl" 450] ["ldl" 500] ["lel" 800]])]
      (is (= "lol 450px,lal 500px,lbl 800px,lcl 450px,ldl 500px,lel 800px,800px" result)))))

(deftest media-queries-and-sizes
  (testing "The media query and the size are returned for"
    (let [result (tested/media-queries-and-sizes [450 675 800])]
      (testing "the wide design"
        (is (= ["(min-aspect-ratio: 210/297) and (max-height: 636px)" 450]
               (nth result 0))))
      (testing "the narrow design"
        (is (= ["(max-aspect-ratio: 210/297) and (max-width: 450px)" 450]
               (nth result 3))))
      (testing "the wide design and the highest size"
        (is (= ["(min-aspect-ratio: 210/297) and (min-height: 1131px)" 800]
               (nth result 2))))
      (testing "the wide design and the highest size"
        (is (= ["(max-aspect-ratio: 210/297) and (min-width: 800px)" 800]
               (nth result 5)))))))

(deftest links-preload
  (testing "The correct link tags are returned"
    (is (= [[:link {:rel "preload" :href "/image-400.png" :as "image" :media "lol"}]
            [:link {:rel "prefetch" :href "/image-400.png" :as "image" :media "lol"}]
            [:link {:rel "preload" :href "/image-400.png" :as "image" :media "lel"}]
            [:link {:rel "prefetch" :href "/image-400.png" :as "image" :media "lel"}]]
           (tested/links-preload "/image" [["lol" 400] ["lel" 400]])))))
