(defn get-banner
  []
  (try
    (str
      (slurp "resources/text/banner.txt")
      ;(slurp "resources/text/loading.txt")
      )
    ;; If another project can't find the banner, just skip it;
    ;; this function is really only meant to be used by Dragon itself.
    (catch Exception _ "")))

(defn get-prompt
  [ns]
  (str "\u001B[35m[\u001B[34m"
       ns
       "\u001B[35m]\u001B[33m λ\u001B[m=> "))

(defproject hexagram30/language "4.1.0-SNAPSHOT"
  :description "A language and word generator for use in hexagram30 narratives"
  :url "https://github.com/hexagram30/language"
  :license {
    :name "Apache License, Version 2.0"
    :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :exclusions [
    [io.aviso/pretty]
    [org.clojure/tools.reader]]
  :dependencies [
    [clojusc/opennlp "0.4.1-SNAPSHOT"]
    [clojusc/system-manager "0.3.0-SNAPSHOT"]
    [clojusc/twig "0.3.3"]
    [hexagram30/common "0.1.0-SNAPSHOT"]
    [hexagram30/dice "0.1.0-SNAPSHOT"]
    [io.aviso/pretty "0.1.34"]
    [org.clojure/clojure "1.9.0"]
    [org.clojure/tools.reader "1.3.0"]]
  :profiles {
    :ubercompile {
      :aot :all}
    :dev {
      :dependencies [
        [clojusc/trifl "0.3.0"]
        [org.clojure/tools.namespace "0.2.11"]]
      :plugins [
        [lein-shell "0.5.0"]
        [venantius/ultra "0.5.2"]]
      :source-paths ["dev-resources/src"]
      :repl-options {
        :init-ns hxgm30.language.repl
        :prompt ~get-prompt
        :init ~(println (get-banner))}}
    :lint {
      :source-paths ^:replace ["src"]
      :test-paths ^:replace []
      :plugins [
        [jonase/eastwood "0.2.9"]
        [lein-ancient "0.6.15"]
        [lein-kibit "0.1.6"]]}
    :test {
      :plugins [
        [lein-ltest "0.3.0"]]
      :test-selectors {
        :unit #(not (or (:integration %) (:system %)))
        :integration :integration
        :system :system
        :default (complement :system)}}}
  :aliases {
    ;; Dev Aliases
    "repl" ["do"
      ["clean"]
      ["repl"]]
    "ubercompile" ["do"
      ["clean"]
      ["with-profile" "+ubercompile" "compile"]]
    "check-vers" ["with-profile" "+lint" "ancient" "check" ":all"]
    "check-jars" ["with-profile" "+lint" "do"
      ["deps" ":tree"]
      ["deps" ":plugin-tree"]]
    "check-deps" ["do"
      ["check-jars"]
      ["check-vers"]]
    "kibit" ["with-profile" "+lint" "kibit"]
    "eastwood" ["with-profile" "+lint" "eastwood" "{:namespaces [:source-paths]}"]
    ; "eastwood" ["with-profile" "+lint" "eastwood" "{:namespaces [hxgm30.language.syntagmata.core hxgm30.language.syntagmata.corpus]}"]
    "lint" ["do"
      ["kibit"]
      ;["eastwood"]
      ]
    "ltest" ["with-profile" "+test" "ltest"]
    "ltest-clean" ["do"
      ["clean"]
      ["ltest"]]
    "build" ["do"
      ["clean"]
      ["check-vers"]
      ["lint"]
      ["ltest" ":all"]
      ["uberjar"]]
    ;; Scripts
    "fictional" ["run" "-m" "hxgm30.language.syntagmata.lang.merge"]
    "name" ["run" "-m" "hxgm30.language.syntagmata.lang.names"]
    "names" ["run" "-m" "hxgm30.language.syntagmata.lang.names"]
    "regen-syntagmata" ["run" "-m" "hxgm30.language.syntagmata.core" "regen-syntagmata"]})

