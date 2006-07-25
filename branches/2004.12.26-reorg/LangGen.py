import re
import time
import random
import codecs

basePath = './corpora'
vowelFileTmpl = basePath + "/vowels/%s.txt"
consonantFileTmpl = basePath + "/consonants/%s.txt"
statsFileTmpl = basePath + "/stats/%s.txt"
sourceFileTmpl = basePath + "/sources/%s.txt"

class UnexpectedParameter(Exception):
    '''
    The parameter passed is not expected.
    '''

class InvalidLetter(Exception):
    '''
    The charater is neither a consonant nor a vowel in the given alphabet.
    '''

class FileNotFound(Exception):
    '''
    No such number, no such home.
    '''

class BaseType(object):
    '''
    A base type for several types of object that are tracked in this module.
    '''
    def __init__(self):
        self.name = self.__class__.__name__

class IllegalType(BaseType):
    '''
    A character which does not belong to the alphabet being used.
    '''

class LetterType(BaseType):
    '''
    Just letters.
    '''

class VowelType(LetterType):
    '''
    A type for vowels.
    '''

class ConsonantType(LetterType):
    '''
    A type for consonants.
    '''

class InitialType(BaseType):
    '''
    A marker for initial pseudo-syllables.
    '''

class MedialType(BaseType):
    '''
    A marker for medial pseudo-syllables.
    '''

class FinalType(BaseType):
    '''
    A marker for final pseudo-syllables.
    '''

IllegalType = IllegalType()

LetterType = LetterType()
VowelType = VowelType()
ConsonantType = ConsonantType()

InitialType = InitialType()
MedialType = MedialType()
FinalType = FinalType()

class Counter(dict):

    def updateCount(self, key):
        try:
            self[key] += 1
        except KeyError:
            self.setdefault(key, 1)

class Letter(object):
    '''
    >>> letter = Letter('abc')
    >>> letter('a')
    True
    >>> letter('b')
    True
    >>> letter('c')
    True
    >>> letter('x')
    False
    '''
    def __init__(self, letters):
        self.letters = letters
        self.pattern = "[%s]+" % letters
        self.regex = re.compile(self.pattern)
        self.match = None

    def __call__(self, letter):
        self.match = self.regex.search(letter)
        if self.match:
            return True
        return False

class Vowel(Letter):
    pass

class Consonant(Letter):
    pass

class Word(object):
    '''
    >>> word = Word('abc123')
    >>> word('a')
    True
    >>> word('abc')
    True
    >>> word('1ab3')
    True
    >>> word('1ab3d')
    False
    >>> word('1ab 3d')
    False
    >>> word('izqrt')
    False

    # Now let's try it with letter objects
    >>> v = Vowel('aeiouy')
    >>> c = Consonant('bcdfghjklmnpqrstvwxz')
    >>> word = Word(v,c)
    >>> word('apple')
    True
    >>> word('123')
    False
    '''

    def __init__(self, *letters):
        if len(letters) == 1:
            letters = letters[0]
        elif len(letters) > 1:
            letters = ''.join([ x.letters for x in letters 
                if isinstance(x, Letter) ])
        else:
            raise UnexpectedParameter
        self.pattern = "^[%s]+$" % letters
        self.regex = re.compile(self.pattern)
        self.match = None

    def __call__(self, word):
        stripEndings = """;:.?!-'","""
        for ending in stripEndings:
            word = word.strip(ending)
        self.match = self.regex.search(word)
        if self.match:
            return True
        return False
        
class UTF8File(object):
    
    def __init__(self, filename=None, mode=None):
        self.filename = filename
        if filename:
            self.open(filename, mode)

    def open(self, filename, mode):
        if filename and mode:
            self.fh = codecs.open(filename, mode, 'utf-8')
            return self
        raise FileNotFound
    
    def write(self, data):
        self.fh.write(data)
    
    def read(self):
        return self.fh.read()

    def readlines(self):
        return self.fh.readlines()

    def close(self):
        self.fh.close()
        
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
        if not vowels:
            vowels = UTF8File(vowelFileTmpl % langName)
        if not consonants:
            consonants = UTF8File(consonantFileTmpl % langName)
        self.langName = langName
        self.vowel = Vowel(vowels)
        self.consonant = Consonant(consonants)
        self.word = Word(self.vowel, self.consonant)
        self.stats = {}
        self.totals = Counter()

    def __add__(self, syntagObj):
        vowels = self.vowel.letters + syntagObj.vowel.letters
        consonants = self.consonant.letters + syntagObj.consonant.letters
        newSyn = Syntagmata(vowels=vowels, consonants=consonants)
        newSyn.stats = {
            InitialType.name: Counter(),
            MedialType.name: Counter(),
            FinalType.name: Counter()}
        itemSum = self.stats.items() + syntagObj.stats.items()
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

    def getStats(self, sourceText=''):
        if self.stats:
            return self.stats
        if sourceText:
            return self.generateStats(sourceText)
        file = UTF8File(statsFileTmpl % self.langName)
        self.stats = eval(file.read())
        file.close()
        return self.stats

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
            self.totals.updateCount(LetterType.name)
            try:
                thisType = self.getLetterType(letter)
            except InvalidLetter:
                self.totals.updateCount(IllegalType.name)
                continue
            if not part:
                self.totals.updateCount(InitialType.name)
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
            file = UTF8File(sourceFileTmpl % self.langName)
            sourceText = file.read()
            file.close()
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
                self.totals.updateCount(FinalType.name)
            if len(syllables) > 2:
                for medial in syllables[1:-1]:
                    medialData.updateCount(medial)
                    self.totals.updateCount(MedialType.name)
        self.stats = {
            InitialType.name: initialData,
            MedialType.name: medialData,
            FinalType.name: finalData}
        return self.getStats()

    def makeWordPart(self, positionObj):
        syllableData = self.stats[positionObj.name]
        syllableData
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

    def makeChineseWord(self, syllableCount):
        word = ''
        for i in range(int(syllableCount/2)):
            word += self.makeInitialPart()
            word += self.makeFinalPart()
        return word
        
