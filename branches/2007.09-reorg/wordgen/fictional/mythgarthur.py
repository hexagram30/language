from wordgen.languages import Language
from wordgen.syntagmata import Syntagmata

# base languages
afrikaner = Syntagmata('afrikaner')
arabic = Syntagmata('arabic')
chinese = Syntagmata('chinese')
english = Syntagmata('english')
gaelic = Syntagmata('gaelic')
german = Syntagmata('german')
hebrew = Syntagmata('hebrew')
oldenglish = Syntagmata('oldenglish')
oldnorse = Syntagmata('oldnorse')
sanskrit = Syntagmata('sanskrit')

class Orcish(Language):

    def __init__(self):
        super(Orcish, self).__init__()
        self.language = (german * 4 + afrikaner * 8 + oldnorse + 
            arabic * 9 + chinese * 5)

class Elvish(Language):

    def __init__(self):
        super(Elvish, self).__init__()
        self.language = (oldnorse * 5 + oldenglish * 2)

class Human(Language):

    def __init__(self):
        super(Human, self).__init__()
        self.language = (oldenglish + oldnorse * 2 + sanskrit * 6 + 
            english * 5)

class Dwarvish(Language):

    def __init__(self):
        super(Dwarvish, self).__init__()
        self.language = (gaelic * 5 + hebrew * 4 + german)
