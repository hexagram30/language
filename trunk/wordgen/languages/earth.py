from wordgen.languages import Language
from wordgen.syntagmata import Syntagmata

class English(Language):

    def __init__(self):
        self.language = Syntagmata('english')
