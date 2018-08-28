(ns hxgm30.language.syntagmata.lang.merge
  (:require
    [clojure.string :as string]
    [clojusc.system-manager.core :as system-manager]
    [clojusc.twig :as logger]
    [hxgm30.dice.components.random :as random]
    [hxgm30.language.components.core]
    [hxgm30.language.syntagmata.core :as syntagmata]
    [hxgm30.language.syntagmata.corpus :as corpus]
    [hxgm30.language.syntagmata.lang.fictional.mythgarthur :as mythgarthur]
    [hxgm30.language.syntagmata.lang.fictional.rook :as rook]
    [hxgm30.language.syntagmata.rand :as rand]
    [hxgm30.language.syntagmata.util :as util])
  (:gen-class))

(defn select
  "Expects a signature like the following:
  ```
    {:english 2
     :chinese 5
     :arabic 3}
  ```

  Thus, a call should looke like this:
  ```
    (select {:english 2
             :chinese 5
             :arabic 3})
  ```"
  [system lang-freqs]
  (util/percent-> (random/float system) (util/frequencies->percent-ranges lang-freqs)))

(defn word
  [system lang-freqs]
  (let [lang (select system lang-freqs)
        stats (syntagmata/syntagmata lang)
        syllables (rand/syllable-count system stats)]
    (case syllables
      1 (rand/syllable system stats :initial)
      2 (str (rand/syllable system stats :initial)
             (rand/syllable
              system
              (syntagmata/syntagmata
                (select system lang-freqs))
              :final))
      (str (rand/syllable system stats :initial)
           (->> syllables
                dec
                range
                (mapcat (fn [_]
                         (rand/syllable
                          system
                          (syntagmata/syntagmata (select system lang-freqs))
                          :medial)))
                (string/join ""))
           (rand/syllable
            system
            (syntagmata/syntagmata
              (select system lang-freqs))
            :final)))))

(defn sentence
  ([system lang-freqs]
    (sentence system lang-freqs (random/int system 10)))
  ([system lang-freqs words]
    (str
      (->> words
           inc
           range
           (map (fn [_] (word system lang-freqs)))
           (string/join " ")
           string/capitalize)
      ".")))

(defn paragraph
  ([system lang-freqs]
    (paragraph system lang-freqs (random/int system 10)))
  ([system lang-freqs sentence-count]
    (str
      (->> sentence-count
           inc
           range
           (map (fn [_] (sentence system lang-freqs)))
           (string/join " ")))))

(defn- print-sample
  [system name lang]
  (print (format "\n\t%s: %s\n" name (paragraph system lang))))

(defn run
  [system world language]
  (case world
    :rook (case language
            :rookish (print-sample system "Rookish" rook/rookish)
            :elani (print-sample system "Elani" rook/elani)
            :jas (print-sample system "Jas" rook/jas)
            :mux (print-sample system "Mux" rook/mux)
            (doall
              (do (run system :rook :rookish)
                  (run system :rook :elani)
                  (run system :rook :jas)
                  (run system :rook :mux))))
    :mythgarthur (case language
                   :orcish (print-sample system "Orcish" mythgarthur/orcish)
                   :elvish (print-sample system "Elvish" mythgarthur/elvish)
                   :human (print-sample system "Human" mythgarthur/human)
                   :dwarvish (print-sample system "Dwarvish" mythgarthur/dwarvish)
                   (doall
                     (do (run system :mythgarthur :orcish)
                         (run system :mythgarthur :elvish)
                         (run system :mythgarthur :human)
                         (run system :mythgarthur :dwarvish))))))


(defn -main
  [& args]
  (logger/set-level! '[hxgm30] :error)
  (system-manager/setup-manager {:init 'hxgm30.language.components.core/cli
                                 :throw-errors true})
  (system-manager/startup)
  (let [sys (system-manager/system)
        world (keyword (first args))
        language (keyword (second args))]
    (run sys world language)
    (println)))
