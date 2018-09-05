(ns hxgm30.language.components.lang
  (:require
    [clojusc.twig :as logger]
    [com.stuartsierra.component :as component]
    [hxgm30.language.components.config :as config]
    [hxgm30.language.gen.core :as gen]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Language Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-generator
  [system gen-type]
  (get-in system [:lang :generators gen-type]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord LangServer [
  dictionary
  generators])

(defn start
  [this]
  (let [port (config/lang-udp-server-port this)]
    (log/infof "Starting language component ..." port)
    (log/debug "Started language component.")
    (assoc this :generators {
                  :markov (gen/create-content-generator
                           this :markov)
                  :syntagmata (gen/create-content-generator
                               this :syntagmata)})))

(defn stop
  [this]
  (log/info "Stopping language component ...")
  (log/debug "Stopped language component.")
  (assoc this :dictionary nil
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
