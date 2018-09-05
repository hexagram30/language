(ns hxgm30.language.gen.assembled.core
  (:require
    [clojure.string :as string]
    [clojusc.twig :as logger]
    [hxgm30.dice.components.random :as random]
    [hxgm30.language.components.config :as config]
    [hxgm30.language.components.core]
    [hxgm30.language.gen.corpus :as corpus]
    [hxgm30.language.gen.assembled.mythgarthur :as mythgarthur]
    [hxgm30.language.gen.assembled.rook :as rook]
    [hxgm30.language.gen.core :as gen]
    [hxgm30.language.util :as util])
  (:gen-class))

(defn select-lang
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
  [this lang-freqs]
  (->> lang-freqs
       util/frequencies->percent-ranges
       (util/percent-> (random/float (:system this)))))

(defn select-stats
  [this lang-freqs]
  (gen/stats (:stats-gen this) (select-lang this lang-freqs)))

(defn word
  [this lang-freqs]
  (let [stats (select-stats this lang-freqs)
        syllables (gen/syllable-count this stats)]
    (case syllables
      1 (gen/syllable this stats :initial)
      2 (str (gen/syllable this stats :initial)
             (gen/syllable this
                            (select-stats this lang-freqs)
                            :final))
      (str (gen/syllable this stats :initial)
           (->> syllables
                dec
                range
                (mapcat (fn [_]
                         (gen/syllable this
                                       (select-stats this lang-freqs)
                                       :medial)))
                (string/join ""))
           (gen/syllable this
                         (select-stats this lang-freqs)
                         :final)))))

(defn sentence
  ([this lang-freqs]
    (sentence this lang-freqs (random/int (:system this) 10)))
  ([this lang-freqs words]
    (str
      (->> words
           inc
           range
           (map (fn [_] (word this lang-freqs)))
           (string/join " ")
           string/capitalize)
      ".")))

(defn paragraph
  ([this lang-freqs]
    (paragraph this lang-freqs (random/int (:system this) 10)))
  ([this lang-freqs sentence-count]
    (str
      (->> sentence-count
           inc
           range
           (map (fn [_] (sentence this lang-freqs)))
           (string/join " ")))))

(defn- print-sample
  [this name lang]
  (print (format "\n\t%s: %s\n" name (paragraph this lang))))

(defn run
  [this world language]
  (case world
    :rook (case language
            :rookish (print-sample this "Rookish" rook/rookish)
            :elani (print-sample this "Elani" rook/elani)
            :jas (print-sample this "Jas" rook/jas)
            :mux (print-sample this "Mux" rook/mux)
            (doall
              (do (run this :rook :rookish)
                  (run this :rook :elani)
                  (run this :rook :jas)
                  (run this :rook :mux))))
    :mythgarthur (case language
                   :orcish (print-sample this "Orcish" mythgarthur/orcish)
                   :elvish (print-sample this "Elvish" mythgarthur/elvish)
                   :human (print-sample this "Human" mythgarthur/human)
                   :dwarvish (print-sample this "Dwarvish" mythgarthur/dwarvish)
                   (doall
                     (do (run this :mythgarthur :orcish)
                         (run this :mythgarthur :elvish)
                         (run this :mythgarthur :human)
                         (run this :mythgarthur :dwarvish))))))
