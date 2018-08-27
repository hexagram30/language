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
(def name-file-resource-template (str "resources/" name-file-template))

(defn load-clean
  [fullpath]
  )
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
      (dump (format name-file-resource-template
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
    ([race name-type data-type]
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
  ([type language]
    (load-oneline-file (load type language)))
  ([race name-type data-type]
    (load-oneline-file (load race name-type data-type)))
  ([fullpath]
    (->> fullpath
         first
         (map str)
         set)))

(defn load-consonants
  ([language]
    (load-oneline-file :consonants language))
  ([race name-type]
    (load-oneline-file race name-type :consonants)))

(defn load-vowels
  ([language]
    (load-oneline-file :vowels language))
  ([race name-type]
    (load-oneline-file race name-type :vowels)))

(defn load-alphabet
  [& args]
  (set/union (apply load-consonants args)
             (apply load-vowels args)))

(defn re-not-alpha
  [& args]
  (str "[^" (string/join (apply load-alphabet args)) "]"))

(defn re-alpha
  [& args]
  (str "[" (string/join (apply load-alphabet args)) "]"))

(defn re-vowel
  [& args]
  (str "[" (string/join (apply load-vowels args)) "]"))

(defn re-consonant
  [& args]
  (str "[" (string/join (apply load-consonants args)) "]"))

(defn re-sound-transitions
  [& args]
  (format "(%s+)|(%s+%s+)|(%s+)"
          (apply re-vowel args)
          (apply re-consonant args)
          (apply re-vowel args)
          (apply re-consonant args)))

(defn clean-word
  [word & args]
  (-> word
      (string/replace (re-pattern (apply re-not-alpha args))
                      " ")
      (string/split #"\s")))

(defn clean-words
  [words & args]
  (->> words
      (mapcat #(apply clean-word (cons % args)))
      (remove empty?)))

(defn load-source
  [language]
  (let [raw-words (load :sources language)]
    (clean-words language raw-words)))

(defn load-wordlist
  ([language]
    (load :wordlists language))
  ([race name-type]
    (load race name-type :list)))

(defn load-stats
  ([language]
    (undump :stats language))
  ([race name-type]
    (undump race name-type :stats)))

(defn save-wordlist
  [language data]
  (doall
    (dump :wordlists language (string/join "\n" data))))
