(ns hxgm30.language.cli
  (:require
    [clojusc.system-manager.core :as system-manager]
    [clojusc.twig :as logger]
    [hxgm30.language.components.config :as config]
    [hxgm30.language.components.core]
    [hxgm30.language.components.lang :as lang-component]
    [hxgm30.language.gen.assembled.core :as asm]
    [hxgm30.language.gen.core :as gen]
    [hxgm30.language.gen.name :as name]))

(defn setup-system
  []
  (logger/set-level! '[hxgm30] :error)
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
    (case cmd
      :regen-markov-chains (do
                             (println "Regenerating markov-chain data ...\n")
                             (gen/regen-stats
                              (lang-component/get-generator sys :markov)))
      :regen-syntagmata (do
                          (println "Regenerating syntagmata data ...\n")
                          (gen/regen-stats
                           (lang-component/get-generator sys :syntagmata)))
      :names (if-let [race (second args)]
               (name/run generator (keyword race))
               (name/run generator))
      :assemble (let [world (keyword (first args))
                      language (keyword (second args))]
                  (asm/run generator world language)))
    (println)))

