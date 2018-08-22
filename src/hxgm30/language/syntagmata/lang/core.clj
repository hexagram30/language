(ns hxgm30.language.syntagmata.lang.core
  (:require
    [clojure.string :as string]
    [hxgm30.language.syntagmata.core :as syntagmata]
    [hxgm30.language.syntagmata.corpus :as corpus]
    [hxgm30.language.syntagmata.lang.fictional.mythgarthur :as mythgarthur]
    [hxgm30.language.syntagmata.lang.fictional.rook :as rook]
    [hxgm30.language.syntagmata.rand :as rand]
    [hxgm30.language.syntagmata.util :as util])
  (:gen-class))

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
  ([lang-freqs]
    (sentence lang-freqs (rand-int 10)))
  ([lang-freqs words]
    (str
      (->> words
           inc
           range
           (map (fn [_] (word lang-freqs)))
           (string/join " ")
           string/capitalize)
      ".")))

(defn paragraph
  ([lang-freqs]
    (paragraph lang-freqs (rand-int 10)))
  ([lang-freqs sentence-count]
    (str
      (->> sentence-count
           inc
           range
           (map (fn [_] (sentence lang-freqs)))
           (string/join " ")))))

(defn- print-sample
  [name lang]
  (print (format "\n\t%s: %s\n" name (paragraph lang))))

(defn -main
  [& args]
  (let [world (keyword (first args))
        language (keyword (second args))]
    (case world
      :rook (case language
              :rookish (print-sample "Rookish" rook/rookish)
              :elani (print-sample "Elani" rook/elani)
              :jas (print-sample "Jas" rook/jas)
              :mux (print-sample "Mux" rook/mux)
              (doall
                (do (-main :rook :rookish)
                    (-main :rook :elani)
                    (-main :rook :jas)
                    (-main :rook :mux))))
      :mythgarthur (case language
                     :orcish (print-sample "Orcish" mythgarthur/orcish)
                     :elvish (print-sample "Elvish" mythgarthur/elvish)
                     :human (print-sample "Human" mythgarthur/human)
                     :dwarvish (print-sample "Dwarvish" mythgarthur/dwarvish)
                     (doall
                       (do (-main :mythgarthur :orcish)
                           (-main :mythgarthur :elvish)
                           (-main :mythgarthur :human)
                           (-main :mythgarthur :dwarvish)))))))
