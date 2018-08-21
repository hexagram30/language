(ns hxgm30.language.syntagmata.util
  (:require
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clojure.string :as string]))

(defn percent->
  [rand-float percent-ranges]
  (->> percent-ranges
       (map (fn [[[low high] syll-count]]
             (when (and (< low rand-float) (< rand-float high))
               syll-count)))
       (remove nil?)
       first))
