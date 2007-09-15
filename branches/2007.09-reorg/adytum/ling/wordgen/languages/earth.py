from wordgen.languages import Language
from wordgen.syntagmata import Syntagmata

class English(Language):

    def __init__(self):
        super(English, self).__init__()
        self.language = Syntagmata('english')

class Gaelic(Language):

    def __init__(self):
        super(Gaelic, self).__init__()
        self.language = Syntagmata('gaelic')
