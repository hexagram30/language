import re

from anamyeter import exceptions


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
            raise exceptions.UnexpectedParameter
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
