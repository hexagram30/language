(ns hxgm30.language.components.lang
  (:require
    [clojusc.twig :as logger]
    [com.stuartsierra.component :as component]
    [hxgm30.db.plugin.component :as db-component]
    [hxgm30.db.plugin.redis.api.db :as db]
    [hxgm30.language.components.config :as config]
    [hxgm30.language.gen.core :as gen]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Language Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-generator
  [system gen-type]
  (get-in system [:lang :generators gen-type]))

(defn get-db
  [system]
  (get-in system [:lang :db]))

(defn ingest-stats
  [system & args]
  (apply db/ingest-stats
         (cons (get-db system) (butlast args))
         ))

(def lang-stats #(db/lang-stats (get-db %1) %2))
(def name-stats #(db/name-stats (get-db %1) %2 %3))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord LangServer [
  conn
  dictionary
  generators])

(defn start
  [this]
  (let [lang-db (db/create-lang-db (db-component/db-conn this))]
    (log/info "Starting language component ...")
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
  (map->LangServer {}))
