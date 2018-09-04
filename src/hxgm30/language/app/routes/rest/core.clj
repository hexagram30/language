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
  [
    ;; Generate content based on assembled languages
    ["/api/language/gen/assembled/:world/:lang"
     {:get handler/ok}]
    ;; Generate content based on existing languages
    ["/api/language/gen/:lang"
     {:get handler/ok}]
    ;; Generate names
    ["/api/language/gen/name/:race/:name-type"
     {:get handler/ok}]
    ;; Stats operations for stored languages and names
    ["/api/language/stats/:lang"
     {:get (handler/read-lang-stats httpd-component)}]
    ["/api/language/stats/:race/:name-type"
    {:get (handler/read-name-stats httpd-component)}]
    ;; Dictionary operations
    ["/api/language/dictionary/:lang"
     {:get handler/ok}]
    ["/api/language/dictionary/:lang/search"
     {:get handler/ok}]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Assembled Routes   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn all
  [httpd-component api-version]
  (concat
    (language-api httpd-component)
    (base/admin-api httpd-component)
    base/testing))
