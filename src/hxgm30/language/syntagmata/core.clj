(ns hxgm30.language.syntagmata.core
  (:require
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clojure.string :as string]
    [hxgm30.language.syntagmata.corpus :as corpus]))

(defn pseudo-syllable-freqs
  "Create a lookup of key/value pairs where the key is the number
  pseudo-syllables and the associated value is the number of times the given
  pseudo-syllable occurs."
  [language words]
  (->> words
       (map (comp inc
                  count
                  #(string/split % #"_")
                  #(string/replace % (re-pattern (str (corpus/re-vowel language) "+")) "_")))
       frequencies))

(defn frequency->percent-range
  "A reducer that, given a frequency and an accumulator, returns an updated
  accumulator with the given frequency converted to a percent range. The first
  argument is expectred to be provided as a partial."
  [total acc [k v]]
  (let [last-value (or (last (first (last acc))) 0)
        v-percent (/ v total)]
    (conj acc [[last-value (+ v-percent last-value)] k])))

(defn frequencies->percent-ranges
  "Create a lookup of key/value pairs where the key is the range of percentages
  where a syllable count occurs, and the value is the syllable count."
  [freqs]
  (let [total (reduce + 0.0 (vals freqs))]
    (->> freqs
         (reduce (partial frequency->percent-range total) [])
         (into {}))))

(defn sound-transition-freqs
  ""
  [language words]
  (->> words
       (mapcat #(re-seq (re-pattern (corpus/re-sound-transition language)) %))
       frequencies))

(defn stats
  ""
  [language]
  (let [words (corpus/load-wordlist language)
        pseudo-syllable-freqs (pseudo-syllable-freqs language words)
        sound-transition-freqs (sound-transition-freqs language words)]
    {:pseudo-syllables {
      :frequencies pseudo-syllable-freqs
      :percent-ranges (frequencies->percent-ranges pseudo-syllable-freqs)}
     :sound-transitions {
      :frequencies sound-transition-freqs
      :percent-ranges (frequencies->percent-ranges sound-transition-freqs)}}))
