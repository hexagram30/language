(ns hxgm30.language.cli
  (:require
    [clojusc.system-manager.core :as system-manager]
    [clojusc.twig :as logger]
    [hxgm30.language.components.core]))

(defn setup-system
  []
  (logger/set-level! '[hxgm30] :error)
  (system-manager/setup-manager {:init 'hxgm30.language.components.core/cli
                                 :throw-errors true})
  (system-manager/startup)
  (system-manager/system))
