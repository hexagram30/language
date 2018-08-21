(ns hxgm30.language.syntagmata.lang.core
  (:require
    [clojure.string :as string]
    [hxgm30.language.syntagmata.core :as syntagmata]
    [hxgm30.language.syntagmata.corpus :as corpus]
    [hxgm30.language.syntagmata.rand :as rand]
    [hxgm30.language.syntagmata.util :as util]))

(defn select
  "Expects a signature like the following:
  ```
    {:english 2
     :chinese 5
     :arabic 3}
  ```

  Thus, a call should looke like this:
  ```
    (select {:english 2
             :chinese 5
             :arabic 3})
  ```"
  [lang-freqs]
  (util/percent-> (rand) (util/frequencies->percent-ranges lang-freqs)))

(defn word
  [lang-freqs]
  (let [lang (select lang-freqs)
        stats (syntagmata/stats lang)
        syllables (rand/syllable-count stats)]
    (case syllables
      1 (rand/syllable stats :initial)
      2 (str (rand/syllable stats :initial)
             (rand/syllable (syntagmata/stats (select lang-freqs)) :final))
      (str (rand/syllable stats :initial)
           (->> syllables
                dec
                range
                (mapcat (fn [_] (rand/syllable (syntagmata/stats (select lang-freqs)) :medial)))
                (string/join ""))
           (rand/syllable (syntagmata/stats (select lang-freqs)) :final)))))

(defn sentence
  ([stats]
    (sentence stats (rand-int 10)))
  ([stats words]
    (str
      (->> words
           inc
           range
           (map (fn [_] (word stats)))
           (string/join " ")
           string/capitalize)
      ".")))

(defn paragraph
  ([stats]
    (paragraph stats (rand-int 10)))
  ([stats sentence-count]
    (str
      (->> sentence-count
           inc
           range
           (map (fn [_] (sentence stats))))
      " ")))
