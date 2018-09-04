(ns hxgm30.language.app.handler.core
  "This namespace defines the handlers for general resources.

  Simple handlers will only need to make a call to a library and then have that
  data prepared for the client by standard response function. More complex
  handlers will need to perform additional tasks. For example, in order of
  increasing complexity:
  * utilize non-default, non-trivial response functions
  * operate on the obtained data with various transformations, including
    extracting form data, query strings, etc.
  * take advantage of middleware functions that encapsulate complicated
    business logic"
  (:require
    [clojure.java.io :as io]
    [clojusc.twig :as twig]
    [hxgm30.httpd.kit.response :as response]
    [hxgm30.language.gen.corpus :as corpus]
    [ring.middleware.file :as file-middleware]
    [ring.util.codec :as codec]
    [ring.util.http-response]
    [ring.util.response :as ring-response]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-lang
  [request]
  (keyword (get-in request [:path-params :lang])))

(defn get-race
  [request]
  (keyword (get-in request [:path-params :race])))

(defn get-name-type
  [request]
  (keyword (get-in request [:path-params :name-type])))

(defn get-gen-type
  [request]
  (or (keyword (get-in request [:params :gen-type]))
      :markov))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Generic Handlers   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ok
  (fn [request]
    (response/text request "OK")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   API Handlers   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn read-lang-stats
  [component]
  (fn [request]
    (let [lang (get-lang request)
          gen-type (get-gen-type request)]
      (log/debug "Got language: " lang)
      (response/json request
                     (corpus/undump gen-type lang)))))

(defn read-name-stats
  [component]
  (fn [request]
    (let [race (get-race request)
          name-type (get-name-type request)
          gen-type (get-gen-type request)]
      (log/debug "Got race: " race)
      (log/debug "Got name type: " name-type)
      (response/json request
                     (corpus/undump race name-type gen-type)))))
