(ns hxgm30.language.app.routes.rest.core
  (:require
    [hxgm30.httpd.app.routes.rest.core :as base]
    [hxgm30.httpd.app.handler.core :as base-handler]
    [hxgm30.language.app.handler.core :as handler]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   REST API Routes   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn language-api
  [httpd-component]
  ["/api/language"
    ;; Generate content based on assembled languages
    ["/gen/assembled/:world/:lang" {:get base-handler/ok}]
    ["/gen/assembled/:world/:lang/markov" {:get base-handler/ok}]
    ["/gen/assembled/:world/:lang/syntagmata" {:get base-handler/ok}]
    ;; Generate content based on existing languages
    ["/gen/:lang" {:get base-handler/ok}]
    ["/gen/:lang/markov" {:get base-handler/ok}]
    ["/gen/:lang/syntagmata" {:get base-handler/ok}]
    ;; Generate names
    ["/gen/name/:race/:name-type " {:get base-handler/ok}]
    ["/gen/name/:race/:name-type/markov" {:get base-handler/ok}]
    ["/gen/name/:race/:name-type/syntagmata" {:get base-handler/ok}]
    ;; Stats operations for stored languages and names
    ["/stats/:lang/:name-type/markov" {:get base-handler/ok}]
    ["/stats/:lang/:name-type/syntagmata" {:get base-handler/ok}]
    ;; Dictionary operations
    ["/dictionary/:lang" {:get base-handler/ok}]
    ["/dictionary/:lang/search" {:get base-handler/ok}]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Assembled Routes   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn all
  [httpd-component api-version]
  (concat
    (language-api httpd-component)
    (base/admin-api httpd-component)
    base/testing))
