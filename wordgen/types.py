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

