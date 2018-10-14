(ns hxgm30.language.components.lang
  (:require
    [clojusc.twig :as logger]
    [com.stuartsierra.component :as component]
    [hxgm30.db.plugin.component :as db-component]
    [hxgm30.db.plugin.redis.api.db :as db]
    [hxgm30.language.common :as common]
    [hxgm30.language.components.config :as config]
    [hxgm30.language.gen.corpus :as corpus]
    [hxgm30.language.gen.core :as gen]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-generator
  [system gen-type]
  (get-in system [:lang :generators gen-type]))

(defn get-db
  [system]
  (get-in system [:lang :db]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Language Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- -ingest-all-stats
  [system]
  (db/ingest-stats
    (get-db system)
    (concat (for [lang common/supported-languages
                  gen common/gen-types]
              (do
              (log/debugf "lang, gen: %s, %s" lang  gen)
              [[lang gen] (apply corpus/undump [lang gen])]))
            (for [race common/supported-names
                  name-type common/supported-name-types
                  gen common/gen-types]
              (do
              (log/debugf "race, name-type, gen: %s, %s, %s" race name-type gen)
              [[race name-type gen]
               (apply corpus/undump [race name-type gen])])))))

(defn ingest-stats
  [system & args]
  (if (nil? args)
    (-ingest-all-stats system)
    (apply
      db/ingest-stats
      (concat
       [(get-db system)]
       args
       [(apply corpus/undump args)]))))

(def lang-stats #(db/lang-stats (get-db %1) %2 %3))
(def name-stats #(db/name-stats (get-db %1) %2 %3 %4))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord LangServer [
  db
  dictionary
  generators])

(defn start
  [this]
  (let [lang-db (db/create-lang-db (db-component/db-conn this))
        ;lang-db :thing
        ]
    (log/info "Starting language component ...")
    (log/debug "Using connection:" (db-component/db-conn this))
    (log/debug "Started language component.")
    (assoc this :db lang-db
                :generators {
                  :markov (gen/create-content-generator
                           this :markov)
                  :syntagmata (gen/create-content-generator
                               this :syntagmata)})))

(defn stop
  [this]
  (log/info "Stopping language component ...")
  (log/debug "Stopped language component.")
  (assoc this :db nil
              :dictionary nil
              :generators nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend LangServer
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  []
  (map->LangServer {:db nil}))

