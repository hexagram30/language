#!/usr/bin/python

from nltk.tokenizer import WhitespaceTokenizer, Token
from nltk.probability import FreqDist, ConditionalFreqDist

filename = 'examples/rookish_source/english_wordlist.txt'

VOWELS = ('a', 'e', 'i', 'o', 'u')

corpus = Token(TEXT=open(filename).read())
WhitespaceTokenizer().tokenize(corpus)

freq_dist = FreqDist()
for token in corpus['SUBTOKENS']:
    freq_dist.inc(len(token['TEXT']))

print "Letter-count frequencies:\n"
print [ '%s : %.2f' % (x, freq_dist.freq(x)*100) for x in freq_dist.sorted_samples() ]

def get_type(letter):
    if letter in VOWELS: return 'vowel'
    else: return 'consonant'

# get the distribution for vowel or consonant at each letter position
# in a word
cfdist = ConditionalFreqDist()
for token in corpus['SUBTOKENS']:
    word = token['TEXT']
    for letter_index in range(0,len(word)):
        outcome = get_type(word[letter_index])
        condition = letter_index
        cfdist[condition].inc(outcome)

# get the distribution for any given letter at each position in a word
cfdist = ConditionalFreqDist()
for token in corpus['SUBTOKENS']:
    word = token['TEXT']
    for letter_index in range(0,len(word)):
        outcome = word[letter_index]
        condition = letter_index
        cfdist[condition].inc(outcome)
data = []
mathematica_data = []
def get_samples(cfdist, word_length, letter):
    try: return cfdist[word_length].freq(letter)
    except: return 0

mathematica_data = []
for word_length in cfdist.conditions():
    #for letter in 'abcdefghijklnopqrstuvwxyz':
        #mathematica_data.append([word_length, ord(letter), int("%d" % (10000*get_samples(cfdist, word_length, letter)))])
        #mathematica_data.append([int("%d" % (10000*get_samples(cfdist, word_length, letter)))])
    mathematica_data.append([ int("%d" % (10000*get_samples(cfdist, word_length, letter))) for letter in 'abcdefghijklnopqrstuvwxyz' ])

print data
print mathematica_data
mathematica_data = []
for word_length in cfdist.conditions():
    mathematica_data.append([ 100*get_samples(cfdist, word_length, letter) for letter in 'abcdefghijklnopqrstuvwxyz' ])

mathematica_data = []
for letter in 'abcdefghijklnopqrstuvwxyz':
    mathematica_data.append([ 100*get_samples(cfdist, word_length, letter) for word_length in cfdist.conditions() ])

md = str(mathematica_data)
md = md.replace("[", "{")
md = md.replace("]", "}")
print md

