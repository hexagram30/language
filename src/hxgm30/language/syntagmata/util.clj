(ns hxgm30.language.syntagmata.util
  (:require
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clojure.string :as string]))

(defn load-corpora
  [type language]
  (->> (format "syntagmata/corpora/%s/%s.txt"
               (name type)
               (name language))
       io/resource
       io/reader
       line-seq
       (mapcat #(string/split % #" "))
       sort))

(defn load-oneline-file
  [type language]
  (->> language
       (load-corpora type)
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
