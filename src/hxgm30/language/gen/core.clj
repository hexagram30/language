(ns hxgm30.language.gen.core
  (:require
    [hxgm30.language.gen.impl.markov :as markov]
    [hxgm30.language.gen.impl.syntagmata :as syntagmata])
  (:import
    (hxgm30.language.gen.impl.markov MarkovContentGenerator
                                     MarkovStatsGenerator)
    (hxgm30.language.gen.impl.syntagmata SyntagmataContentGenerator
                                         SyntagmataStatsGenerator)))

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
  (syllable-count [this] [this stats])
  (syllable [this] [this stats] [this stats position])
  (word [this] [this stats] [this stats syllables] [this stats syllables acc])
  (sentence [this] [this stats] [this stats words])
  (paragraph [this] [this stats] [this stats sencences]))

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
