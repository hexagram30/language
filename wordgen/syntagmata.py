import re
import time
import random

from wordgen.types import *
from wordgen.exceptions import *

from utils import UTF8File, Counter
from elements import Vowel, Consonant, Word
from corpora.corpus import Corpus

class Syntagmata(object):
    '''
    A python class for representing the statistical relationship of consonant
    and vowel and consonant groups.

    What operations do we want to support for this?
        * obtaining statistical information from source text
        * generating a dense representation of this in a data structure
        * writing and reading this data structure
        * the ability to combine mutliple source stats
            o e.g., "I want this word to be 3 parts Russian-derived and 2 parts
              Chinese-derived"
            o this operation should be support via simply adding objects
              together - addition will be the union of the two stat data
              structures, where the frequencies (counts) are additive
            o the percentage desired for a particular lanugage would be
              indicated by integer multiplication, where the frequencies of the
              data structure(s) would be multiplied by integers
        * displaying the word parts
        * displaying the stats for a given word part

    >>> syn = Syntagmata(vowels='aeiouy', consonants='bcdfghjklmnpqrstvwxz')
    >>> syn.vowel.letters
    'aeiouy'
    >>> syn.consonant.letters
    'bcdfghjklmnpqrstvwxz'
    >>> syn.isVowel('a'), syn.getLetterType('a').name
    (True, 'VowelType')
    >>> syn.isConsonant('b'), syn.getLetterType('b').name
    (True, 'ConsonantType')
    >>> syn.isWord('polychronopolous')
    True
    >>> syn.isWord('101 Main St.')
    False

    # check for pseudo-syllables
    >>> syn.getPseudoSyllables('a')
    ['a']
    >>> syn.getPseudoSyllables('the')
    ['the']
    >>> syn.getPseudoSyllables('apple')
    ['appl', 'e']
    >>> syn.getPseudoSyllables('frenetic')
    ['fre', 'ne', 'ti', 'c']
    >>> syn.getPseudoSyllables('polychronopolous')
    ['po', 'ly', 'chro', 'no', 'po', 'lou', 's']

    # test stats
    >>> syn1 = Syntagmata(vowels='aeiouy', consonants='bcdfghjklmnpqrstvwxz')
    >>> sourceText = "Polychronopolous is a delicious meal for the polyglot in all of us."
    >>> stats = syn1.getStats(sourceText)
    >>> finals = stats[FinalType.name].items()
    >>> finals.sort()
    >>> print finals
    [('l', 1), ('r', 1), ('s', 2), ('t', 1)]

    # some panagrams from http://www.p22.com/products/pangramcontest.html
    >>> syn2 = Syntagmata(vowels='aeiouy', consonants='bcdfghjklmnpqrstvwxz')
    >>> sourceText = """
    ...  I jump quickly, shaving two dozen fox bare.
    ...  The xylophone orchestra vowed to imbibe jugs of kumquat fizz.
    ...  Knowledge of zymurgy and Bacchus justly pleases a quiet vixen.
    ...  Zealous dominatrix whips frail, quivering boy with jockstrap!
    ...  """
    >>> stats = syn2.getStats(sourceText)
    >>> finals = stats[FinalType.name].items()
    >>> finals.sort()
    >>> print finals
    [('a', 1), ('ckly', 1), ('d', 1), ('dge', 1), ('e', 1), ('gs', 1), ('l', 1), ('mp', 1), ('n', 2), ('ne', 1), ('ng', 2), ('p', 1), ('ps', 1), ('re', 1), ('rgy', 1), ('s', 3), ('stly', 1), ('t', 2), ('th', 1), ('x', 2), ('zz', 1)]

    # now test operations on the syntagmata
    >>> syn3 = syn1 + syn1
    >>> finals = syn3.stats[FinalType.name].items()
    >>> finals.sort()
    >>> print finals
    [('l', 2), ('r', 2), ('s', 4), ('t', 2)]

    >>> syn4 = syn1 * 1
    >>> finals = syn4.stats[FinalType.name].items()
    >>> finals.sort()
    >>> print finals
    [('l', 1), ('r', 1), ('s', 2), ('t', 1)]

    >>> syn5 = syn1 * 2
    >>> finals = syn5.stats[FinalType.name].items()
    >>> finals.sort()
    >>> print finals
    [('l', 2), ('r', 2), ('s', 4), ('t', 2)]

    >>> syn6 = syn1 * 3
    >>> finals = syn6.stats[FinalType.name].items()
    >>> finals.sort()
    >>> print finals
    [('l', 3), ('r', 3), ('s', 6), ('t', 3)]

    >>> syn7 = syn1 * 10
    >>> finals = syn7.stats[FinalType.name].items()
    >>> finals.sort()
    >>> print finals
    [('l', 10), ('r', 10), ('s', 20), ('t', 10)]

    >>> syn7 = syn1 * 10 + syn1 * 20 + syn1 * 3
    >>> finals = syn7.stats[FinalType.name].items()
    >>> finals.sort()
    >>> print finals
    [('l', 33), ('r', 33), ('s', 66), ('t', 33)]

    '''
    def __init__(self, langName='', vowels='', consonants=''):
        self.corpus = Corpus(langName, vowels, consonants)
        if not vowels:
            vowels = self.corpus.getVowels()
        if not consonants:
            consonants = self.corpus.getConsonants()
        self.langName = langName
        self.vowel = Vowel(vowels)
        self.consonant = Consonant(consonants)
        self.word = Word(self.vowel, self.consonant)
        self.stats = {}

    def __add__(self, syntagObj):
        vowels = self.vowel.letters + syntagObj.vowel.letters
        consonants = self.consonant.letters + syntagObj.consonant.letters
        newSyn = Syntagmata(vowels=vowels, consonants=consonants)
        newSyn.stats = {
            InitialType.name: Counter(),
            MedialType.name: Counter(),
            FinalType.name: Counter()}
        itemSum = self.getStats().items() + syntagObj.getStats().items()
        for key, data in itemSum:
            for syllable, count in data.items():
                try:
                    newSyn.stats[key][syllable] += count
                except KeyError:
                    newSyn.stats[key].setdefault(syllable, count)
        return newSyn

    def __mul__(self, integer):
        integer = int(integer)
        if integer < 1:
            msg = "You can only mulitiply by a positive integer greater than 0."
            raise ValueError, msg
        if integer == 1:
            return self
        newSyn = self + self
        for i in xrange(integer - 2):
            newSyn = newSyn + self
        return newSyn

    def isWord(self, word):
        return self.word(word)

    def isVowel(self, letter):
        return self.vowel(letter)

    def isConsonant(self, letter):
        return self.consonant(letter)

    def getLetterType(self, letter):
        if self.isVowel(letter):
            return VowelType
        elif self.isConsonant(letter):
            return ConsonantType
        raise InvalidLetter

    def getAlphabet(self):
        return self.consonant.letters + self.vowel.letters

    def makeWordList(self, sourceLines):
        wordList = []
        for line in sourceLines:
            words = line.split()
            for word in words:
                word = word.strip().lower()
                if self.isWord(word):
                    wordList.append(word.lower())
        return wordList

    def getPseudoSyllables(self, word):
        part = ''
        word = word.lower()
        thisType = initialType = lastType = None
        syllables = []
        for letter in word:
            try:
                thisType = self.getLetterType(letter)
            except InvalidLetter:
                continue
            if not part:
                initialType = thisType
            if thisType == initialType and thisType != lastType:
                syllables.append(part)
                part = ''
            lastType = thisType
            part += letter
        # add last syllable
        syllables.append(part)
        # the first one in the list is always empty
        return syllables[1:]

    def generateStats(self, sourceText=''):
        if not sourceText:
            sourceText = self.corpus.getSource()
        sourceLines = sourceText.split('\n')
        initialData = Counter()
        medialData = Counter()
        finalData = Counter()
        for word in self.makeWordList(sourceLines):
            syllables = self.getPseudoSyllables(word)
            initialData.updateCount(syllables[0])
            final = ''
            medials = []
            if len(syllables) > 1:
                finalData.updateCount(syllables[-1])
            if len(syllables) > 2:
                for medial in syllables[1:-1]:
                    medialData.updateCount(medial)
        self.stats = {
            InitialType.name: initialData,
            MedialType.name: medialData,
            FinalType.name: finalData}
        return self.getStats()

    def getStats(self, sourceText=''):
        if self.stats:
            return self.stats
        try:
            self.stats = self.corpus.getStats()
            if self.stats:
                return self.stats
        except IOError:
            pass
        return self.generateStats(sourceText)

    def makeWordPart(self, positionObj):
        stats = self.getStats()
        syllableData = stats[positionObj.name]
        summedCounts = sum(syllableData.values())
        choiceIndex = random.randint(1, summedCounts)
        # next we need to get the count ranges for each pseudo-syllable so that
        # we pick a syllable randomly but statistically accurately
        ranges = []
        lastEndpoint = 0
        for syllable, count in syllableData.items():
            thisRange = count + lastEndpoint
            # if the randomly chosen index is between the last endpoint and the
            # current endpoint, then we've got a match and will use the current
            # syllable
            if lastEndpoint < choiceIndex <= thisRange:
                break
            lastEndpoint += count
        return syllable

    def makeFinalPart(self):
        return self.makeWordPart(FinalType)

    def makeInitialPart(self):
        return self.makeWordPart(InitialType)

    def makeMedialPart(self):
        return self.makeWordPart(MedialType)

    def makeWord(self, syllableCount):
        word = self.makeInitialPart()
        for i in range(syllableCount - 2):
            word += self.makeMedialPart()
        word += self.makeFinalPart()
        return word

    def makeCVWord(self, syllableCount):
        """
        This method forces a CV (consonant-vowel) pattern that is commonly
        found in Japanese and Chinese phonology.
        """
        word = ''
        for i in range(int(syllableCount/2)):
            word += self.makeInitialPart()
            word += self.makeFinalPart()
        return word

    def writeStatsFile(self):
        stats = self.getStats()
        file = UTF8File(filename=self.corpus.statsFile, mode='w+')
        file.write(str(stats))
        file.close()

def _test():
    import doctest
    doctest.testmod()

if __name__ == "__main__":
    _test()
