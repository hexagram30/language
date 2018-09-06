(ns hxgm30.language.app.routes.rest.core
  (:require
    [hxgm30.httpd.app.routes.rest.core :as base]
    [hxgm30.httpd.app.handler.core :as base-handler]
    [hxgm30.language.app.handler.assembled :as assembled-handler]
    [hxgm30.language.app.handler.content :as content-handler]
    [hxgm30.language.app.handler.general :as general-handler]
    [hxgm30.language.app.handler.names :as names-handler]
    [hxgm30.language.app.handler.stats :as stats-handler]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   REST API Routes   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn language-api
  [httpd-component]
  [;; Generate content based on assembled languages
   ["/api/language/gen/assembled"
    {:put (assembled-handler/gen-assembled httpd-component)}]
   ["/api/language/gen/assembled/:world/:lang"
    {:get (assembled-handler/gen-assembled httpd-component)}]
   ;; Generate content based on existing languages
   ["/api/language/gen/content/:lang"
    {:get (content-handler/gen-content httpd-component)}]
   ;; Generate names
   ["/api/language/gen/name/:race/:name-type"
    {:get (names-handler/gen-name httpd-component)}]
   ;; Stats operations for stored languages and names
   ["/api/language/stats/:lang"
    {:get (stats-handler/read-lang-stats httpd-component)
     ;; XXX add :post support for component boot-strapping
     ;; XXX add :put support for component updates
     }]
   ["/api/language/stats/:race/:name-type"
    {:get (stats-handler/read-name-stats httpd-component)
    ;; XXX add :post support for component boot-strapping
    ;; XXX add :put support for component updates
    }]
   ;; Dictionary operations
   ["/api/language/dictionary/:lang"
    {:get general-handler/not-implemented
    ;; XXX add :post support for new entries
    ;; XXX add :put support for updating entries
    }]
   ["/api/language/dictionary/:lang/search"
    {:get general-handler/not-implemented}]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Assembled Routes   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn all
  [httpd-component api-version]
  (concat
    (language-api httpd-component)
    (base/admin-api httpd-component)
    base/testing))
