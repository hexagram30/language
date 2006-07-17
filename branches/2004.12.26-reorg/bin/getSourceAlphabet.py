import re
import sys
import codecs

sourceFile = sys.argv[1]
try:
    destFile = sys.argv[2]
except IndexError:
    destFile = None

data = codecs.open(sourceFile, 'r', 'utf-8').read()
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
    fh = codecs.open(destFile, 'w+', 'utf-8')
    fh.write(alphabet)
    fh.close()
    print "Alphabet saved to %s." % destFile
else:
    alphabet
