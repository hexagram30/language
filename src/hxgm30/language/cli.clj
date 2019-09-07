(ns hxgm30.language.cli
  (:require
    [clojusc.system-manager.core :as system-manager]
    [clojusc.twig :as logger]
    [com.stuartsierra.component :as component]
    [hxgm30.language.components.config :as config]
    [hxgm30.language.components.core]
    [hxgm30.language.components.lang :as lang-component]
    [hxgm30.language.gen.assembled.core :as asm]
    [hxgm30.language.gen.core :as gen]
    [hxgm30.language.gen.name :as name]
    [taoensso.timbre :as log]
    [trifl.java :as java]))

(defn setup-system
  []
  (logger/set-level! '[hxgm30] :info)
  (system-manager/setup-manager {:init 'hxgm30.language.components.core/cli
                                 :throw-errors true})
  (system-manager/startup)
  (system-manager/system))

(defn -main
  [& args]
  (let [cmd (keyword (first args))
        sys (setup-system)
        gen-type (config/lang-default-generator-mode sys)
        generator (lang-component/get-generator sys gen-type)]
    (log/debug "Got generator:" generator)
    (case cmd
      :regen-markov (do
                      (println "Regenerating markov-chain data ...\n")
                      (->> :markov
                           (lang-component/get-generator sys)
                           :stats-gen
                           gen/regen-stats))
      :regen-syntagmata (do
                          (println "Regenerating syntagmata data ...\n")
                          (->> :syntagmata
                               (lang-component/get-generator sys)
                               :stats-gen
                               gen/regen-stats))
      :names (if-let [race (second args)]
               (if-let [count (nth args 2)]
                  (name/run generator (keyword race) (Integer/parseInt count))
                  (name/run generator (keyword race)))
               (name/run generator))
      :assemble (let [world (keyword (first args))
                      language (keyword (second args))]
                  (asm/run generator world language)))
    (println)
    (component/stop sys)))
