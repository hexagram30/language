(ns hxgm30.language.syntagmata.util
  (:require
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clojure.string :as string]))

(def percent-multiplier
  "Note that the multiplier here is selected such that enough significant
  digits are used to avoid rounding errors when converting back to percent."
  1000)

(defn percent->freq
  [percent]
  (* percent percent-multiplier))

(defn frequency->percent-range
  "A reducer that, given a frequency and an accumulator, returns an updated
  accumulator with the given frequency converted to a percent range (appended
  to the accumulator). The first argument is expectred to be provided as a
  partial."
  [total acc [k v]]
  (let [last-value (or (last (first (last acc))) 0)
        v-percent (/ v total)]
    (conj acc [[last-value (+ v-percent last-value)] k])))

(defn frequencies->percent-ranges
  "Create a lookup of key/value pairs where the key is the range of percentages
  where a syllable count occurs, and the value is the syllable count."
  [freqs]
  (let [total (reduce + 0.0 (vals freqs))]
    (->> freqs
         (reduce (partial frequency->percent-range total) [])
         (into {}))))

(defn percent->
  "Given a percent (as a float between 0.0 and 1.0) and a lookup table of
  percent ranges, find the range in which the given percent falls, and return
  the associated value."
  [percent percent-ranges]
  (->> percent-ranges
       (map (fn [[[low high] syll-count]]
             (when (and (< low percent) (< percent high))
               syll-count)))
       (remove nil?)
       first))
