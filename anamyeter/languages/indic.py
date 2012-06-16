class Sanskrit(Language):
    implements(ILanguage)
    iso639 = "san"
    def __init__(self):
        super(Sanskrit, self).__init__()
        self.language = Syntagmata('sanskrit')


class Hindi(Language):
    implements(ILanguage)
    iso639 = "hin"
    def __init__(self):
        super(Hindi, self).__init__()
        self.language = Syntagmata('hindi')
