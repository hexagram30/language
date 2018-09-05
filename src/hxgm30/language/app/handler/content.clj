(ns hxgm30.language.app.handler.content
  "This namespace defines the handlers for content resources.

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
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- -gen-content
  [generator lang content-type]
  (let [stats (gen/stats (:stats-gen generator) lang)]
    (case content-type
      :word (gen/word generator stats)
      :sentence (gen/sentence generator stats)
      :paragraph (gen/paragraph generator stats))))

(defn gen-one-content
  [generator lang content-type]
  [{:language (util/lang-name lang)
    :content-type content-type
    :content (-gen-content generator lang content-type)}])

(defn gen-all-content
  [generator lang]
  (let [stats (gen/stats (:stats-gen generator) lang)]
    [{:language (util/lang-name lang)
      :word (gen/word generator stats)
      :sentence (gen/sentence generator stats)
      :paragraph (gen/paragraph generator stats)}]))

(defn gen-one-content-all-langs
  [generator lang content-type]
  (->> common/supported-languages
       (mapcat #(gen-one-content generator % content-type))
       vec))

(defn gen-all-content-all-langs
  [generator]
  (->> common/supported-languages
       (mapcat #(gen-all-content generator %))
       vec))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   API Handlers   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn gen-content
  "Usage:

  * /api/language/gen/content/gaelic?content-type=word
  * /api/language/gen/content/pie?content-type=sentence
  * /api/language/gen/content/oldnorse?content-type=paragraph
  * /api/language/gen/content/oldnorse?content-type=paragraph&gen-type=syntagmata
  * /api/language/gen/content/arabic?content-type=all
  * /api/language/gen/content/all?content-type=word
  * /api/language/gen/content/all?content-type=sentence
  * /api/language/gen/content/all?content-type=paragraph
  * /api/language/gen/content/all?content-type=all"
  [component]
  (fn [request]
    (let [lang (util/get-lang request)
          content-type (util/get-content-type request)
          gen-type (util/get-gen-type request)
          generator (lang-component/get-generator component gen-type)]
      (response/json
        request
        (cond (and (not= :all lang) (not= :all content-type))
              (gen-one-content generator lang content-type)

              (and (not= :all lang) (= :all content-type))
              (gen-all-content generator lang)

              (and (= :all lang) (not= :all content-type))
              (gen-one-content-all-langs generator lang content-type)

              (and (= :all lang) (= :all content-type))
              (gen-all-content-all-langs generator))))))
