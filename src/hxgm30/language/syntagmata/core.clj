(ns hxgm30.language.syntagmata.core
  (:require
    [clojure.java.io :as io]
    [clojure.pprint :as pprint]
    [clojure.set :as set]
    [clojure.string :as string]
    [hxgm30.language.common :as common]
    [hxgm30.language.syntagmata.corpus :as corpus]
    [hxgm30.language.util :as util]
    [taoensso.timbre :as log])
  (:gen-class))

(defn generate-syntagmata
  ""
  [& args]
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

(defn regen-language-syntagmata
  []
  (doall
    (for [language common/supported-languages]
      (do
        (log/debugf "Processing %s ..." language)
        (corpus/dump-syntagmata language (generate-syntagmata language))
        {language :ok}))))

(defn regen-name-syntagmata
  []
  (doall
    (for [race common/supported-names
          name-type common/supported-name-types]
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
