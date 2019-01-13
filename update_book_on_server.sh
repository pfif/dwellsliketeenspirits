lein clean
mkdir resources/public/compiled-book/
lein compile-book
gsutil rm gs://dwellsliketeenspirits-data/*
gsutil cp resources/public/compiled-book/* gs://dwellsliketeenspirits-data/
