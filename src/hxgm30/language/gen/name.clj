(ns hxgm30.language.gen.name
  (:require
    [clojure.string :as string]
    [clojusc.system-manager.core :as system-manager]
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

(defn gen-name
  [this race name-type]
  (string/capitalize
    (gen/word this
      (gen/stats (:stats-gen this) race name-type))))

(defn last
  [system race]
  (gen-name system race :surname))

(defn female
  [system race]
  (gen-name system race :female))

(defn male
  [system race]
  (gen-name system race :male))

(defn- print-sample
  [system race]
  (let [lastname (last system race)]
    (print (format "\n%s\n\tFemale: %s %s\n\tMale: %s %s\n"
                   (string/capitalize (name race))
                   (female system race)
                   lastname
                   (male system race)
                   lastname))))

(defn run
  ([this]
    (doall
      (for [race common/supported-names]
        (print-sample this race))))
  ([this race]
    (print-sample this race)))

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
