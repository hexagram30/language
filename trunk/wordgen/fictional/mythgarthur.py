from wordgen.languages import *

Orcish = Composite()
Orcish.addLanguage(Arabic, 9)
Orcish.addLanguage(Afrikaner, 8)
Orcish.addLanguage(Chinese, 5)
Orcish.addLanguage(German, 4)
Orcish.addLanguage(OldNorse)

Elvish = Composite()
Elvish.addLanguage(OldNorse, 5)
Elvish.addLanguage(OldEnglish, 2)
Elvish.addLanguage(Gaelic)

Human = Composite()
Human.addLanguage(Sanskrit, 6)
Human.addLanguage(English, 6)
Human.addLanguage(OldNorse, 2)
Elvish.addLanguage(OldEnglish)

Dwarvish = Composite()
Dwarvish.addLanguage(Gaelic, 5)
Dwarvish.addLanguage(Hebrew, 5)
Dwarvish.addLanguage(German)
