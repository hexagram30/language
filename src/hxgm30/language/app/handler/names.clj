(ns hxgm30.language.app.handler.names
  "This namespace defines the handlers for names resources.

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
    [hxgm30.language.common :as common]
    [hxgm30.language.components.lang :as lang-component]
    [hxgm30.language.gen.core :as gen]
    [hxgm30.language.gen.name :as name]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- -gen-name
  [generator race name-type sylls]
  (if-not (nil? sylls)
    (name/gen-name generator race name-type sylls)
    (name/gen-name generator race name-type)))

(defn gen-one-name
  [generator race name-type sylls]
  [{:race race
    :name-type name-type
    :name (-gen-name generator race name-type sylls)}])

(defn gen-all-names
  [generator race sylls]
  [{:race race
    :name {:female (-gen-name generator race :female sylls)
           :male (-gen-name generator race :male sylls)
           :surname (-gen-name generator race :surname sylls)}}])

(defn gen-one-name-all-races
  [generator name-type sylls]
  (->> common/supported-names
       (mapcat #(gen-one-name generator % name-type sylls))
       vec))

(defn gen-all-names-all-races
  [generator sylls]
  (->> common/supported-names
       (mapcat #(gen-all-names generator % sylls))
       vec))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   API Handlers   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn gen-name
  "Usage:

  * /api/language/gen/name/halfling/surname
  * /api/language/gen/name/dwarf/all?gen-type=syntagmata
  * /api/language/gen/name/all/female?gen-type=syntagmata&syllables=3
  * /api/language/gen/name/all/all?gen-type=markov

  The generator type `markov` is the default, so in the last example you could
  pass the query without the parameter."
  [component]
  (fn [request]
    (let [race (util/get-race request)
          name-type (util/get-name-type request)
          gen-type (util/get-gen-type request)
          sylls (util/get-syllables request)
          generator (lang-component/get-generator component gen-type)]
      (response/json
        request
        (cond (and (not= :all race) (not= :all name-type))
              (gen-one-name generator race name-type sylls)

              (and (not= :all race) (= :all name-type))
              (gen-all-names generator race sylls)

              (and (= :all race) (not= :all name-type))
              (gen-one-name-all-races generator name-type sylls)

              (and (= :all race) (= :all name-type))
              (gen-all-names-all-races generator sylls))))))
