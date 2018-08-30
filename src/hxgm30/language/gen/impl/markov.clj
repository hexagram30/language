(ns hxgm30.language.gen.impl.markov
  (:require
    [clojure.string :as string]
    [hxgm30.dice.components.random :as random]
    [hxgm30.language.common :as common]
    [hxgm30.language.gen.corpus :as corpus]
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
  [system])

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

(defn regen-language-stats
  [this]
  (doall
    (for [language common/supported-languages]
      (do
        (log/debugf "Processing %s ..." language)
        (corpus/dump-markov language (generate-stats this language))
        {language :ok}))))

(defn regen-name-stats
  [this]
  (doall
    (for [race common/supported-names
          name-type common/supported-name-types]
      (do
        (log/debugf "Processing %s + %s ..." race name-type)
        (corpus/dump-markov race name-type (generate-stats this race name-type))
        {race {name-type :ok}}))))

(defn regen-stats
  ([this]
    (regen-language-stats this)
    (regen-name-stats this)
    :ok)
  ([this language]
    (corpus/dump-markov language (generate-stats this language)))
  ([this race name-type]
    (corpus/dump-markov race
                        name-type
                        (generate-stats this race name-type))))

(defn stats
  ([this language]
    (corpus/undump-markov language))
  ([this race name-type]
    (corpus/undump-markov race name-type)))

(def stats-gen-behaviour
  {:generate-stats generate-stats
   :regen-language-stats regen-language-stats
   :regen-name-stats regen-name-stats
   :regen-stats regen-stats
   :stats stats})

(defn create-stats-generator
  [system]
  (map->MarkovStatsGenerator
    {:system system}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Concent Generator Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord MarkovContentGenerator
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
  ([this stats]
    (word this stats (syllable-count this stats)))
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
  ([this stats]
    (sentence this stats (random/int (:system this) 10)))
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
  ([this stats]
    (paragraph this stats (random/int (:system this) 10)))
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
  (map->MarkovContentGenerator
    {:system system
     :stats-gen (create-stats-generator system)}))
