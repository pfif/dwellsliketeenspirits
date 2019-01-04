(ns cornelius-reader.events-tests
  (:require  [clojure.test :refer :all]
             [cornelius-reader.events :as tested]))

(deftest initialization
  (testing "The resulting DB contains"
    (let [result (tested/initialization {:another-thing "value"} [:initialize [:pages :chapter]])]
      (testing "the compiled-book that is passed"
        (is (= (get result :cornelius-reader.db/compiled-book)
               [:pages :chapter])))
      (testing "a reference to the first page"
        (is (= (get result :cornelius-reader.db/current-page-reference)
               0)))
      (testing "what was in the DB when it was passed"
        (is (= (get result :another-thing)
            "value"))))))
