(ns hxgm30.language.syntagmata.core
  (:require
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clojure.string :as string]
    [hxgm30.language.syntagmata.corpus :as corpus]
    [hxgm30.language.syntagmata.util :as util]))

(def supported
  [:afrikaans
   :arabic
   :chinese
   :english
   :finnish
   :french
   :gaelic
   :german
   :greek
   :hebrew
   :hindi
   :japanese
   :korean
   :latin
   :oldenglish
   :oldnorse
   :onomatopoetic
   :pie
   :russian
   :sanskrit
   :scots
   :spanish])

(defn pseudo-syllables
  ""
  [language words]
  (map (comp #(if (= % []) [""] %)
             #(string/split % #"_")
             #(string/replace % (re-pattern (str (corpus/re-vowel language) "+")) "_"))
       words))

(defn pseudo-syllable-counts
  ""
  [language words]
  (map count (pseudo-syllables language words)))

(defn pseudo-syllable-freqs
  "The intent of this function is to provide information regarding how many
  pseudo-syllables occur in the words of a corpus, ultimately giving a
  statistical view of word length.

  Create a lookup of key/value pairs where the key is the number
  pseudo-syllables and the associated value is the number of times the given
  pseudo-syllable occurs."
  [language words]
  (frequencies (pseudo-syllable-counts language words)))

(defn sound-transitions
  ""
  [language words]
  (map (comp #(remove nil? %)
             #(mapcat rest %)
             #(re-seq (re-pattern (corpus/re-sound-transitions language)) %))
       words))

(defn flat-sound-transitions
  [language words]
  (flatten (sound-transitions language words)))

(defn positional-sound-transitions
  [position transitions]
  (case position
    :initial (map first transitions)
    :final (remove nil? (map (comp last rest) transitions))
    (mapcat (comp butlast rest) transitions)))

(defn positional-sound-transition-freqs
  [position transitions]
  (frequencies (positional-sound-transitions position transitions)))

(defn generate-stats
  ""
  [language]
  (let [words (corpus/load-wordlist language)
        pseudo-syllable-freqs (pseudo-syllable-freqs language words)
        transitions (sound-transitions language words)
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

(defn regen-stats
  ([]
    (doall
      (for [language supported]
        (do
          (corpus/dump :stats language (generate-stats language))
          {language :ok}))))
  ([language]
    (corpus/dump :stats language (generate-stats language))))

(defn stats
  [language]
  (corpus/undump :stats language))

