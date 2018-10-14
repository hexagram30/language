(ns hxgm30.language.repl
  (:require
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.string :as string]
    [clojure.tools.namespace.repl :as repl]
    [clojusc.system-manager.core :as system-manager :refer [reset shutdown startup system]]
    [clojusc.twig :as logger]
    [com.stuartsierra.component :as component]
    [hxgm30.db.plugin.redis.api.db :as db-api]
    [hxgm30.db.plugin.redis.api.schema :as schema]
    [hxgm30.dice.components.random :as random]
    [hxgm30.language.common :as common]
    [hxgm30.language.components.config :as config]
    [hxgm30.language.components.core]
    [hxgm30.language.components.lang :as db]
    [hxgm30.language.gen.assembled.core :as assembled]
    [hxgm30.language.gen.assembled.mythgarthur :as mythgarthur]
    [hxgm30.language.gen.assembled.rook :as rook]
    [hxgm30.language.gen.core :as gen]
    [hxgm30.language.gen.corpus :as corpus]
    [hxgm30.language.gen.name :as name]
    [hxgm30.language.io :as lang-io]
    [hxgm30.language.natural.core :as nlp]
    [hxgm30.language.util :as util]
    [taoensso.carmine :as redis]
    [trifl.java :refer [show-methods]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Initial Setup & Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def setup-options {
  :init 'hxgm30.language.components.core/init
  :after-refresh 'hxgm30.language.repl/init-and-startup
  :throw-errors false})

(defn init
  "This is used to set the options and any other global data.

  This is defined in a function for re-use. For instance, when a REPL is
  reloaded, the options will be lost and need to be re-applied."
  []
  (logger/set-level! '[hxgm30] :debug)
  (system-manager/setup-manager setup-options))

(defn init-and-startup
  "This is used as the 'after-refresh' function by the REPL tools library.
  Not only do the options (and other global operations) need to be re-applied,
  the system also needs to be started up, once these options have be set up."
  []
  (init)
  (system-manager/startup))

; It is not always desired that a system be started up upon REPL loading.
; Thus, we set the options and perform any global operations with init,
; and let the user determine when then want to bring up (a potentially
; computationally intensive) system.
(init)

(defn banner
  []
  (println (slurp (io/resource "text/banner.txt")))
  :ok)

;; Words and names generator
(comment
  (def m (gen/create-content-generator (system) :markov))
  (def stats (gen/stats (:stats-gen m) :halfling :male))
  (gen/word m stats 1)
  (gen/word m stats 2)
  (gen/word m stats 3)
  )

;; Natural language
(comment
  (def tkns (nlp/tokenize "take the hat from the table"))
  tkns
  ; ["take" "the" "hat" "from" "the" "table" "."]
  (def tagged (nlp/pos-tag tkns))
  tagged
  ; (["take" "VB"]
  ;  ["the" "DT"]
  ;  ["hat" "NN"]
  ;  ["from" "IN"]
  ;  ["the" "DT"]
  ;  ["table" "NN"]
  ;  ["." "."])
  (def chunked (nlp/chunker tagged))
  chunked
  ; ({:phrase ["take"] :tag "VP"}
  ;  {:phrase ["the" "hat"] :tag "NP"}
  ;  {:phrase ["from"] :tag "PP"}
  ;  {:phrase ["the" "table"] :tag "NP"})
  )
