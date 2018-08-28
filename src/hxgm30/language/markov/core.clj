(ns hxgm30.language.markov.core
  (:require
    [hxgm30.language.common :as common]
    [hxgm30.language.syntagmata.corpus :as corpus]
    [hxgm30.language.util :as util]
    [taoensso.timbre :as log])
  (:gen-class))

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

(defn generate-markov-chain
  ""
  [& args]
  (let [words (apply corpus/load-wordlist args)
        transitions (apply common/sound-transitions (cons words args))]
    (->> transitions
         (mapcat syllables->pairs)
         transitions-lookup)))

(defn regen-language-markov-chains
  []
  (doall
    (for [language common/supported-languages]
      (do
        (log/debugf "Processing %s ..." language)
        (corpus/dump-markov language (generate-markov-chain language))
        {language :ok}))))

(defn regen-name-markov-chains
  []
  (doall
    (for [race common/supported-names
          name-type common/supported-name-types]
      (do
        (log/debugf "Processing %s + %s ..." race name-type)
        (corpus/dump-markov race name-type (generate-markov-chain race name-type))
        {race {name-type :ok}}))))

(defn regen-markov-chains
  ([]
    (regen-language-markov-chains)
    (regen-name-markov-chains)
    :ok)
  ([language]
    (corpus/dump-markov language (generate-markov-chain language)))
  ([race name-type]
    (corpus/dump-markov race
                        name-type
                        (generate-markov-chain race name-type))))

(defn markov-chain
  ([language]
    (corpus/undump-markov language))
  ([race name-type]
    (corpus/undump-markov race name-type)))

(defn -main
  [& args]
  (let [cmd (keyword (first args))]
    (case cmd
      :regen-markov-chains (do
                             (println "Regenerating markov-chain data ...\n")
                             (regen-markov-chains)
                             (println)))))
