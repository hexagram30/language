(ns hxgm30.language.syntagmata.corpus
  (:require
    [clojure.set :as set]
    [clojure.string :as string]
    [hxgm30.language.io :as io]
    [hxgm30.language.syntagmata.util :as util]))

(def file-template "corpora/syntagmata/%s/%s")
(def name-file-template "corpora/syntagmata/names/%s/%s/%s")

(defn fullpath
  ([type language]
    (format file-template
            (name type)
            (name language)))
  ([race name-type data-type]
    (format name-file-template
            (name race)
            (name name-type)
            (name data-type))))

(defn load-lines
  ([type language]
    (load-lines (fullpath type language)))
  ([race name-type data-type]
    (load-lines (fullpath race name-type data-type)))
  ([fullpath]
    (io/load-clean-lines fullpath)))

(defn load-clean-lines
  ([type language]
    (load-clean-lines (fullpath type language)))
  ([race name-type data-type]
    (load-clean-lines (fullpath race name-type data-type)))
  ([fullpath]
    (io/load-clean-lines fullpath)))

(defn extract-alphabet
  ([type language]
    (extract-alphabet (fullpath type language)))
  ([race name-type data-type]
    (extract-alphabet (fullpath race name-type data-type)))
  ([fullpath]
    (->> fullpath
         io/load-clean-lines
         (map set)
         (apply clojure.set/union)
         (apply sorted-set)
         clojure.string/join)))

(defn dump
    ([type language data]
      (dump (fullpath type language)
            data))
    ([race name-type data-type data]
      (dump (fullpath race name-type data-type) data))
    ([fullpath data]
      (io/dump (str "resources/" fullpath) data)))

(defn undump
    ([type language]
      (undump (fullpath type language)))
    ([race name-type data-type]
      (undump (fullpath race name-type data-type)))
    ([fullpath]
      (io/undump fullpath)))

(defn load-oneline-file
  ([type language]
    (load-oneline-file (fullpath type language)))
  ([race name-type data-type]
    (load-oneline-file (fullpath race name-type data-type)))
  ([fullpath]
    (io/load-oneline-file fullpath)))

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

(defn string-chars->string
  [string-chars]
  (string/replace (string/join string-chars) #"-" (str "\"" "-")))

(defn regex-range
  [string-chars]
  (str "[" (string-chars->string string-chars) "]"))

(defn regex-not-range
  [string-chars]
  (str "[^" (string-chars->string string-chars) "]"))

(defn re-not-alpha
  [& args]
  (regex-not-range (apply load-alphabet args)))

(defn re-alpha
  [& args]
  (regex-range (apply load-alphabet args)))

(defn re-vowel
  [& args]
  (regex-range (apply load-vowels args)))

(defn re-consonant
  [& args]
  (regex-range (apply load-consonants args)))

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
  (let [raw-words (load-clean-lines :sources language)]
    (clean-words raw-words language)))

(defn load-wordlist
  ([language]
    (load-lines :wordlists language))
  ([race name-type]
    (load-lines race name-type :list)))

(defn load-stats
  ([language]
    (undump :stats language))
  ([race name-type]
    (undump race name-type :stats)))

(defn save-wordlist
  [language data]
  (doall
    (dump :wordlists language (string/join "\n" data))))
