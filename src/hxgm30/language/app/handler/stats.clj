(ns hxgm30.language.app.handler.stats
  "This namespace defines the handlers for stats resources.

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
    [hxgm30.httpd.kit.response :as response]
    [hxgm30.language.app.handler.util :as util]
    [hxgm30.language.components.lang :as lang]
    [taoensso.timbre :as log]))

(defn read-lang-stats
  [component]
  (fn [request]
    (let [lang (util/get-lang request)
          gen-type (util/get-gen-type request)]
      (log/debug "Got language:" lang)
      (response/json request
                     (lang/lang-stats component gen-type lang)))))

(defn read-name-stats
  [component]
  (fn [request]
    (let [race (util/get-race request)
          name-type (util/get-name-type request)
          gen-type (util/get-gen-type request)]
      (response/json request
                     (lang/name-stats component race name-type gen-type)))))
