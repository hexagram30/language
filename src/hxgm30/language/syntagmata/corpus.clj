(ns hxgm30.language.syntagmata.corpus
  (:require
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clojure.string :as string]
    [hxgm30.language.syntagmata.util :as util]))

(defn load
  [type language]
  (->> (format "syntagmata/corpora/%s/%s.txt"
               (name type)
               (name language))
       io/resource
       io/reader
       line-seq
       (mapcat #(string/split % #" "))
       (remove empty?)
       (map (comp string/lower-case
                  #(string/replace % "\"" "")
                  #(string/replace % "'s" "")
                  #(string/replace % "'ll" "")
                  #(string/replace % "'d" "")
                  #(string/replace % "'re" "")))
       sort))

(defn load-oneline-file
  [type language]
  (->> language
       (load type)
       first
       (map str)
       set))

(defn load-consonants
  [language]
  (load-oneline-file :consonants language))

(defn load-vowels
  [language]
  (load-oneline-file :vowels language))

(defn load-alphabet
  [language]
  (set/union (load-consonants language) (load-vowels language)))

(defn re-not-alpha
  [language]
  (str "[^" (apply str (load-alphabet language)) "]"))

(defn re-alpha
  [language]
  (str "[" (apply str (load-alphabet language)) "]"))

(defn re-vowel
  [language]
  (str "[" (apply str (load-vowels language)) "]"))

(defn re-consonant
  [language]
  (str "[" (apply str (load-consonants language)) "]"))

(defn re-sound-transition
  [language]
  (str (re-consonant :sanskrit) "?" (re-vowel :sanskrit) "+"))

(defn clean-word
  [language word]
  (-> word
      (string/replace (re-pattern (re-not-alpha language))
                      " ")
      (string/split #"\s")))

(defn clean-words
  [language words]
  (->> words
      (mapcat #(clean-word language %))
      (remove empty?)))

(defn load-source
  [language]
  (let [raw-words (load :sources language)]
    (clean-words language raw-words)))