class StatsMaker(object):
    '''

    '''

    def __init__(self, vowels=None, consonants=None):
        if not vowels:
            vowels = 'aeiouy'
        if not consonants:
            consonants = 'bcdfghjklmnpqrstvwxz'
        self.vowel = Vowel(vowels)
        self.consonant = Consonant(consonants)
        self.word = Word(self.vowel, self.consonant)

        self.source_filename = ''
        self.legal_filename = ''
        self.list_filname = ''
        self.stats_filname = ''
        self.freqs = {}
        self.totals = {}
        self.wordlist = []
        self.illegals = {}

        self.stats = {}

    def isWord(self, word):
        return self.word(word)

    def isVowel(self, letter):
        return self.vowel(letter)

    def isConsonant(self, letter):
        return self.consonant(letter)

    def getWordList(self):
        '''

        '''
        if self.wordlist:
            return self.wordlist
        file = UTF8File(self.source_filename, 'r')
        for line in file.readlines():
            words = line.split()
            for word in words:
                word = word.strip()
                if len(word) > 2:
                    if self.isWord(word):
                        self.wordlist.append(word.lower())
        file.close()
        return self.wordlist

    def writeWordList(self):
        '''
        
        '''
        file = UTF8File(self.list_filname, 'w+')
        self.wordlist = self.getWordList()
        for word in self.wordlist:
            file.write(word + "\n")
        file.close()

    def getLegalVowels(self):
        return self.vowel.letters

    def getLegalConsonants(self):
        return self.consonant.letters

    def sortStats(self, data):
        '''

        '''
        data = map(None, data.values(), data.keys())
        data.sort()
        data.reverse()    
        return data

    def dictCount(self, data, key):
        '''
        
        '''
        try: data[key] += 1
        except: data[key] = 1
        return data

    def getLetterType(self, letter):
        '''

        '''
        if self.isConsonant(letter): 
              type = "con"
              self.totals = self.dictCount(self.totals, 'consonants')
              self.totals = self.dictCount(self.totals, 'letters')
        elif self.isVowel(letter): 
              type = "vow"
              self.totals = self.dictCount(self.totals, 'vowels')
              self.totals = self.dictCount(self.totals, 'letters')
        else: 
              type = "ill"
              self.totals = self.dictCount(self.totals, 'illegals')
              self.illegals = self.dictCount(self.illegals, letter)
        return type

    def getWordParts(self, word):
        '''

        '''
        #print word
        part = ''
        beg_check = 1
        output = []
        for i in range(len(word)):
            letter = word[i]
            part = part + letter
            try: 
                next = self.getLetterType(word[i+1])
                if i == 0 or beg_check: position = "beg"
                else: position = "med"
            except: 
                next = None
                position = "end"
            type = self.getLetterType(letter)
            if type != next:
                beg_check = 0
                #print "index: %s, letter: %i, type: %s, next: %s, part: %s, position: %s" % (word[i], i, type, next, part, position)
                output.append((position, part))
                part = ''
        return output
    
    def getWordStats(self):
        '''

        '''
        self.wordlist = self.getWordList()

        parts = []
        beginnings = {}
        medials = {}
        endings = {}
        for word in self.wordlist:
            parts = self.getWordParts(word)
            #print parts
            for part in parts:
                if part[0] == "beg":
                    beginnings = self.dictCount(beginnings, part[1])
                    self.totals = self.dictCount(self.totals, 'beginnings')
                elif part[0] == "med":
                    medials = self.dictCount(medials, part[1])
                    self.totals = self.dictCount(self.totals, 'medials')
                elif part[0] == "end":
                    endings = self.dictCount(endings, part[1])
                    self.totals = self.dictCount(self.totals, 'endings')

        beginnings = self.sortStats(beginnings)
        endings = self.sortStats(endings)
        medials = self.sortStats(medials)
        self.stats = {
            'beginnings':beginnings, 
            'medials': medials, 
            'endings': endings}
        return self.stats

    def statTotals(self):
        '''

        '''
        totals = 0
        self.stats = self.getWordStats()
        for type in self.stats.keys():
            #print type
            for tuple in self.stats[type]:
                #print tuple
                if tuple[1] != '':
                    totals += tuple[0]
        return totals

    def writeStatsFile(self):
        '''

        '''
        output = ''
        percent = 0
        stats = {}
        file = UTF8File(self.stats_filename, 'w+')
        self.stats = self.getWordStats()
        for type in self.stats.keys():
            counter = 1
            for (part_count, word_part) in self.stats[type]:
                if word_part != '':
                    ### pickle method
                    #stats[type] = {'range': (counter, counter + part_count), 'word_part': word_part}
                    stats.setdefault(type,[]).append((counter, counter + part_count, word_part))

                    ### old method
                    # print part_count, word_part
                    # percent = float(part_count)/self.totals[type]
                    # output = "'%s'\t'%s'\t%i\t%f\t(%i, %i)\n" % (type, word_part, part_count, percent, counter, counter + part_count)
                    # file.write(output)
                    counter += part_count
        #print stats
        file.write(str(stats))
        file.close()

