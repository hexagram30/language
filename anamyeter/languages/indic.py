class Sanskrit(Language):
    implements(ILanguage)
    def __init__(self):
        super(Sanskrit, self).__init__()
        self.language = Syntagmata('sanskrit')


class Hindi(Language):
    implements(ILanguage)
    def __init__(self):
        super(Hindi, self).__init__()
        self.language = Syntagmata('hindi')
