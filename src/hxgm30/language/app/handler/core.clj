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
    [ring.middleware.file :as file-middleware]
    [ring.util.codec :as codec]
    [ring.util.http-response]
    [ring.util.response :as ring-response]
    [taoensso.timbre :as log]))

;; TBD
