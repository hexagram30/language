(ns hxgm30.language.syntagmata.core
  (:require
    [clojure.java.io :as io]
    [clojure.pprint :as pprint]
    [clojure.set :as set]
    [clojure.string :as string]
    [hxgm30.language.syntagmata.corpus :as corpus]
    [hxgm30.language.syntagmata.lang.core :as lang]
    [hxgm30.language.util :as util]
    [taoensso.timbre :as log])
  (:gen-class))

(defn pseudo-syllables
  ""
  [words & args]
  (map (comp #(if (= % []) [""] %)
             #(string/split % #"_")
             #(string/replace %
                              (re-pattern
                                (str (apply corpus/re-vowel args) "+")) "_"))
       words))

(defn pseudo-syllable-counts
  ""
  [words & args]
  (map count (apply pseudo-syllables (cons words args))))

(defn pseudo-syllable-freqs
  "The intent of this function is to provide information regarding how many
  pseudo-syllables occur in the words of a corpus, ultimately giving a
  statistical view of word length.

  Create a lookup of key/value pairs where the key is the number
  pseudo-syllables and the associated value is the number of times the given
  pseudo-syllable occurs."
  [words & args]
  (frequencies (apply pseudo-syllable-counts (cons words args))))

(defn sound-transitions
  ""
  [words & args]
  (map (comp #(remove nil? %)
             #(mapcat rest %)
             #(re-seq (re-pattern (apply corpus/re-sound-transitions args)) %))
       words))

(defn flat-sound-transitions
  [words & args]
  (flatten (apply sound-transitions (cons words args))))

(defn positional-sound-transitions
  [position transitions]
  (case position
    :initial (map first transitions)
    :final (remove nil? (map (comp last rest) transitions))
    (mapcat (comp butlast rest) transitions)))

(defn positional-sound-transition-freqs
  [position transitions]
  (frequencies (positional-sound-transitions position transitions)))

(defn generate-syntagmata
  ""
  [& args]
  (let [words (apply corpus/load-wordlist args)
        pseudo-syllable-freqs (apply pseudo-syllable-freqs (cons words args))
        transitions (apply sound-transitions (cons words args))
        initial (positional-sound-transition-freqs :initial transitions)
        medial (positional-sound-transition-freqs :medial transitions)
        final (positional-sound-transition-freqs :final transitions)]
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

(defn regen-language-syntagmata
  []
  (doall
    (for [language lang/supported-languages]
      (do
        (log/debugf "Processing %s ..." language)
        (corpus/dump-syntagmata language (generate-syntagmata language))
        {language :ok}))))

(defn regen-name-syntagmata
  []
  (doall
    (for [race lang/supported-names
          name-type lang/supported-name-types]
      (do
        (log/debugf "Processing %s + %s ..." race name-type)
        (corpus/dump-syntagmata race name-type (generate-syntagmata race name-type))
        {race {name-type :ok}}))))

(defn regen-syntagmata
  ([]
      (regen-language-syntagmata)
      (regen-name-syntagmata)
      :ok)
  ([language]
    (corpus/dump-syntagmata language (generate-syntagmata language)))
  ([race name-type]
    (corpus/dump-syntagmata race
                            name-type
                            (generate-syntagmata race name-type))))

(defn syntagmata
  ([language]
    (corpus/undump-syntagmata language))
  ([race name-type]
    (corpus/undump-syntagmata race name-type)))

(defn -main
  [& args]
  (let [cmd (keyword (first args))]
    (case cmd
      :regen-syntagmata (do
                          (println "Regenerating syntagmata data ...\n")
                          (regen-syntagmata)
                          (println)))))
