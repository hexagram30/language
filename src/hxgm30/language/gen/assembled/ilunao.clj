(ns hxgm30.language.gen.assembled.ilunao
  "To use the languages defined here, you can do the following (see the `clj`
  namespace or the `repl` namespace in `dev-system` for how to create a system):
  ```
    (require '[hxgm30.language.gen.core :as gen]
             '[hxgm30.language.gen.assembled.core :as lang]
             '[hxgm30.language.gen.assembled.ilunao :as ilunao])
    (lang/paragraph (gen/create-content-generator system) ilunao/othtunwwa)
  ```
  Which will give output along the lines of the following:
  ```
    Whizz ji reira nou pebodana suran yabisebt. Dan
    aravan curassouronan mume potikertarp gun kerr
    aruppitten dep bem. Lu whoom ido ong sluckoo. Lue
    oya nyaazz hesh kedirayo. Pum settewaxth lias.
    Phes gegigupe whistramew losomepe irstendeng
    gesiweck wegetiop boo bur.
  ```")

(def supported-languages
  [;; Northern Tribes
   :nwanitui
   :othitwa
   :othtunwwa
   :zatunun
   :othne
   :wituwi
   :wothniwi
   :unpoiwe
   ;; Land-bridge Culture
   ;; Southern Plateau Nomads
   ;; Large Island Culture
   ;; Central Mountain Culture
   ;; Southern Hemisphere Coastal Cultures
   ])

;; n'Wanitui (Tundra)
(def nwanitui
  {:lakota 50
   :inuit 30
   :finnish 15
   :dwarvish 5})

(def tundra nwanitui)

;; Óðitwa (Boreal Forests)
(def othitwa
  {:lakota 50
   :quenya 5
   :oldnorse 45})

(def boreal-forests othitwa)

;; Óðtunwwa (Plains)
(def othtunwwa
  {:lakota 50
   :mongolian 40
   :oldnorse 10})

(def plains othtunwwa)

;; Zâtunun (Hills)
(def zatunun
  {:lakota 50
   :tibetan 40
   :dwarvish 10})

(def hills zatunun)

;; Óðne (Western Coast)
(def othne
  {:lakota 50
   :gaelic 25
   :oldnorse 25})

(def western-coast othne)

;; Wituwi (Southern Coast)
(def wituwi
  {:lakota 50
   :maori 45
   :oldnorse 5})

(def southern-coast wituwi)

;; Wóðniwi (Eastern Bay)
(def wothniwi
  {:lakota 50
   :maori 40
   :dwarvish 5
   :oldnorse 5})

(def eastern-bay wothniwi)

;; Unpoiwʻe (Southern Islands)
(def unpoiwe
  {:lakota 50
   :maori 20
   :hawaiian 25
   :oldnorse 2.5
   :dwarvish 2.5})

(def southern-islands unpoiwe)
