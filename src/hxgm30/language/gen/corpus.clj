(ns hxgm30.language.gen.corpus
  (:require
    [clojure.set :as set]
    [clojure.string :as string]
    [hxgm30.language.io :as io]
    [hxgm30.language.util :as util]))

(def file-template "corpora/%s/%s")
(def name-file-template "corpora/names/%s/%s/%s")

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
         (apply sorted-set))))

(defn dump
    ([type language data]
      (dump (fullpath type language) data))
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

(defn undump-consonants
  ([language]
    (undump :consonants language))
  ([race name-type]
    (undump race name-type :consonants)))

(defn undump-vowels
  ([language]
    (undump :vowels language))
  ([race name-type]
    (undump race name-type :vowels)))

(defn undump-alphabet
  [& args]
  (apply sorted-set
    (set/union (apply undump-consonants args)
               (apply undump-vowels args))))

(defn string-chars->string
  [string-chars]
  (string/replace (string/join string-chars) "-" (str "\"" "-")))

(defn regex-range
  [string-chars]
  (str "[" (string-chars->string string-chars) "]"))

(defn regex-not-range
  [string-chars]
  (str "[^" (string-chars->string string-chars) "]"))

(defn re-alpha
  [& args]
  (regex-range (apply undump-alphabet args)))

(defn re-not-alpha
  [& args]
  (regex-not-range (apply undump-alphabet args)))

(defn re-consonant
  [& args]
  (regex-range (apply undump-consonants args)))

(defn re-vowel
  [& args]
  (regex-range (apply undump-vowels args)))

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

(defn undump-syntagmata
  ([language]
    (undump :syntagmata language))
  ([race name-type]
    (undump race name-type :syntagmata)))

(defn dump-syntagmata
  ([language data]
    (dump :syntagmata language data))
  ([race name-type data]
    (dump race name-type :syntagmata data)))

(defn undump-markov
  ([language]
    (undump :markov language))
  ([race name-type]
    (undump race name-type :markov)))

(defn dump-markov
  ([language data]
    (dump :markov language data))
  ([race name-type data]
    (dump race name-type :markov data)))

(defn save-wordlist
  [language data]
  (doall
    (dump :wordlists language (string/join "\n" data))))
