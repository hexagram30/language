(ns hxgm30.language.components.core
  (:require
    [clojusc.process.manager.components.docker :as docker]
    [com.stuartsierra.component :as component]
    [hxgm30.db.plugin.backend :as backend]
    [hxgm30.dice.components.random :as random]
    [hxgm30.httpd.components.server :as httpd]
    [hxgm30.language.components.config :as config]
    [hxgm30.language.components.dictionary :as dictionary]
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

(defn db
  [cfg-data]
  (let [backend (get-in cfg-data [:backend :plugin])]
    {:backend (component/using
               (backend/create-component backend)
               [:config :logging])}))

(def language
  {:lang (component/using
          (lang/create-component)
          [:config :logging :random :backend])})

(def dictdb
  {:redis-search (component/using
                  (docker/create-component
                    :neo4j
                    {:image-id "hexagram30/redisearch:latest"
                     :ports ["127.0.0.1:6380:6379"]
                     :volumes [(str (System/getProperty "user.dir") "/data/redis-search:/data")]
                     :container-id-file "/tmp/hxgm30-lang-redissearch-container-id"})
                  [:config :logging])})

(def dict
  {:dictionary (component/using
                (dictionary/create-component)
                [:config :logging :redis-search :lang])})

(def http
  {:httpd (component/using
           (httpd/create-component)
           [:config :logging :random :backend :lang :dictionary])})

;;; Additional components for systems that want to suppress logging (e.g.,
;;; systems created for testing).

(def rnd-without-logging
  {:random (component/using
            (random/create-component)
            [:config])})

(defn db-without-logging
  [cfg-data]
  (let [backend (get-in cfg-data [:backend :plugin])]
    {:backend (component/using
               (backend/create-component backend)
               [:config])}))

(def language-without-logging
  {:lang (component/using
            (lang/create-component)
            [:config :random])})

(def http-without-logging
  {:httpd (component/using
           (httpd/create-component)
           [:config :random :backend :lang :dictionary])})

(defn basic
  [cfg-data]
  (merge (cfg cfg-data)
         log))

(defn main
  [cfg-data]
  (merge (basic cfg-data)
         rnd
         (db cfg-data)
         language
         dictdb
         dict
         http))

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
  (let [cfg-data (config/build-config)]
    (-> cfg-data
        cfg
        (merge rnd-without-logging
               (db-without-logging cfg-data)
                language-without-logging
               http-without-logging)
        component/map->SystemMap)))

(defn initialize
  []
  (let [cfg-data (config/build-config)]
    (-> cfg-data
        main
        component/map->SystemMap)))

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
