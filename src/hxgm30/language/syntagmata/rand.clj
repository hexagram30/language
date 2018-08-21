(ns hxgm30.language.syntagmata.rand
  (:require
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clojure.string :as string]
    [hxgm30.language.syntagmata.util :as util]))

(defn syllable-count
  [stats]
  (util/percent-> (rand) (get-in stats [:pseudo-syllables :percent-ranges])))

(defn syllable
  [stats]
  (util/percent-> (rand) (get-in stats [:sound-transitions :percent-ranges])))

(defn word
  [stats]
  (->> stats
       syllable-count
       inc
       range
       (mapcat (fn [_] (syllable stats)))
       (apply str)))

(defn sentence
  ([stats]
    (sentence stats (rand-int 10)))
  ([stats word-count]
    (->> word-count
         inc
         range
         (map (fn [_] (word stats)))
         (string/join " ")
         string/capitalize)))

(defn paragraph
  ([stats]
    (paragraph stats (rand-int 10)))
  ([stats sentence-count]
    (str
      (->> sentence-count
           inc
           range
           (map (fn [_] (sentence stats)))
           (string/join ". "))
      ".")))
