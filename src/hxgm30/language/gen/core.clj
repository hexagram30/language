(ns hxgm30.language.gen.core
  (:require
    [hxgm30.language.cli :as cli]
    [hxgm30.language.gen.impl.markov :as markov]
    [hxgm30.language.gen.impl.syntagmata :as syntagmata])
  (:import
    (hxgm30.language.gen.impl.markov MarkovContentGenerator
                                     MarkovStatsGenerator)
    (hxgm30.language.gen.impl.syntagmata SyntagmataContentGenerator
                                         SyntagmataStatsGenerator))
  (:gen-class))

(defprotocol StatsGeneratorAPI
  (generate-stats [this language] [this race name-type])
  (regen-language-stats [this])
  (regen-name-stats [this])
  (regen-stats [this] [this language] [this race name-type])
  (stats [this language] [this race name-type]))

(extend MarkovStatsGenerator
        StatsGeneratorAPI
        markov/stats-gen-behaviour)

(extend SyntagmataStatsGenerator
        StatsGeneratorAPI
        syntagmata/stats-gen-behaviour)

(defn create-stats-generator
  [system type]
  (case type
    :markov (markov/create-stats-generator system)
    :syntagmata (syntagmata/create-stats-generator system)))

(defprotocol ContentGeneratorAPI
  (syllable-count [this stats])
  (syllable [this stats] [this stats position])
  (word [this stats] [this stats syllables] [this stats syllables acc])
  (sentence [this stats] [this stats words])
  (paragraph [this stats] [this stats sencences]))

(extend MarkovContentGenerator
        ContentGeneratorAPI
        markov/content-gen-behaviour)

(extend SyntagmataContentGenerator
        ContentGeneratorAPI
        syntagmata/content-gen-behaviour)

(defn create-content-generator
  [system type]
  (case type
    :markov (markov/create-content-generator system)
    :syntagmata (syntagmata/create-content-generator system)))

(defn -main
  [& args]
  (let [sys (cli/setup-system)
        cmd (keyword (first args))]
    (case cmd
      :regen-syntagmata (do
                          (println "Regenerating syntagmata data ...\n")
                          (regen-stats (syntagmata/create-stats-generator sys))
                          (println))
      :regen-markov-chains (do
                             (println "Regenerating markov-chain data ...\n")
                             (regen-stats (markov/create-stats-generator sys))
                             (println)))))
