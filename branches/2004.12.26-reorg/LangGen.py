from string import split, join, find, lower, strip
from random import Random
import time
import re
import codecs

class GenerateStats:
  '''

  '''

  def __init__(self, vowels=None, consonants=None):
    if not vowels:
        vowels = 'aeiouy'
    if not consonants:
        consonants = 'bcdfghjklmnpqrstvwxz'
    self.source_filename = ''
    self.legal_filename = ''
    self.list_filname = ''
    self.stats_filname = ''
    self.freqs = {}
    self.totals = {}
    self.wordlist = []
    self.illegals = {}
    #self.word_pattern = "^[a-zA-Z-']+$"
    self.word_pattern = "^[%s%s]+$" % (vowels, consonants)
    self.vowel_pattern = "[%s]+" % vowels
    self.consonant_pattern = "[%s]+" % consonants
    self.stats = {}

  def getWordList(self):
    '''

    '''
    if self.wordlist:
      return self.wordlist
    fh = codecs.open(self.source_filename, 'r', 'utf-8')
    regex = re.compile(self.word_pattern)
    for line in fh.readlines():
      words = split(line)
      for word in words:
        if len(word) > 2:
          word = strip(word)
          if regex.search(word):
            self.wordlist.append(lower(word))
    fh.close()
    #print self.wordlist
    return self.wordlist

  def writeWordList(self):
    '''
    
    '''
    fh = codecs.open(self.list_filname, 'w+', 'utf-8')
    self.wordlist = self.getWordList()
    for word in self.wordlist:
      fh.write(word + "\n")
    fh.close()

  def __getLegals(self, pattern):
    '''

    '''
    regex = re.compile(pattern)
    found = {}
    output = []
    self.wordlist = self.getWordList()
    for word in self.wordlist:
      if len(word) > 2:
        for letter in word:           
          if regex.search(letter):
            if not found.has_key(letter):
              found.setdefault(letter,[]).append(1)

    output = found.keys()
    output.sort()
    return output    

  def getLegalVowels(self):
    '''

    '''
    return self.__getLegals(self.vowel_pattern)

  def getLegalConsonants(self):
    '''
    
    '''
    return self.__getLegals(self.consonant_pattern)

  def sortStats(self, dict):
    '''
    
    '''
    dict = map(None, dict.values(), dict.keys())
    dict.sort()
    dict.reverse()    
    return dict

  def dictCount(self, dict, key):
    '''
    
    '''
    try: dict[key] += 1
    except: dict[key] = 1
    return dict

  def getLetterType(self, letter):
    '''

    '''
    con_regex = re.compile(self.consonant_pattern)
    vow_regex = re.compile(self.vowel_pattern)
    if con_regex.search(letter): 
      type = "con"
      self.totals = self.dictCount(self.totals, 'consonants')
      self.totals = self.dictCount(self.totals, 'letters')
    elif vow_regex.search(letter): 
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
    self.stats = {'beginnings':beginnings, 'medials': medials, 'endings': endings}
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
    fh = codecs.open(self.stats_filename, 'w+', 'utf-8')
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
    fh.write(str(stats))
    fh.close()

class CreateWord:
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
    fh = codecs.open(self.stats_filename, 'r', 'utf-8')
    self.stats = eval(fh.read())
    self.max['beginnings'] = self.stats['beginnings'][-1][1]
    self.max['medials'] = self.stats['medials'][-1][1]
    self.max['endings'] = self.stats['endings'][-1][1]

  def __getWordPart(self, type):
    '''
    
    '''
    letter = ''
    seed = Random(time.time())
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

class EvolveWord:
  '''

  '''

  def __init__(self, word):  
    self.word = word
    self.stats = {}
    self.last_type = ''
    self.letter = ''
    self.stats_filname = ''
    self.check = GenerateStats()
    self.word_pattern = self.check.word_pattern
    self.vowel_pattern = self.check.vowel_pattern
    self.consonant_pattern = self.check.consonant_pattern
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
