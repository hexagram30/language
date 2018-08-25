(ns hxgm30.language.config
  (:require
   [hxgm30.common.file :as common]))

(def config-file "hexagram30-config/language.edn")

(defn data
  ([]
    (data config-file))
  ([filename]
    (common/read-edn-resource filename)))
