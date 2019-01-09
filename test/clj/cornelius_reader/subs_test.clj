(ns cornelius-reader.subs-test
  (:require  [clojure.test :refer :all]
             [cornelius-reader.subs :as tested]))

(deftest previous-page-path
  (testing "Subscribing to the previous page of"
    (testing "the first page returns nil"
      (is (= nil
             (tested/previous-page-path ["/a/" ["/a/" "/b/" "/c/" "/d/"]] nil))))
    (testing "the last page returns the previous page"
      (is (= "/c/"
             (tested/previous-page-path ["/d/" ["/a/" "/b/" "/c/" "/d/"]] nil))))
    (testing "an unremarkable page returns the previous page"
      (is (= "/b/"
             (tested/previous-page-path ["/c/" ["/a/" "/b/" "/c/" "/d/"]] nil))))))

(deftest following-page-path
  (testing "Subscribing to the following page of"
    (testing "the last page returns nil"
      (is (= nil
             (tested/following-page-path ["/d/" ["/a/" "/b/" "/c/" "/d/"]] nil))))
    (testing "the first page returns the following page"
      (is (= "/b/"
             (tested/following-page-path ["/a/" ["/a/" "/b/" "/c/" "/d/"]] nil))))
    (testing "an unremarkable page returns the following page"
      (is (= "/c/"
             (tested/following-page-path ["/b/" ["/a/" "/b/" "/c/" "/d/"]] nil))))))
