(ns hxgm30.language.cli
  (:require
    [clojusc.system-manager.core :as system-manager]
    [clojusc.twig :as logger]
    [hxgm30.language.components.config :as config]
    [hxgm30.language.components.core]
    [hxgm30.language.components.lang :as lang-component]
    [hxgm30.language.gen.assembled.core :as asm]
    [hxgm30.language.gen.core :as gen]
    [hxgm30.language.gen.name :as name]
    [taoensso.timbre :as log]))

(defn setup-system
  []
  (logger/set-level! '[hxgm30] :trace)
  (system-manager/setup-manager {:init 'hxgm30.language.components.core/init
                                 :throw-errors false})
  (system-manager/startup)
  (system-manager/system))

(defn -main
  [& args]
  (let [cmd (keyword (first args))
        _ (log/debug "Got command:" cmd)
        sys (setup-system)
        _ (log/debug "System, setup complete.")
        gen-type (config/lang-default-generator-mode sys)
        _ (when-not (nil? gen-type) (log/debug "Got generator type:" gen-type))
        generator (lang-component/get-generator sys gen-type)]
    (log/debug "Got generator:" generator)
    (case cmd
      :regen-markov-chains (do
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
               (name/run generator (keyword race))
               (name/run generator))
      :assemble (let [world (keyword (first args))
                      language (keyword (second args))]
                  (asm/run generator world language)))
    (println)))
