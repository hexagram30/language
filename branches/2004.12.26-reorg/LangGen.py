import re
import time
import random
import codecs

class UnexpectedParameter(Exception):
    '''
    The parameter passed is not expected.
    '''

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
        self.match = self.regex.search(word)
        if self.match:
            return True
        return False
        
class UTF8File(object):
    
    def __init__(self, filename, mode):
        self.filename = filename
        self.fh = codecs.open(filename, mode, 'utf-8')
    
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
    '''
        
class GenerateStats(object):
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
                if len(word) > 2:
                    word = word.strip()
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
        '''

        '''
        return self.vowel.letters

    def getLegalConsonants(self):
        '''

        '''
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
        self.stats = eval(file.read())
        self.max['beginnings'] = self.stats['beginnings'][-1][1]
        self.max['medials'] = self.stats['medials'][-1][1]
        self.max['endings'] = self.stats['endings'][-1][1]

    def __getWordPart(self, type):
        '''
        
        '''
        letter = ''
        seed = random.Random(time.time())
        rand = int(seed.uniform(1, self.max[type]))
        for (start, end, letter) in self.stats[type]:
            if rand >= start and rand <= end:
                break

        check = GenerateStats()
        this_type = check.getLetterType(letter[0])
        #print "this type (%s): %s" % (letter, this_type)
        #print "last type: %s" % (self.last_type)

        if this_type != self.last_type: 
            self.last_type = this_type
            self.letter = letter
        else:
            #print "elsing..."
            self.__getWordPart(type)
        #print "elsed."
        return self.letter  

    def getEndPart(self):
        '''

        '''
        return self.__getWordPart('endings')

    def getBegPart(self):
        '''

        '''
        return self.__getWordPart('beginnings')

    def getMedPart(self):
        '''

        '''
        return self.__getWordPart('medials')

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
        self.check = GenerateStats()
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
        # self.gen.__getWordPart

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
