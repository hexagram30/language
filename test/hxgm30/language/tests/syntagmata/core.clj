(ns ^:unit hxgm30.language.tests.syntagmata.core
  (:require
    [clojure.test :refer :all]
    [hxgm30.language.syntagmata.core :as syntagmata]))

(def test-word-list
  ["a"
   "apple"
   "application"
   "cat"
   "encyclopoedia"
   "engineer"
   "tools"
   "tune"])

(deftest pseudo-syllables
  (is (= [[""]
          ["" "ppl"]
          ["" "ppl" "c" "t" "n"]
          ["c" "t"]
          ["" "nc" "cl" "p" "d"]
          ["" "ng" "n" "r"]
          ["t" "ls"]
          ["t" "n"]]
         (syntagmata/pseudo-syllables :english test-word-list))))

(deftest pseudo-syllable-counts
  (is (= [1
          2
          5
          2
          5
          4
          2
          2]
         (syntagmata/pseudo-syllable-counts :english test-word-list))))

(deftest pseudo-syllable-freqs
  (is (= {1 1
          2 4
          5 2
          4 1}
         (syntagmata/pseudo-syllable-freqs :english test-word-list))))

(deftest sound-transitions
  (is (= [["a"]
          ["a" "pple"]
          ["a" "ppli" "ca" "tio" "n"]
          ["ca" "t"]
          ["e" "ncy" "clo" "poe" "dia"]
          ["e" "ngi" "nee" "r"]
          ["too" "ls"]
          ["tu" "ne"]]
         (syntagmata/sound-transitions :english test-word-list))))

(deftest flat-sound-transitions
  (is (= ["a"
          "a" "pple"
          "a" "ppli" "ca" "tio" "n"
          "ca" "t"
          "e" "ncy" "clo" "poe" "dia"
          "e" "ngi" "nee" "r"
          "too" "ls"
          "tu" "ne"]
         (syntagmata/flat-sound-transitions :english test-word-list))))

(deftest positional-sound-transitions
  (let [trans (syntagmata/sound-transitions :english test-word-list)]
    (is (= ["a" "a" "a" "ca" "e" "e" "too" "tu"]
           (syntagmata/positional-sound-transitions :initial trans)))
    (is (= ["ppli" "ca" "tio" "ncy" "clo" "poe" "ngi" "nee"]
           (syntagmata/positional-sound-transitions :medial trans)))
    (is (= ["pple" "n" "t" "dia" "r" "ls" "ne"]
           (syntagmata/positional-sound-transitions :final trans)))))

(deftest positional-sound-transition-freqs
  (let [trans (syntagmata/sound-transitions :english test-word-list)]
    (is (= {"a" 3, "ca" 1, "e" 2, "too" 1, "tu" 1}
           (syntagmata/positional-sound-transition-freqs :initial trans)))
    (is (= {"ppli" 1, "ca" 1, "tio" 1, "ncy" 1, "clo" 1, "poe" 1, "ngi" 1, "nee" 1}
           (syntagmata/positional-sound-transition-freqs :medial trans)))
    (is (= {"pple" 1, "n" 1, "t" 1, "dia" 1, "r" 1, "ls" 1, "ne" 1}
           (syntagmata/positional-sound-transition-freqs :final trans)))))
