(ns hxgm30.language.io
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [clojure.pprint :as pprint]
    [clojure.string :as string]))

(defn load-lines
  [fullpath]
  (->> fullpath
       io/resource
       io/reader
       line-seq))

(defn load-clean-lines
  [fullpath]
  (->> fullpath
       load-lines
       (mapcat #(string/split % #" "))
       (remove empty?)
       (map (comp string/lower-case
                  #(string/replace % "\"" "")
                  #(string/replace % "'s" "")
                  #(string/replace % "'ll" "")
                  #(string/replace % "'d" "")
                  #(string/replace % "'re" "")))
       sort))

(defn dump
  [fullpath data]
  (spit fullpath (with-out-str (pprint/pprint data))))

(defn undump
  [fullpath]
  (->> fullpath
       io/resource
       slurp
       edn/read-string))
