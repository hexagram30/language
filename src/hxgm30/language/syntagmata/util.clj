(ns hxgm30.language.syntagmata.util
  (:require
    [clojure.java.io :as io]
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
