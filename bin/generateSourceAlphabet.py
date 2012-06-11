# -*- coding: utf-8 -*-

import re
import sys
import codecs

from wordgen.utils import UTF8File
from wordgen.syntagmata import Syntagmata

sourceType = sys.argv[1]
sourceFile = "wordgen/corpora/sources/%s.txt" % sourceType
destFile = "wordgen/corpora/alphabets/%s.txt" % sourceType
data = UTF8File(sourceFile, 'r').read()
alphabet = {}
for letter in data:
    letter = letter.strip()
    if letter and re.match("""[^0-9.\[\];\?\!(),:"'_`~-]""", letter):
        alphabet.setdefault(letter, 1)

#alphabet = u''.join(alphabet.keys()).encode('utf-8')
keys = alphabet.keys()
keys.sort()
alphabet = ''.join(keys)

if destFile:
    file = UTF8File(destFile, 'w+')
    file.write(alphabet)
    file.close()
    print "Alphabet saved to %s." % destFile
else:
    alphabet
