(ns cornelius-reader.compiler-tests
  (:require  [clojure.test :refer :all]
             [cornelius-reader.compiler :refer [compile-book! compiled-book resized-images-spec resize-images!]]
             [clojure.string :as str]
             [clojure.java.io :as io])
  (:import (java.nio.file Files)
           (java.nio.file.attribute FileAttribute)))

(def sample-book {"PJ Jone's Diary" ["dlts-chap1-page1.jpg"
                                     "dlts-chapitre1-page2.jpg"],
                  "Chickens and Cauldrons" ["dlts-chap2-page1.jpg"
                                            "dlts-chap2-page2.jpg"
                                            "chap2-page3.jpg"]})


(defn create-temp-directory
  "Creates a new temporary directory with the specified prefix name in the OS-specified temporary file-system location."
  []
  (.toString (Files/createTempDirectory nil (make-array FileAttribute 0))))


(deftest compiled-book-definiton
  (testing "it returns a correctly formatted compiled-book in which"
    (let [compiled-book (compiled-book sample-book)
          compiled-pages (first compiled-book)
          compiled-chapters (last compiled-book)]

      (testing "the name of the pages is without extension"
        (is (= (map first compiled-pages) ["c1p1" "c1p2" "c2p1" "c2p2" "c2p3"])))

      (testing "the chapters are associated with every pages"
        (is (= (map #(nth % 1) compiled-pages) ["PJ Jone's Diary" "PJ Jone's Diary"
                                                "Chickens and Cauldrons" "Chickens and Cauldrons""Chickens and Cauldrons"])))

      (testing "the page number progression are associated with every books"
        (is (= (map #(nth % 2) compiled-pages) ["1/2" "2/2"
                                                "1/3" "2/3" "3/3"])))

      (testing "the chapter number is associated with every page"
        (is (= (map #(nth % 3) compiled-pages) [1 1
                                                2 2 2])))
      (testing "the chapters are returned"
        (is (= compiled-chapters ["PJ Jone's Diary" "Chickens and Cauldrons"])))

      (testing "the page numbers are returned"
        (is (= (map #(nth % 5) compiled-pages)
               [1 2
                1 2 3]))))))


(def sample-compiled-pages '(["c1p1" "PJ Jone's Diary" "1/1" 1 "dlts-chap1-page1.jpg" 1]
                            ["c2p1" "Chickens and Cauldrons" "1/2" 2 "dlts-chap2-page1.jpg" 1]
                            ["c2p2" "Chickens and Cauldrons" "2/2" 2 "dlts-chap2-page2.jpg" 2]))

(deftest resize-image-specs
  (testing "it returns correct specs for resized images with"
    (let [sample-resized-image-spec (resized-images-spec sample-compiled-pages "/origin" "/destination")]
      (testing "the original image named first"
        (is (= (map first sample-resized-image-spec) ["/origin/dlts-chap1-page1.jpg"
                                                      "/origin/dlts-chap1-page1.jpg"
                                                      "/origin/dlts-chap1-page1.jpg"
                                                      "/origin/dlts-chap1-page1.jpg"
                                                      "/origin/dlts-chap1-page1.jpg"
                                                      "/origin/dlts-chap1-page1.jpg"
                                                      "/origin/dlts-chap1-page1.jpg"
                                                      
                                                      "/origin/dlts-chap2-page1.jpg"
                                                      "/origin/dlts-chap2-page1.jpg"
                                                      "/origin/dlts-chap2-page1.jpg"
                                                      "/origin/dlts-chap2-page1.jpg"
                                                      "/origin/dlts-chap2-page1.jpg"
                                                      "/origin/dlts-chap2-page1.jpg"
                                                      "/origin/dlts-chap2-page1.jpg"
                                                      
                                                      "/origin/dlts-chap2-page2.jpg"
                                                      "/origin/dlts-chap2-page2.jpg"
                                                      "/origin/dlts-chap2-page2.jpg"
                                                      "/origin/dlts-chap2-page2.jpg"
                                                      "/origin/dlts-chap2-page2.jpg"
                                                      "/origin/dlts-chap2-page2.jpg"
                                                      "/origin/dlts-chap2-page2.jpg"])))
      
      (testing "the new name for the rezised image second with the width"
        (is (= (map #(nth % 1) sample-resized-image-spec) ["/destination/c1p1-450"
                                                           "/destination/c1p1-675"
                                                           "/destination/c1p1-800"
                                                           "/destination/c1p1-900"
                                                           "/destination/c1p1-1200"
                                                           "/destination/c1p1-1600"
                                                           "/destination/c1p1-2018"
                                                           
                                                           "/destination/c2p1-450"
                                                           "/destination/c2p1-675"
                                                           "/destination/c2p1-800"
                                                           "/destination/c2p1-900"
                                                           "/destination/c2p1-1200"
                                                           "/destination/c2p1-1600"
                                                           "/destination/c2p1-2018"
                                                           
                                                           "/destination/c2p2-450"
                                                           "/destination/c2p2-675"
                                                           "/destination/c2p2-800"
                                                           "/destination/c2p2-900"
                                                           "/destination/c2p2-1200"
                                                           "/destination/c2p2-1600"
                                                           "/destination/c2p2-2018"])))
      (testing "the desired width last"
        (is (= (map last sample-resized-image-spec) [450
                                                     675
                                                     800
                                                     900
                                                     1200
                                                     1600
                                                     2018
                                                     
                                                     450
                                                     675
                                                     800
                                                     900
                                                     1200
                                                     1600
                                                     2018
                                                     
                                                     450
                                                     675
                                                     800
                                                     900
                                                     1200
                                                     1600
                                                     2018]))))))

(deftest image-resizer-tests
  (testing "it converts images that exists"
    (let [output-directory (create-temp-directory)]
      (resize-images! [["test_resources/chap2-page3.jpg" (str output-directory "/images") 2000]
                       ["test_resources/chap2-page3.jpg" (str output-directory "/images-2") 1000]])
      (is (= (.exists (io/file (str output-directory "/images.png"))) true))
      (is (= (.exists (io/file (str output-directory "/images-2.png"))) true)))))
