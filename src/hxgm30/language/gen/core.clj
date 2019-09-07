(ns hxgm30.language.gen.core
  (:require
    [hxgm30.common.util :as util]
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Non-system-using functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn eat [data]
  (loop [d (map identity data)
         acc [d]]
    (if (= (count d) 2)
      acc
      (let [nxt (vec (rest d))]
        (recur nxt
               (conj acc nxt))))))

(defn simple-markov [word]
  (reduce (fn [acc rem] (assoc acc (str (first rem)) (str (second rem))))
          {}
          (eat word)))

(defn create-word-lookup [words-data]
  "This function expects `words-data` to have a structure like the following:

  ```
  (def world
    [{:word \"tunkasila\"
      :weight 52}
     {:word \"nuna\"
      :weight 4}
     {:word \"olor\"
      :weight 3}
     {:word \"shaoghal\"
      :weight 3}
     {:word \"arda\"
      :weight 1}
     {:word \"verǫld\"
      :weight 13}
     {:word \"maailma\"
      :weight 2}
     {:word \"delkhiin\"
      :weight 5}
     {:word \"sikul\"
      :weight 5}
     {:word \"honua\"
      :weight 3}
     {:word \"ao\"
      :weight 9}])
  ````
  "
  (reduce (partial util/frequency->percent-range total)
          []
          (map #(list (simple-markov (:word %)) (:weight %))
               words-data)))

(defn derive-word
  "Given a data structure that's a list of words and their weights, synthesize
  a new word based on the given words with word of higher weight contributed
  their sounds more heavily to the generated word.

  For the data structure, see the docstring of the `create-word-lookup`
  function."
  ([words-data]
    (derive-word words-data (rand-int 10)))
  ([words-data syllable-count]
    (let [total (reduce + 0.0 (map :weight words-data))]
      (apply str
        (flatten
          (map (fn [_] (rand-nth
                        (vec
                         (u/percent-> (rand)
                                      (create-word-lookup words-data)))))
               (range syllable-count)))))))

(defn derive-words
  ([words-data]
    (derive-words words-data 10))
  ([words-data word-count]
    (map (fn [_] (derive-word words-data)) (range word-count)))
  ([words-data word-count syllable-count]
    (map (fn [_] (derive-word words-data syllable-count))
         (range word-count))))
