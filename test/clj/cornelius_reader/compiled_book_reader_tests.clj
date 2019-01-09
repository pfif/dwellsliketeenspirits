(ns cornelius-reader.compiled-book-reader-tests
  (:require  [clojure.test :refer :all]
             [cornelius-reader.compiled-book-reader :as tested]))

(def sample-compiled-book [[["c1p1" "PJ Jone's Diary" "1/1" 1 "dlts-chap1-page1.jpg" 1]
                            ["c2p1" "Chickens and Cauldrons" "1/2" 2 "dlts-chap2-page1.jpg" 1]
                            ["c2p2" "Chickens and Cauldrons" "2/2" 2 "dlts-chap2-page2.jpg" 2]]
                           ["PJ Jone's Diary" "Chickens and Cauldrons"]])

(deftest map-paths-to-pages
  (testing "The paths for all pages mapped with said pages are returned"
    (is (= {"/pj-jones-diary/1/" ["c1p1" "PJ Jone's Diary" "1/1" 1 "dlts-chap1-page1.jpg" 1],
            "/chickens-and-cauldrons/1/" ["c2p1" "Chickens and Cauldrons" "1/2" 2 "dlts-chap2-page1.jpg" 1]
            "/chickens-and-cauldrons/2/" ["c2p2" "Chickens and Cauldrons" "2/2" 2 "dlts-chap2-page2.jpg" 2]}
           (tested/map-paths-to-pages sample-compiled-book))))

  (testing "The returned paths are correctly ordered"
    (is (= ["/pj-jones-diary/1/" "/chickens-and-cauldrons/1/" "/chickens-and-cauldrons/2/"]
           (keys (tested/map-paths-to-pages sample-compiled-book))))))


(deftest paths-for-all-chapters
  (testing "The paths for all chapters are returned"
    (is (= ["/pj-jones-diary/" "/chickens-and-cauldrons/"]
           (tested/paths-for-all-chapters sample-compiled-book)))))
