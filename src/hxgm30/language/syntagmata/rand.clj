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
  [stats position]
  (case position
    :initial (util/percent->
              (rand)
              (get-in stats [:sound-transitions :initial :percent-ranges]))
    :medial (util/percent->
              (rand)
              (get-in stats [:sound-transitions :medial :percent-ranges]))
    :final (util/percent->
              (rand)
              (get-in stats [:sound-transitions :final :percent-ranges]))))

(defn word
  ([stats]
    (word stats (syllable-count stats)))
  ([stats syllables]
    (case syllables
      1 (syllable stats :initial)
      2 (str (syllable stats :initial)
             (syllable stats :final))
      (str (syllable stats :initial)
           (->> syllables
                dec
                range
                (mapcat (fn [_] (syllable stats :medial)))
                (string/join ""))
           (syllable stats :final)))))

(defn sentence
  ([stats]
    (sentence stats (rand-int 10)))
  ([stats words]
    (->> words
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
