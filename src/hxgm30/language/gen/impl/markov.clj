(ns hxgm30.language.gen.impl.markov
  (:require
    [clojure.string :as string]
    [hxgm30.dice.components.random :as random]
    [hxgm30.language.common :as common]
    [hxgm30.language.gen.corpus :as corpus]
    [hxgm30.language.gen.impl.common :as common-impl]
    [hxgm30.language.util :as util]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn syllables->pairs
  [syllables]
  (vec (zipmap (butlast syllables) (rest syllables))))

(defn next-transition-ranges
  [[this-syllable next-syllable]]
  [this-syllable
   (util/frequencies->percent-ranges (frequencies (map second next-syllable)))])

(defn transitions-lookup
  [syllable-pairs]
  (->> syllable-pairs
       (group-by first)
       (map next-transition-ranges)
       (into {})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Stats Generator Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord MarkovStatsGenerator
  [system
   generator
   reader
   writer])

(defn generate-stats
  ""
  [this & args]
  (let [words (apply corpus/load-wordlist args)
        pseudo-syllable-freqs (apply common/pseudo-syllable-freqs (cons words args))
        transitions (apply common/sound-transitions (cons words args))
        initial (common/positional-sound-transition-freqs :initial transitions)]
    {:pseudo-syllables {
      :percent-ranges (util/frequencies->percent-ranges pseudo-syllable-freqs)}
     :sound-transitions {
       :initial {
         :percent-ranges (util/frequencies->percent-ranges initial)}}
     :chain (->> transitions
                 (mapcat syllables->pairs)
                 transitions-lookup)}))

(def stats-gen-behaviour
  {:generate-stats generate-stats
   :regen-language-stats common-impl/regen-language-stats
   :regen-name-stats common-impl/regen-name-stats
   :regen-stats common-impl/regen-stats
   :stats common-impl/stats})

(defn create-stats-generator
  [system]
  (map->MarkovStatsGenerator
    {:system system
     :generator generate-stats
     :reader (common-impl/db-reader-fn system :markov)
     :writer corpus/dump-markov}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Concent Generator Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord MarkovContentGenerator
  [system
   stats-gen
   word-fn])

(defn syllable
  ([this stats]
    (syllable this stats nil))
  ([this stats last-syllable]
    (if (nil? last-syllable)
      (util/percent->
        (random/float (:system this))
        (get-in stats [:sound-transitions :initial :percent-ranges]))
      (util/percent-> (random/float (:system this))
                      (get-in stats [:chain last-syllable])))))

(defn word
  ([this stats-or-lang]
    (if (keyword? stats-or-lang)
      (word this (common-impl/stats (:stats-gen this) stats-or-lang))
      (word this
            stats-or-lang
            (common-impl/syllable-count this stats-or-lang))))
  ([this stats syllables]
    (word this stats syllables [nil]))
  ([this stats syllables acc]
    (if (= 1 syllables)
      (syllable this stats)
      (->> syllables
           range
           (reduce (fn [acc _]
                     (conj acc (syllable this stats (last acc))))
                   acc)
           (remove nil?)
           (string/join "")))))

(def content-gen-behaviour
  {:paragraph common-impl/paragraph
   :sentence common-impl/sentence
   :syllable syllable
   :syllable-count common-impl/syllable-count
   :word word})

(defn create-content-generator
  [system]
  (map->MarkovContentGenerator
    {:stats-gen (create-stats-generator system)
     :system system
     :word-fn word}))
