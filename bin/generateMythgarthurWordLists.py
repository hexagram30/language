from wordgen.fictional import mythgarthur

l = mythgarthur.Orcish()
l.createWordlist(maxSyllables=5)

l = mythgarthur.Elvish()
l.createWordlist(maxSyllables=5)

l = mythgarthur.Human()
l.createWordlist(maxSyllables=5)

l = mythgarthur.Dwarvish()
l.createWordlist(maxSyllables=5)
