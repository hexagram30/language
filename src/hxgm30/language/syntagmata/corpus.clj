(ns hxgm30.language.syntagmata.corpus
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clojure.string :as string]
    [hxgm30.language.syntagmata.util :as util])
  (:refer-clojure :exclude [load]))

(def file-template "syntagmata/corpora/%s/%s.txt")
(def file-resource-template (str "resources/" file-template))
(def name-file-template "syntagmata/corpora/names/%s/%s/%s.txt")


(defn load
  ([type language]
    (load (format file-template
                  (name type)
                  (name language))))
  ([race name-type data-type]
    (load (format name-file-template
                  (name race)
                  (name name-type)
                  (name data-type))))
  ([fullpath]
    (->> fullpath
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
         sort)))

(defn extract-alphabet
  ([type language]
    (extract-alphabet (format file-template
                      (name type)
                      (name language))))
  ([race name-type data-type]
    (extract-alphabet (format name-file-template
                      (name race)
                      (name name-type)
                      (name data-type))))
  ([fullpath]
    (->> fullpath
         load
         (map #(into #{} %))
         (apply clojure.set/union)
         clojure.string/join)))

(defn dump
    ([type language data]
      (dump (format file-resource-template
                    (name type)
                    (name language))
            data))
    ([race name-type data-type data]
      (dump (format name-file-template
                    (name race)
                    (name name-type)
                    (name data-type))
            data))
    ([fullpath data]
      (spit fullpath data)))

(defn undump
    ([type language]
      (undump (format file-template
                      (name type)
                      (name language))))
    ([race name-type data-type data]
      (undump (format name-file-template
                      (name race)
                      (name name-type)
                      (name data-type))))
    ([fullpath]
      (->> fullpath
           io/resource
           slurp
           edn/read-string)))

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
  (str "[^" (string/join (load-alphabet language)) "]"))

(defn re-alpha
  [language]
  (str "[" (string/join (load-alphabet language)) "]"))

(defn re-vowel
  [language]
  (str "[" (string/join (load-vowels language)) "]"))

(defn re-consonant
  [language]
  (str "[" (string/join (load-consonants language)) "]"))

(defn re-sound-transitions
  [language]
  (format "(%s+)|(%s+%s+)|(%s+)"
          (re-vowel language)
          (re-consonant language)
          (re-vowel language)
          (re-consonant language)))

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

(defn load-wordlist
    [language]
    (load :wordlists language))

(defn load-stats
    [language]
    (load :stats language))

(defn save-wordlist
  [language data]
  (doall
    (dump :wordlists language (string/join "\n" data))))
