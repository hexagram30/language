(ns ^:unit hxgm30.language.tests.natural
  (:require
    [clojure.test :refer :all]
    [hxgm30.language.natural.core :as natural]))

(deftest noun?
  (is (natural/noun? ["book" "NN"]))
  (is (natural/noun? ["books" "NNS"]))
  (is (natural/noun? ["Alice" "NNP"]))
  (is (natural/noun? ["Bobs" "NNPS"])))

(deftest verb?
  (is (natural/verb? ["take" "VB"]))
  (is (natural/verb? ["takes" "VBZ"]))
  (is (natural/verb? ["took" "VBD"]))
  (is (natural/verb? ["taking" "VBG"]))
  (is (natural/verb? ["taken" "VBN"])))

(deftest noun-or-verb?
  (is (natural/noun-or-verb? ["book" "NN"]))
  (is (natural/noun-or-verb? ["books" "NNS"]))
  (is (natural/noun-or-verb? ["Alice" "NNP"]))
  (is (natural/noun-or-verb? ["Bobs" "NNPS"]))
  (is (natural/noun-or-verb? ["take" "VB"]))
  (is (natural/noun-or-verb? ["takes" "VBZ"]))
  (is (natural/noun-or-verb? ["took" "VBD"]))
  (is (natural/noun-or-verb? ["taking" "VBG"]))
  (is (natural/noun-or-verb? ["taken" "VBN"])))
