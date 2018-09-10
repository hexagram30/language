(ns hxgm30.language.util
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
