(ns hxgm30.language.syntagmata.lang.core
  (:require
    [hxgm30.language.syntagmata.corpus :as corpus]
    [hxgm30.language.syntagmata.rand :as rand]))

(def supported-languages
  [:afrikaans
   :arabic
   :chinese
   :english
   :finnish
   :french
   :gaelic
   :german
   :greek
   :hebrew
   :hindi
   :japanese
   :korean
   :latin
   :oldenglish
   :oldnorse
   :onomatopoetic
   :pie
   :russian
   :sanskrit
   :scots
   :spanish])

(def supported-names
  [:dragonborn
   :dwarf
   :elf
   :gnome
   :halfling
   :human
   :orc
   :tiefling])

(def supported-name-types
  [:female
   :male
   :surname])

(def word #(rand/word %1 (corpus/undump-syntagmata %2)))
(def sentence #(rand/sentence %1 (corpus/undump-syntagmata %2)))
(def paragraph #(rand/paragraph %1 (corpus/undump-syntagmata %2)))
