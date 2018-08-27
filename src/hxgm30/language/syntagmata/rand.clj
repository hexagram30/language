(ns hxgm30.language.syntagmata.rand
  (:require
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clojure.string :as string]
    [hxgm30.dice.components.random :as random]
    [hxgm30.language.syntagmata.util :as util]))

(defn syllable-count
  [system stats]
  (util/percent-> (random/float system)
                  (get-in stats [:pseudo-syllables :percent-ranges])))

(defn syllable
  [system stats position]
  (case position
    :initial (util/percent->
              (random/float system)
              (get-in stats [:sound-transitions :initial :percent-ranges]))
    :medial (util/percent->
              (random/float system)
              (get-in stats [:sound-transitions :medial :percent-ranges]))
    :final (util/percent->
              (random/float system)
              (get-in stats [:sound-transitions :final :percent-ranges]))))

(defn word
  ([system stats]
    (word system stats (syllable-count system stats)))
  ([system stats syllables]
    (case syllables
      1 (syllable system stats :initial)
      2 (str (syllable system stats :initial)
             (syllable system stats :final))
      (str (syllable system stats :initial)
           (->> syllables
                dec
                range
                (mapcat (fn [_] (syllable system stats :medial)))
                (string/join ""))
           (syllable system stats :final)))))

(defn sentence
  ([system stats]
    (sentence system stats (random/int system 10)))
  ([system stats words]
    (str
      (->> words
           inc
           range
           (map (fn [_] (word system stats)))
           (string/join " ")
           string/capitalize)
      ".")))

(defn paragraph
  ([system stats]
    (paragraph system stats (random/int system 10)))
  ([system stats sentence-count]
    (string/join
      " "
      (->> sentence-count
           inc
           range
           (map (fn [_] (sentence system stats)))))))
