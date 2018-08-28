(ns hxgm30.language.syntagmata.lang.names
  (:require
    [clojure.string :as string]
    [clojusc.system-manager.core :as system-manager]
    [clojusc.twig :as logger]
    [hxgm30.dice.components.random :as random]
    [hxgm30.language.common :as common]
    [hxgm30.language.components.core]
    [hxgm30.language.syntagmata.core :as syntagmata]
    [hxgm30.language.syntagmata.corpus :as corpus]
    [hxgm30.language.syntagmata.rand :as rand]
    [hxgm30.language.util :as util])
  (:refer-clojure :exclude [last])
  (:gen-class))

(defn gen-name
  [system race name-type]
  (string/capitalize (rand/word system (corpus/undump-syntagmata race name-type))))

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
  ([system]
    (doall
      (for [race common/supported-names]
        (print-sample system race))))
  ([system race]
    (print-sample system race)))

(defn -main
  [& args]
  (logger/set-level! '[hxgm30] :error)
  (system-manager/setup-manager {:init 'hxgm30.language.components.core/cli
                                 :throw-errors true})
  (system-manager/startup)
  (let [sys (system-manager/system)]
    (if-let [race (first args)]
      (run sys (keyword race))
      (run sys)))
  (println))
