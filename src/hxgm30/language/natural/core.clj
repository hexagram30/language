(ns hxgm30.language.natural.core
  (:require
    [hxgm30.language.natural.util :as util]
    [opennlp.nlp :as nlp]
    [opennlp.treebank :as treebank]))

(def tokenize
  ; [sentence]
  ; (let [tokenizer (nlp/make-tokenizer (util/get-model "en-token.bin"))]
  ;   (tokenizer (util/close-sentence sentence))))
  (comp (nlp/make-tokenizer (util/get-model "en-token.bin"))
        util/close-sentence))

(def pos-tag
  (nlp/make-pos-tagger
    (util/get-model "en-pos-maxent.bin")))

(def chunker
  (treebank/make-treebank-chunker
    (util/get-model "en-chunker.bin")))
