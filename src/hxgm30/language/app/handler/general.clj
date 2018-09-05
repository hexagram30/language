(ns hxgm30.language.app.handler.general
  "This namespace defines the handlers for general resources."
  (:require
    [hxgm30.httpd.kit.response :as response]
    [taoensso.timbre :as log]))

(def ok
  (fn [request]
    (response/text request "OK")))

(def not-implemented
  (fn [request]
    (response/json request {:errors ["Handler for resource not implemented."]})))
