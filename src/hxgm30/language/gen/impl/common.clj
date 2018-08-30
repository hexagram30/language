(ns hxgm30.language.gen.impl.common
  (:require
    [clojure.string :as string]
    [hxgm30.dice.components.random :as random]
    [hxgm30.language.common :as common]
    [hxgm30.language.gen.corpus :as corpus]
    [hxgm30.language.util :as util]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-generator
  [this]
  (:generator this))

(defn get-reader
  [this]
  (:reader this))

(defn get-writer
  [this]
  (:writer this))

(defn get-stats-reader
  [this]
  (get-generator (:stats-gen this)))

(defn get-stats-reader
  [this]
  (get-reader (:stats-gen this)))

(defn get-stats-writer
  [this]
  (get-writer (:stats-gen this)))

(defn get-word-fn
  [this]
  (:word-fn this))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Stats Generator Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn regen-language-stats
  [this]
  (doall
    (for [language common/supported-languages]
      (do
        (log/debugf "Processing %s ..." language)
        (corpus/dump-markov language ((get-generator this) this language))
        {language :ok}))))

(defn regen-name-stats
  [this]
  (doall
    (for [race common/supported-names
          name-type common/supported-name-types]
      (do
        (log/debugf "Processing %s + %s ..." race name-type)
        ((get-writer this) race name-type ((get-generator this) this race name-type))
        {race {name-type :ok}}))))

(defn regen-stats
  ([this]
    (regen-language-stats this)
    (regen-name-stats this)
    :ok)
  ([this language]
    ((get-writer this) language ((get-generator this) this language)))
  ([this race name-type]
    ((get-writer this) race
                        name-type
                        ((get-generator) this race name-type))))

(defn stats
  ([this language]
    ((get-reader this) language))
  ([this race name-type]
    ((get-reader this) race name-type)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Concent Generator Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn syllable-count
  [this stats]
  (util/percent-> (random/float (:system this))
                  (get-in stats [:pseudo-syllables :percent-ranges])))

(defn sentence
  ([this stats-or-lang]
    (if (keyword? stats-or-lang)
      (sentence this ((get-stats-reader this) stats-or-lang))
      (sentence this stats-or-lang (random/int (:system this) 10))))
  ([this stats words]
    (str
      (->> words
           inc
           range
           (map (fn [_] ((get-word-fn this) this stats)))
           (string/join " ")
           string/capitalize)
      ".")))

(defn paragraph
  ([this stats-or-lang]
    (if (keyword? stats-or-lang)
      (paragraph this ((get-stats-reader this) stats-or-lang))
      (paragraph this stats-or-lang (random/int (:system this) 10))))
  ([this stats sentence-count]
    (string/join
      " "
      (->> sentence-count
           inc
           range
           (map (fn [_] (sentence this stats)))))))
