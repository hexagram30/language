(ns hxgm30.language.gen.name
  (:require
    [clojure.string :as string]
    [clojusc.twig :as logger]
    [hxgm30.dice.components.random :as random]
    [hxgm30.language.cli :as cli]
    [hxgm30.language.common :as common]
    [hxgm30.language.components.config :as config]
    [hxgm30.language.components.core]
    [hxgm30.language.gen.core :as gen]
    [hxgm30.language.gen.corpus :as corpus]
    [hxgm30.language.util :as util])
  (:refer-clojure :exclude [last])
  (:gen-class))

;; XXX Let's fix this up: have an API that takes the generator as the first arg,
;;     and then in the Language component, have functions that take the systm as
;;     the first component.

(defn gen-name
  ([generator race name-type]
    (string/capitalize
      (gen/word generator
                (gen/stats (:stats-gen generator) race name-type))))
  ([generator race name-type syllables]
    (string/capitalize
      (gen/word generator
                (gen/stats (:stats-gen generator) race name-type)
                syllables))))

(defn last
  [generator race]
  (gen-name generator race :surname))

(defn female
  [generator race]
  (gen-name generator race :female))

(defn male
  [generator race]
  (gen-name generator race :male))

(defn- print-sample
  [generator race]
  (let [lastname (last generator race)]
    (print (format "\n%s\n\tFemale: %s %s\n\tMale: %s %s\n"
                   (string/capitalize (name race))
                   (female generator race)
                   lastname
                   (male generator race)
                   lastname))))

(defn run
  ([generator]
    (doall
      (for [race common/supported-names]
        (print-sample generator race))))
  ([generator race]
    (print-sample generator race)))

(defn -main
  [& args]
  (let [sys (cli/setup-system)
        generator (gen/create-content-generator
                   sys
                   (config/lang-default-generator-mode sys))]
    (if-let [race (first args)]
      (run generator (keyword race))
      (run generator)))
  (println))
