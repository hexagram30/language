(ns hxgm30.language.natural.core
  (:require
    [hxgm30.language.natural.util :as util]
    [opennlp.nlp :as nlp]
    [opennlp.treebank :as treebank]
    [wordnet.core :as wordnet])
  (:import
    (clojure.lang Keyword)))

(def tokenize
  (comp (nlp/make-tokenizer (util/get-model "en-token.bin"))
        util/close-sentence))

(def pos-tag
  (nlp/make-pos-tagger
    (util/get-model "en-pos-maxent.bin")))

(def chunker
  (treebank/make-treebank-chunker
    (util/get-model "en-chunker.bin")))

(def wordnet-dict (wordnet/make-dictionary (util/get-wordnet-dict)))

(defn determiner?
  [tagged-element]
  (= "DT" (second tagged-element)))

(defn synonyms
  [word ^Keyword pos]
  (->> (wordnet-dict word pos)
       (mapcat (comp wordnet/words wordnet/synset))
       (reduce (fn [acc x] (conj acc (:lemma x)))
               #{})))

(defn related-words
  [word ^Keyword pos]
  (->> (wordnet-dict word pos)
       (mapcat (comp #(wordnet/related-synsets % :hypernym) wordnet/synset))
       (mapcat wordnet/words)
       (reduce (fn [acc x] (conj acc (:lemma x)))
               #{})))

(defn parse
  ([sentence]
    (parse sentence {:with-determiners? true}))
  ([sentence opts]
    (let [tokens (tokenize sentence)
          tagged (pos-tag tokens)]
      {:tagged (if (:with-determiners? opts)
                 tagged
                 (remove determiner? tagged))
       :chunked (chunker tagged)})))
