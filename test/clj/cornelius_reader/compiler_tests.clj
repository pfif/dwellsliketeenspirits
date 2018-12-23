(ns cornelius-reader.compiler-tests
  (:require  [clojure.test :refer :all]
             [cornelius-reader.compiler :refer [compile-book!]]
             [clojure.string :as str])
  (:import (java.nio.file Files)
           (java.nio.file.attribute FileAttribute)))

(defn create-temp-directory
  "Creates a new temporary directory with the specified prefix name in the OS-specified temporary file-system location."
  []
  (.toString (Files/createTempDirectory nil (make-array FileAttribute 0))))

(deftest write-compiled-book
  (testing "it writes in compiled-book.edn"
    (let [output-directory (create-temp-directory)]
      (compile-book! "test_resources" output-directory)
      (let [raw-compiled-book (slurp (str/join "/" [output-directory "compiled-book.edn"]))
            compiled-book (read-string raw-compiled-book)
            compiled-pages (first compiled-book)
            compiled-chapters (last compiled-book)]

        (testing "the name of the pages without extension"
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
        )
      )))
