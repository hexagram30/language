(ns hxgm30.language.app.handler.util
  (:require
    [clojure.string :as string]))

(defn get-lang
  [request]
  (keyword (get-in request [:path-params :lang])))

(defn get-content-type
  [request]
  (keyword (get-in request [:params :content-type])))

(defn get-world
  [request]
  (keyword (get-in request [:path-params :world])))

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

(defn get-syllables
  [request]
  (when-let [sylls (get-in request [:params :syllables])]
    (Integer/parseInt sylls)))

(defn lang-name
  [lang]
  (cond (= :pie lang) "Proto-Indo-European"
        (= :oldenglish lang) "Old English"
        (= :oldnorse lang) "Old Norse"
        :else (string/capitalize (name lang))))