class CreateWord(object):
    '''

    '''
 
    def __init__(self):
        self.stats_filename = ''
        self.stats = {}
        self.max = {}
        self.last_type = ''
        self.letter = ''
  
    def openPickledData(self):
        '''

        ''' 
        if self.stats: return 1
        file = UTF8File(self.stats_filename, 'r')
        # the stats data structure is saved as a string in a file
        self.stats = eval(file.read())
        # we want the maximum values for each of the letter placement types so
        # that we can determine the range wherein they lie -- we'll use this
        # information when generating a random number
        self.max['beginnings'] = self.stats['beginnings'][-1][1]
        self.max['medials'] = self.stats['medials'][-1][1]
        self.max['endings'] = self.stats['endings'][-1][1]

    def _getWordPart(self, type):
        '''
        
        '''
        letter = ''
        seed = random.Random(time.time())
        rand = int(seed.uniform(1, self.max[type]))
        for (start, end, letter) in self.stats[type]:
            if rand >= start and rand <= end:
                break

        check = StatsMaker()
        this_type = check.getLetterType(letter[0])

        if this_type != self.last_type: 
            self.last_type = this_type
            self.letter = letter
        else:
            self._getWordPart(type)
        return self.letter  

    def getEndPart(self):
        '''

        '''
        return self._getWordPart('endings')

    def getBegPart(self):
        '''

        '''
        return self._getWordPart('beginnings')

    def getMedPart(self):
        '''

        '''
        return self._getWordPart('medials')

    def getWord(self, parts):
        '''

        '''
        word = self.getBegPart()
        for i in range(parts - 2):
            word += self.getMedPart()
        word += self.getEndPart()

        return word

    def getChineseWord(self, parts):
        '''

        '''
        word = ''
        for i in range(int(parts/2)):
            word += self.getBegPart()
            word += self.getEndPart()
        
        return word

class EvolveWord(object):
    '''

    '''

    def __init__(self, word):  
        self.word = word
        self.stats = {}
        self.last_type = ''
        self.letter = ''
        self.stats_filname = ''
        self.check = StatsMaker()
        self.gen = CreateWord()
        self.gen.stats_filname = self.stats_filname
        #self.patterns = 

    def deletePart(self):
        '''

        '''

    def insertPart(self):
        '''

        '''
        # get current type
        # self.gen._getWordPart

    def metathesizePart(self):
        '''

        '''
  
    def eliminateRepeats(self, part):
        '''

        '''
        #pattern = ".*[a-zA-Z]{2,}.*"
        pattern = "([a]{2,})"
        regex = re.compile(pattern)
        return re.sub("([a]{1})([a]{1,})","\1", part)

    def assimilatePart(self):
        '''

        '''
        print self.word
        print self.eliminateRepeats(self.word)
        parts = self.check.getWordParts(self.word)    
        print parts
        output = ''
        for (location, part) in parts:
            if part != 'beg' and len(part) >= 2:
            #  for i in range(len(part)):
            #    print "i: %s, part: %s" % (i, part[i])
                output += self.eliminateRepeats(part)
        return output

    def dissimilatePart(self):
        '''

        '''

    def driftPart(self):
        '''

        '''
        # substitue part 2 for all occurances of part 1
        # but do it randomly, not for every letter in word... some percentage/probablity

def _test():
    import doctest, LangGen
    return doctest.testmod(LangGen)

if __name__ == '__main__':
    _test()
