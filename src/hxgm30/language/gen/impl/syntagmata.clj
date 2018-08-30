(ns hxgm30.language.gen.impl.syntagmata
  (:require
    [clojure.string :as string]
    [hxgm30.dice.components.random :as random]
    [hxgm30.language.common :as common]
    [hxgm30.language.gen.corpus :as corpus]
    [hxgm30.language.util :as util]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Stats Generator Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord SyntagmataStatsGenerator
  [system])

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

(defn regen-language-stats
  [this]
  (doall
    (for [language common/supported-languages]
      (do
        (log/debugf "Processing %s ..." language)
        (corpus/dump-syntagmata language (generate-stats this language))
        {language :ok}))))

(defn regen-name-stats
  [this]
  (doall
    (for [race common/supported-names
          name-type common/supported-name-types]
      (do
        (log/debugf "Processing %s + %s ..." race name-type)
        (corpus/dump-syntagmata race
                                name-type
                                (generate-stats this race name-type))
        {race {name-type :ok}}))))

(defn regen-stats
  ([this]
    (regen-language-stats this)
    (regen-name-stats this)
    :ok)
  ([this language]
    (corpus/dump-syntagmata language (generate-stats this language)))
  ([this race name-type]
    (corpus/dump-syntagmata race
                            name-type
                            (generate-stats this race name-type))))

(defn stats
  ([this language]
    (corpus/undump-syntagmata language))
  ([this race name-type]
    (corpus/undump-syntagmata race name-type)))

(def stats-gen-behaviour
  {:generate-stats generate-stats
   :regen-language-stats regen-language-stats
   :regen-name-stats regen-name-stats
   :regen-stats regen-stats
   :stats stats})

(defn create-stats-generator
  [system]
  (map->SyntagmataStatsGenerator
    {:system system}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Concent Generator Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord SyntagmataContentGenerator
  [system
   stats-gen])

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
      (word this stats-or-lang (syllable-count this stats-or-lang))))
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

(defn sentence
  ([this stats-or-lang]
    (if (keyword? stats-or-lang)
      (sentence this (corpus/undump-syntagmata stats-or-lang))
      (sentence this stats-or-lang (random/int (:system this) 10))))
  ([this stats words]
    (str
      (->> words
           inc
           range
           (map (fn [_] (word this stats)))
           (string/join " ")
           string/capitalize)
      ".")))

(defn paragraph
  ([this stats-or-lang]
    (if (keyword? stats-or-lang)
      (paragraph this (corpus/undump-syntagmata stats-or-lang))
      (paragraph this stats-or-lang (random/int (:system this) 10))))
  ([this stats sentence-count]
    (string/join
      " "
      (->> sentence-count
           inc
           range
           (map (fn [_] (sentence this stats)))))))

(def content-gen-behaviour
  {:syllable-count syllable-count
   :syllable syllable
   :word word
   :sentence sentence
   :paragraph paragraph})

(defn create-content-generator
  [system]
  (map->SyntagmataContentGenerator
    {:system system
     :stats-gen (create-stats-generator system)}))
