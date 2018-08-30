(ns hxgm30.language.common
  (:require
    [clojure.string :as string]
    [hxgm30.language.gen.corpus :as corpus]))

(def supported-languages
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

(def supported-names
  [:dragonborn
   :dwarf
   :elf
   :gnome
   :halfling
   :human
   :orc
   :tiefling])

(def supported-name-types
  [:female
   :male
   :surname])

(defn pseudo-syllables
  ""
  [words & args]
  (map (comp #(if (= % []) [""] %)
             #(string/split % #"_")
             #(string/replace %
                              (re-pattern
                                (str (apply corpus/re-vowel args) "+")) "_"))
       words))

(defn pseudo-syllable-counts
  ""
  [words & args]
  (map count (apply pseudo-syllables (cons words args))))

(defn pseudo-syllable-freqs
  "The intent of this function is to provide information regarding how many
  pseudo-syllables occur in the words of a corpus, ultimately giving a
  statistical view of word length.

  Create a lookup of key/value pairs where the key is the number
  pseudo-syllables and the associated value is the number of times the given
  pseudo-syllable occurs."
  [words & args]
  (frequencies (apply pseudo-syllable-counts (cons words args))))

(defn sound-transitions
  ""
  [words & args]
  (map (comp #(remove nil? %)
             #(mapcat rest %)
             #(re-seq (re-pattern (apply corpus/re-sound-transitions args)) %))
       words))

(defn flat-sound-transitions
  [words & args]
  (flatten (apply sound-transitions (cons words args))))

(defn positional-sound-transitions
  [position transitions]
  (case position
    :initial (map first transitions)
    :final (remove nil? (map (comp last rest) transitions))
    (mapcat (comp butlast rest) transitions)))

(defn positional-sound-transition-freqs
  [position transitions]
  (frequencies (positional-sound-transitions position transitions)))
