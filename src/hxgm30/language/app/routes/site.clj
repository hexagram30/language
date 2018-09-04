(ns hxgm30.language.app.routes.site
  "This namespace defines the regular content routes provided by this service.

  Upon idnetifying a particular request as matching a given route, work is then
  handed off to the relevant request handler function."
  (:require
    [hxgm30.httpd.app.routes.site :as base-site]
    [hxgm30.httpd.app.handler.core :as core-handler]
    [hxgm30.httpd.components.config :as config]
    [hxgm30.httpd.site.pages :as pages]
    [hxgm30.language.app.routes.rest.core :as rest]
    [reitit.ring :as ring]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Main Routes   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; TBD

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Static & Redirect Routes   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; TBD

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Assembled Routes   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn all
  [httpd-component]
  ;; (concat
   (base-site/all httpd-component)
   ;; (main httpd-component)
   ;; (redirects httpd-component)
   ;; (static httpd-component))
  )
