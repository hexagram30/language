(ns hxgm30.language.gen.lang.core
  (:require
    [hxgm30.language.gen.corpus :as corpus]
    [hxgm30.language.gen.rand :as rand]))

(def word #(rand/word %1 (corpus/undump-syntagmata %2)))
(def sentence #(rand/sentence %1 (corpus/undump-syntagmata %2)))
(def paragraph #(rand/paragraph %1 (corpus/undump-syntagmata %2)))
