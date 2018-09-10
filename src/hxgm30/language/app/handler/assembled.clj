(ns hxgm30.language.app.handler.assembled
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
    [cheshire.core :as json]
    [hxgm30.httpd.kit.response :as response]
    [hxgm30.language.app.handler.util :as util]
    [hxgm30.language.gen.assembled.core :as asm]
    [hxgm30.language.gen.assembled.mythgarthur]
    [hxgm30.language.gen.assembled.rook]
    [hxgm30.language.common :as common]
    [hxgm30.language.components.lang :as lang-component]
    [hxgm30.language.gen.core :as gen]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Constants   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def base-ns "hxgm30.language.gen.assembled")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- -get-var
  [string]
  (log/tracef "Resolving %s ..." string)
  (-> string
      symbol
      resolve
      var-get))

(defn- -lang-freqs
  [world lang]
  (-get-var (format "%s.%s/%s" base-ns (name world) (name lang))))

(defn- -supported-langs
  [world]
  (-get-var (format "%s.%s/supported-languages" base-ns (name world))))

(defn- -gen-content
  [generator lang-freqs content-type]
  (case content-type
    :word (asm/word generator lang-freqs)
    :sentence (asm/sentence generator lang-freqs)
    :paragraph (asm/paragraph generator lang-freqs)))

(defn gen-one-content
  [generator [lang lang-freqs] content-type]
  [{:language (util/lang-name lang)
    :content-type content-type
    :content (-gen-content generator lang-freqs content-type)}])

(defn gen-all-content
  [generator [lang lang-freqs]]
  [{:language (util/lang-name lang)
    :word (asm/word generator lang-freqs)
    :sentence (asm/sentence generator lang-freqs)
    :paragraph (asm/paragraph generator lang-freqs)}])

(defn gen-one-content-all-langs
  [generator world content-type]
  (let [langs (-supported-langs world)]
    (->> langs
         (map #(-lang-freqs world %))
         (zipmap langs)
         (mapcat #(gen-one-content generator % content-type))
         vec)))

(defn gen-all-content-all-langs
  [generator world]
  (let [langs (-supported-langs world)]
    (->> langs
         (map #(-lang-freqs world %))
         (zipmap langs)
         (mapcat #(gen-all-content generator %))
         vec)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   API Handlers   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn gen-assembled-worlds
  [component request]
  (let [world (util/get-world request)
        _ (log/warn "world:" world)
        lang (util/get-lang request)
        _ (log/warn "lang:" lang)
        content-type (util/get-content-type request)
        generator (lang-component/get-generator component :syntagmata)]
    (log/warn "content-type:" content-type)
    (log/warn "generator:" generator)
    (response/json
      request
      (cond (and (not= :all lang) (not= :all content-type))
            (gen-one-content generator
                            [lang (-lang-freqs world lang)]
                            content-type)

            (and (not= :all lang) (= :all content-type))
            (gen-all-content generator
                             [lang (-lang-freqs world lang)])

            (and (= :all lang) (not= :all content-type))
            (gen-one-content-all-langs generator world content-type)

            (and (= :all lang) (= :all content-type))
            (gen-all-content-all-langs generator world)))))

(defn gen-assembled-user-provided
  [component request]
  (let [lang :user-supplied
        body (json/parse-string (slurp (:body request)) true)
        freqs (:language-frequencies body)
        content-type (keyword (:content-type body))
        generator (lang-component/get-generator component :syntagmata)]
    (log/error "body:" body)
    (log/error "freqs:" freqs)
    (log/error "content-type:" content-type)
    (response/json
      request
      (cond (and (not= :all lang) (not= :all content-type))
            (gen-one-content generator
                            [lang freqs]
                            content-type)

            (and (not= :all lang) (= :all content-type))
            (gen-all-content generator
                             [lang freqs])))))

(defn gen-assembled
  "GET usage:

  * /api/language/gen/assembled/mythgarthur/dwarvish?content-type=work
  * /api/language/gen/assembled/mythgarthur/elvish?content-type=sentence
  * /api/language/gen/assembled/rook/mux?content-type=paragraph
  * /api/language/gen/assembled/rook/all?content-type=all
  * /api/language/gen/assembled/all?content-type=word
  * /api/language/gen/assembled/all?content-type=sentence
  * /api/language/gen/assembled/all?content-type=paragraph
  * /api/language/gen/assembled/all?content-type=all

  PUT usage:

   * /api/language/gen/assembled

  with a JSON payload such as:

  ```
    {\"language-frequencies\": {\"gaelic\": 4, \"oldnorse\": 3},
     \"content-type\": \"all\"}
  ```

  Note that with `PUT` usage, setting the `Content-Type` header to
  `application/json` is required."
  [component]
  (fn [request]
    (case (:request-method request)
      :get (gen-assembled-worlds component request)
      :put (gen-assembled-user-provided component request))))
