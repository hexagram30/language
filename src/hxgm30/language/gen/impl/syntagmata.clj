(ns hxgm30.language.gen.impl.syntagmata
  (:require
    [clojure.string :as string]
    [hxgm30.dice.components.random :as random]
    [hxgm30.language.common :as common]
    [hxgm30.language.gen.corpus :as corpus]
    [hxgm30.language.gen.impl.common :as common-impl]
    [hxgm30.language.util :as util]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Stats Generator Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord SyntagmataStatsGenerator
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
        initial (common/positional-sound-transition-freqs :initial transitions)
        medial (common/positional-sound-transition-freqs :medial transitions)
        final (common/positional-sound-transition-freqs :final transitions)]
    {:pseudo-syllables {
      :frequencies pseudo-syllable-freqs
      :percent-ranges (util/frequencies->percent-ranges pseudo-syllable-freqs)}
     :sound-transitions {
       :initial {
         :frequencies initial
         :percent-ranges (util/frequencies->percent-ranges initial)}
       :medial {
         :frequencies medial
         :percent-ranges (util/frequencies->percent-ranges medial)}
       :final {
         :frequencies final
         :percent-ranges (util/frequencies->percent-ranges final)}}}))

(def stats-gen-behaviour
  {:generate-stats generate-stats
   :regen-language-stats common-impl/regen-language-stats
   :regen-name-stats common-impl/regen-name-stats
   :regen-stats common-impl/regen-stats
   :stats common-impl/stats})

(defn create-stats-generator
  [system]
  (map->SyntagmataStatsGenerator
    {:system system
     :generator generate-stats
     :reader corpus/undump-markov
     :writer corpus/dump-markov}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Concent Generator Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord SyntagmataContentGenerator
  [system
   stats-gen
   word-fn])

(defn syllable-count
  [this stats]
  (util/percent-> (random/float (:system this))
                  (get-in stats [:pseudo-syllables :percent-ranges])))

(defn syllable
  [this stats position]
  (case position
    :initial (util/percent->
              (random/float (:system this))
              (get-in stats [:sound-transitions :initial :percent-ranges]))
    :medial (util/percent->
              (random/float (:system this))
              (get-in stats [:sound-transitions :medial :percent-ranges]))
    :final (util/percent->
              (random/float (:system this))
              (get-in stats [:sound-transitions :final :percent-ranges]))))

(defn word
  ([this stats-or-lang]
    (if (keyword? stats-or-lang)
      (word this (corpus/undump-syntagmata stats-or-lang))
      (word this stats-or-lang (common-impl/syllable-count this stats-or-lang))))
  ([this stats syllables]
    (case syllables
      1 (syllable this stats :initial)
      2 (str (syllable this stats :initial)
             (syllable this stats :final))
      (str (syllable this stats :initial)
           (->> syllables
                dec
                range
                (mapcat (fn [_] (syllable this stats :medial)))
                (string/join ""))
           (syllable this stats :final)))))

(def content-gen-behaviour
  {:paragraph common-impl/paragraph
   :sentence common-impl/sentence
   :syllable syllable
   :syllable-count common-impl/syllable-count
   :word word})

(defn create-content-generator
  [system]
  (map->SyntagmataContentGenerator
    {:system system
     :stats-gen (create-stats-generator system)
     :word-fn word}))
