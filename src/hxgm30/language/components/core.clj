(ns hxgm30.language.components.core
  (:require
    [com.stuartsierra.component :as component]
    [hxgm30.dice.components.random :as random]
    [hxgm30.httpd.components.server :as httpd]
    [hxgm30.language.components.config :as config]
    [hxgm30.language.components.lang :as lang]
    [hxgm30.language.components.logging :as logging]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Common Configuration Components   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn cfg
  [cfg-data]
  {:config (config/create-component cfg-data)})

(def log
  {:logging (component/using
             (logging/create-component)
             [:config])})

(def rnd
  {:random (component/using
            (random/create-component)
            [:config :logging])})

(def language
  {:lang (component/using
            (lang/create-component)
            [:config :logging :random])})

(def httpd
  {:httpd (component/using
           (httpd/create-component)
           [:config :logging :random :lang])})

;;; Additional components for systems that want to supress logging (e.g.,
;;; systems created for testing).

(def rnd-without-logging
  {:random (component/using
            (random/create-component)
            [:config])})

(def language-without-logging
  {:lang (component/using
            (lang/create-component)
            [:config :random])})

(def httpd-without-logging
  {:httpd (component/using
           (httpd/create-component)
           [:config :random :lang])})

(defn basic
  [cfg-data]
  (merge (cfg cfg-data)
         log))

(defn main
  [cfg-data]
  (merge (basic cfg-data)
         rnd
         httpd))

(defn main
  [cfg-data]
  (merge (basic cfg-data)
         rnd
         language
         httpd))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Initializations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-config-only
  []
  (component/map->SystemMap (config/build-config)))

(defn initialize-bare-bones
  []
  (-> (config/build-config)
      basic
      component/map->SystemMap))

(defn initialize-without-logging
  []
  (-> (config/build-config)
      cfg
      (merge rnd-without-logging
             language-without-logging
             httpd-without-logging)
      component/map->SystemMap))

(defn initialize
  []
  (-> (config/build-config)
      main
      component/map->SystemMap))

(def init-lookup
  {:basic #'initialize-bare-bones
   :cli #'initialize-without-logging
   :main #'initialize
   :testing-config-only #'initialize-config-only
   :testing #'initialize-without-logging})

(defn init
  ([]
    (init :main))
  ([mode]
    ((mode init-lookup))))

(def cli #(init :cli))
(def integration-testing #(init :testing-config-only))
(def testing #(init :testing))
