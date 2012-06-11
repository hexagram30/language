from wordgen.languages import *

Rookish = Composite('rookish')
Rookish.addLanguage(English, 3)
Rookish.addLanguage(Hindi)
Rookish.addLanguage(Japanese, 2)
Rookish.addLanguage(Spanish, 2)

Elani = Composite('elani')
Elani.addLanguage(Arabic)
Elani.addLanguage(French, 3)
Elani.addLanguage(German, 2)
Elani.addLanguage(Korean)

Jas = Composite('jas')
Jas.addLanguage(Chinese, 4)
Jas.addLanguage(Afrikaner, 1)
Jas.addLanguage(Russian, 2)

Mux = Composite('mux')
Mux.addLanguage(Onomatopoetic, 20)
Mux.addLanguage(Rookish, 2)
Mux.addLanguage(Elani)
Mux.addLanguage(Jas)
