(ns hxgm30.language.natural.core
  (:require
    [hxgm30.language.natural.util :as util]
    [opennlp.nlp :as nlp]
    [opennlp.treebank :as treebank]))

(def tokenize
  (comp (nlp/make-tokenizer (util/get-model "en-token.bin"))
        util/close-sentence))

(def pos-tag
  (nlp/make-pos-tagger
    (util/get-model "en-pos-maxent.bin")))

(def chunker
  (treebank/make-treebank-chunker
    (util/get-model "en-chunker.bin")))

(defn determiner?
  [tagged-element]
  (= "DT" (second tagged-element)))

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
