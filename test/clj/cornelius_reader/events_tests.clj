(ns cornelius-reader.events-tests
  (:require  [clojure.test :refer :all]
             [cornelius-reader.events :as tested]))

(deftest initialization
  (testing "On initialization, the resulting DB contains"
    (let [result (tested/initialization {:another-thing "value"} [:initialization "https://asset.example.com"])]
      (testing "a reference to no compiled book"
        (is (= (get result :cornelius-reader.db/compiled-book)
               nil)))
      (testing "what was in the DB when it was passed"
        (is (= (get result :another-thing)
               "value")))
      (testing "the url to the asset server"
        (is (= (get result :cornelius-reader.db/asset-server)
               "https://asset.example.com"))))))

(deftest compiled-book-ready
  (testing "After the compiled book signaled as ready, the resulting DB contains"
    (let [result (tested/compiled-book-ready {:another-thing "value"} [:compiled-book-ready [:pages :chapter]])]
      (testing "the compiled-book that is passed"
        (is (= (get result :cornelius-reader.db/compiled-book)
               [:pages :chapter])))
      (testing "a reference to the first page"
        (is (= (get result :cornelius-reader.db/current-page-reference)
               0)))
      (testing "what was in the DB when it was passed"
        (is (= (get result :another-thing)
               "value"))))))

(def sample-paths-for-all-pages ["/pj-jones-diary/1/" "/chickens-and-cauldrons/1/" "/chickens-and-cauldrons/2/"])
(def sample-paths-for-all-chapters ["/pj-jones-diary/" "/chickens-and-cauldrons/"])

(deftest canoncial-path
  (testing "The path for a page is returned as is"
    (is (= "/pj-jones-diary/1/"
           (tested/canonical-path sample-paths-for-all-pages sample-paths-for-all-chapters "/pj-jones-diary/1/"))))

  (testing "The first page is appended to a PATH"
    (is (= "/pj-jones-diary/1/"
           (tested/canonical-path sample-paths-for-all-pages sample-paths-for-all-chapters "/pj-jones-diary/"))))

  (testing "The PATH is replaced by the PATH of the first page of the last chapter when it"
    (testing "refers to a bogus page in an existing chapter"
      (is (= "/chickens-and-cauldrons/1/"
             (tested/canonical-path sample-paths-for-all-pages sample-paths-for-all-chapters "/pj-jones-diary/4/"))))
    (testing "refers to a bogus chapter (with a bogus page)"
      (is (= "/chickens-and-cauldrons/1/"
             (tested/canonical-path sample-paths-for-all-pages sample-paths-for-all-chapters "/bogus-chapter/2/"))))
    (testing "refers to a bogus chapter (without a bogus page)"
      (is (= "/chickens-and-cauldrons/1/"
             (tested/canonical-path sample-paths-for-all-pages sample-paths-for-all-chapters "/bogus-chapter/"))))
    (testing "is the homepage"
      (is (= "/chickens-and-cauldrons/1/"
             (tested/canonical-path sample-paths-for-all-pages sample-paths-for-all-chapters "/"))))
    (testing "is the homepage (without slash)"
      (is (= "/chickens-and-cauldrons/1/"
             (tested/canonical-path sample-paths-for-all-pages sample-paths-for-all-chapters ""))))
    (testing "it is full bogus"
      (is (= "/chickens-and-cauldrons/1/"
             (tested/canonical-path sample-paths-for-all-pages sample-paths-for-all-chapters "/dlodl/hahaha/maisje/argelege/"))))))
