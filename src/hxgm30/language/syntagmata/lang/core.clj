(ns hxgm30.language.syntagmata.lang.core
  (:require
    [hxgm30.language.syntagmata.corpus :as corpus]
    [hxgm30.language.syntagmata.rand :as rand]))

(def word #(rand/word %1 (corpus/undump-syntagmata %2)))
(def sentence #(rand/sentence %1 (corpus/undump-syntagmata %2)))
(def paragraph #(rand/paragraph %1 (corpus/undump-syntagmata %2)))
